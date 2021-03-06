<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->

<link rel="stylesheet" href="lib/css/bootstrap-datetimepicker.min.css" />
<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script src="../js/Chart.js/Chart.js"></script>
<script type="text/javascript" src="lib/js/moment.js"></script>
<script type="text/javascript" src="lib/js/transition.js"></script>
<script type="text/javascript" src="lib/js/collapse.js"></script>
<script type="text/javascript" src="lib/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="lib/js/bootstrap-datetimepicker.min.js"></script>
<link rel="stylesheet" href="lib/css/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="lib/css/bootstrap/css/bootstrap-theme.min.css" />
<link rel="stylesheet" href="lib/css/bootstrap-datetimepicker.min.css" />

<script>
var tagQueue = [];
var eventID = -1;
var genderChart = -1;
var ageChart = -1;
var tagChart = -1;
var watchedChart = -1;
$( document ).ready(function() {
	$('#startTime').datetimepicker();
	$('#endTime').datetimepicker();
	
	$(".mainTabNav").click(function(){
		//Styling stuff (changing colors and hiding/showing)
		$(".eventTab").removeClass("active");
		var tabID = $(this).data("tab");
		$("#"+tabID).addClass("active");
		$(".mainTabNav").removeClass("active");
		$(this).addClass("active");
	});
	$(".tabNav").click(function(){
		//Styling stuff (changing colors and hiding/showing)
		$(".analyticsTab").removeClass("active");
		var tabID = $(this).data("tab");
		$("#"+tabID).addClass("active");
		$(".tabNav").removeClass("active");
		$(this).addClass("active");
	});
	
	parseEventInfo('${eventInfo}');
	parseTags('${tags}');
	createGraphs();
	populateAgeDropdowns();

	// When the filter button is clicked, get updated info without refreshing
 	$("#filterButton").click(function() {
 		var min = $('#minAgeFilter').val();
 		var max = $('#maxAgeFilter').val();
 		var gen = $('#genderFilter').val();
 		var id = $('#eventIdInput').val();
 		var dataObj = {'event-id':eventID, 'minAge':min, 'maxAge':max, 'gender':gen };
 	    
 		$.ajax({
            url : '/individual-event',
            tpye: "GET",
            data : dataObj,
            success : function(data, status, xhr) {
				var newGen =eval("("+xhr.getResponseHeader('genderGraph')+")");
				var newAge = eval("("+xhr.getResponseHeader('ageGraph')+")");
				var newTag = eval("("+xhr.getResponseHeader('tagGraph')+")");
				var newWatch = eval("("+xhr.getResponseHeader('tagWatchGraph')+")");
            	recreateGraphs(newGen, newAge, newTag, newWatch);
            }
        });
 	});
	
	$("input, textarea").each(function(){
		$(this).attr("readonly",true);
	});
	if($("#startTime").data("DateTimePicker").date().toDate().getTime() < Date.now()){
		$("#editButtonRow").hide();
	}
	$("#newTagBtn").click(function(){
		postTag();
	});
});

var options = {
		//responsive: true,
		animationEasing: "easeOutQuart"
	};
	
	function createGraphs() {
		// Get the context of the canvas element we want to select
		var genderCtx = document.getElementById("gender").getContext("2d");
		var ageCtx = document.getElementById("age").getContext("2d");
		var tagsContext = document.getElementById("popularTags").getContext("2d");
		var tagsWatchedContext = document.getElementById("watchedTags").getContext("2d");
		genderChart = new Chart(genderCtx).Doughnut(${genderData}, options);
		ageChart = new Chart(ageCtx).Bar(${ageData}, options);
		tagChart = new Chart(tagsContext).Bar(${tagData}, options);
		watchedChart = new Chart(tagsWatchedContext).Bar(${watchedTagData}, options);
	}
	
	function recreateGraphs(gen, age, tag, tagWatch) {
		// Get the context of the canvas element we want to select
		var genderCtx = document.getElementById("gender").getContext("2d");
		var ageCtx = document.getElementById("age").getContext("2d");
		var tagsContext = document.getElementById("popularTags").getContext("2d");
		var tagsWatchedContext = document.getElementById("watchedTags").getContext("2d");
		// Destroy previous charts
		if(genderChart != -1) {
			genderChart.destroy();
		}
		if(ageChart != -1) {
			ageChart.destroy();
		}
		if(tagChart != -1) {
			tagChart.destroy();
		}
		if(watchedChart != -1) {
			watchedChart.destroy();
		}
		genderChart = new Chart(genderCtx).Doughnut(gen, options);
		ageChart = new Chart(ageCtx).Bar(age, options);
		tagChart = new Chart(tagsContext).Bar(tag, options);
		watchedChart = new Chart(tagsWatchedContext).Bar(tagWatch, options);
	}
	
function makeEditable(){
	$("input,textarea").each(function(){
		$(this).attr("readonly",false);
	});
	$("#newTagRow").show();
	$("#submitButtonRow").show();
	$("#editButtonRow").hide();

	$("body").on("click", ".tag", function(){
		$(this).toggleClass("active");
		if(!$(this).hasClass("active")){
			var index = tagQueue.indexOf($(this));
			tagQueue.splice(index,1);
		}else{
			tagQueue.push($(this));
			if (tagQueue.length > 6){
				//popqueue 
				var popped = tagQueue.shift();
				popped.removeClass("active");
			}
		}
	});
}
function postTag(){
	var tagText = $("#tagText").val();
	var data = {'tagText': tagText};
	
	 $.post( "/create-tag", data, function( resp ) {
 	 	var tagInfo = jQuery.parseJSON(resp);
 	 	var id = tagInfo.id;
 	 	var text = tagInfo.tagText;
 	 	$("#tagsContainer").append("<div class='tag' data-tagid='"+id+"'>"+text+"</div>");
 	 	$("#tagsContainer").find(".tag:last").click();
 	}).fail(function() {
 	    console.log( "error" );
 	});
	
}
function editEvent() {
	var title = $("#title").val();
	var start = $("#startTime").data("DateTimePicker").date().toDate().getTime();
	var end = $("#endTime").data("DateTimePicker").date().toDate().getTime();
	var address = $("#addressInput").val();
	var description = $("#description").val();
	var tags = [];
	$(".tag.active").each(function(){
		tags.push({
			"tagText":$(this).html(),
			"id":$(this).data("tagid")
		});
	})
	tags = JSON.stringify(tags);
    var data = {'title': title, 'start': start, 'end': end,
    		'address': address, 'description':description, 'tag-list':tags, 'event-id':eventID };
    
    $.post( "/individual-event", data, function( resp ) {
    	 	console.log("success");
    	 	console.log(resp);
    	 	location.reload();
    	}).fail(function() {
    	    console.log( "error" );
    	});
}

function parseTags(tagsString){
	var tags = jQuery.parseJSON(tagsString);
	for (var i = 0; i < tags.length; i++){
		var text = tags[i].tagText;
		var id = tags[i].id;
		$("#tagsContainer").append("<div class='tag active' data-tagid='"+id+"'>"+text+"</div> ");
		tagQueue.push($(".tag").last());
	}
}

function parseEventInfo(eventString){
	var eventInfo = jQuery.parseJSON(eventString);
	var title = eventInfo[0].title;
	var desc = eventInfo[0].description;
	var location = eventInfo[0].displayLocation;
	var start = getDateString(eventInfo[0].start);
	var end = getDateString(eventInfo[0].end);
	eventID = eventInfo[0].id;
	$("#startTime").data('DateTimePicker').date(new Date(eventInfo[0].start));
	$("#endTime").data('DateTimePicker').date(new Date(eventInfo[0].end));
	$("#title").val(title);
	$("#description").val(desc);
	$("#addressInput").val(location);	
}

function getDateString(date) {
	var start = new Date(date);
	var date = start.getDate();
    var month = start.getMonth() + 1; //Months are zero based
    var year = start.getFullYear();
    var hour = start.getHours();
    var minutes = start.getMinutes();
    if (minutes < 10){
    	minutes = "0"+minutes
    }
    var ampm = "AM";
    if (hour>11){
    	hour = hour-12;
    	ampm = "PM";
    }
    if (hour == 0 ){
    	hour = 12;
    }
    var dateString = month+"/"+date+"/"+year+"\t"+hour+":"+minutes+" "+ampm;
    return dateString;
}

function populateAgeDropdowns() {
	var min = document.getElementById("minAgeFilter");
	var max = document.getElementById("maxAgeFilter");
	var none = document.createElement("option");
	var values = ["None", "under18", "18-20", "21-24", "25-29", "30-39", "40-49", "50-59", "over60"];
	for(var i=0; i<values.length; i++) {
		var opt1 = document.createElement("option");
		var opt2 = document.createElement("option");
		opt1.value = values[i];
		opt2.value = values[i];
		opt1.textContent = values[i];
		opt2.textContent = values[i];
		if(values[i] == "None") {
			opt1.selected = "selected";
			opt2.selected = "selected";
		}
		min.appendChild(opt1);
		max.appendChild(opt2);
	}
}
</script>


<script type="text/javascript">
      // This sample uses the Place Autocomplete widget to allow the user to search
      // for and select a place. The sample then displays an info window containing
      // the place ID and other information about the place that the user has
      // selected.

      // This example requires the Places library. Include the libraries=places
      // parameter when you first load the API. For example:
      // <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">

      function initMap() {
        var map = new google.maps.Map(document.getElementById('map'), {
        	center: {lat: 39.9540, lng: -75.1880},
          zoom: 13
        });

        var input = document.getElementById('addressInput');

        var autocomplete = new google.maps.places.Autocomplete(input);
        autocomplete.bindTo('bounds', map);

        map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

        var infowindow = new google.maps.InfoWindow();
        var marker = new google.maps.Marker({
          map: map
        });
        marker.addListener('click', function() {
          infowindow.open(map, marker);
        });

        autocomplete.addListener('place_changed', function() {
          infowindow.close();
          var place = autocomplete.getPlace();
          if (!place.geometry) {
            return;
          }

          if (place.geometry.viewport) {
            map.fitBounds(place.geometry.viewport);
          } else {
            map.setCenter(place.geometry.location);
            map.setZoom(17);
          }

          // Set the position of the marker using the place ID and location.
          marker.setPlace({
            placeId: place.place_id,
            location: place.geometry.location
          });
          marker.setVisible(true);

          infowindow.setContent('<div><strong>' + place.name + '</strong><br>' +
              'Place ID: ' + place.place_id + '<br>' +
              place.formatted_address);
          infowindow.open(map, marker);
        });
      }
</script>
<script
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAdzV3Wi4EVmmF34N-cwEEgic4lhj8AvCY&libraries=places&callback=initMap"
	async defer></script>

<style>
.tabsDiv {
	width: 100%;
	margin-bottom: 2em;
}
.tabNav {
	color: rgb(31, 169, 255);
	width: 45%;
	display: inline-block;
	border-top-right-radius: 1em;
	border-top-left-radius: 1em;
	text-align: center;
	border-style: solid;
    border-width: 1px;
    border-color: rgb(31,169,255);
}
.mainTabNav {
	color: rgb(31, 169, 255);
	width: 45%;
	display: inline-block;
	border-top-right-radius: 1em;
	border-top-left-radius: 1em;
	text-align: center;
	border-style: solid;
    border-width: 1px;
    border-color: rgb(31,169,255);
}
.tabNav.active {
	background-color: rgb(31, 169, 255);
	color: #FFF
}
.tabNav:hover {
	background-color: rgb(31, 169, 255);
	color: #FFF;
	cursor: pointer;
}
.mainTabNav.active {
	background-color: rgb(31, 169, 255);
	color: #FFF
}

.mainTabNav:hover {
	background-color: rgb(31, 169, 255);
	color: #FFF;
	cursor: pointer;
}
.analyticsTab {
	display: none;
}
.analyticsTab.active {
	display: block;
}

.eventTab {
	display: none;
}

.eventTab.active {
	display: block;
}

.tag {
	float: left !important;
	border: 1px solid rgb(31, 169, 255) !important;
	border-radius: 3px !important;
	width: auto !important;
	padding: 5px;
	margin: 3px;
	cursor: pointer;
}

.tag.active {
	background-color: rgb(31, 169, 255) !important;
	color: #FFF !important;
	'
}

textarea:focus, input:focus, textarea, input {
	outline: none !important;
}

.label {
	font-weight: bold;
	color: #000;
	margin-top: 5px;
}

.column-right {
/* 	float: right; */
	overflow-y: auto;
	overflow-x: hidden;
	width: 85%;
	height: auto;
}
.column-center { 
		display: inline-block; 
		width: 50%; 
		padding: 25px;
		text-align:center;
}
.column-left { 
		float: left; 
		width: 50%;
		padding: 25px;
		text-align:center;
}

.event-desc {
	text-align: center;
	padding: 10px;
}

.description {
	width: 70%;
	height: 75px;
}

.addressText {
	width: 75%;
}

.picker {
	width: auto;
}

.tag.active {
	background-color: rgb(31, 169, 255) !important;
	color: #FFF !important;
}

#newTagRow {
	display: none;
}

#map {
	height: 300px;
}

.controls {
	background-color: #fff;
	border-radius: 2px;
	border: 1px solid transparent;
	box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
	box-sizing: border-box;
	font-family: Roboto;
	font-size: 15px;
	font-weight: 300;
	height: 29px;
	margin-left: 17px;
	margin-top: 10px;
	outline: none;
	padding: 0 11px 0 13px;
	text-overflow: ellipsis;
	width: 400px;
}

.controls:focus {
	border-color: #4d90fe;
}

.smallButton {
	font-size: 13px !important;
	padding: 5px 10px !important;
}
.filterDiv {
     	width: 40%; 
    	padding-bottom : 1%; /* = width for a 1:1 aspect ratio */
    	margin: 0% 0% 2% 0%;
  	}

#submitButtonRow {
	display: none;
}
</style>
<t:ZeppaBase>
	<jsp:attribute name="title"><h2>Individual Event</h2></jsp:attribute>
	<jsp:attribute name="username">
	 (${userName})
  	</jsp:attribute>
	<jsp:body>
		<div id="mainTabNavBar" class="tabsDiv">
	 		<!-- These comments are required to fix html bug which moves tab down to next line -->
	 		<span class="mainTabNav active" data-tab="eventInfoTab">Event Info</span><!--
	 		--><span class="mainTabNav" data-tab="eventAnalyticsTab">Event Analytics</span>
 		</div>
 		<div id="eventAnalyticsTab" class="eventTab">
			<div class="column-right">
				<div class="filterDiv">
			  	<input type="hidden" id="eventIdInput" name="event-id"
						value="${eventId}" />
				<table style="width: 99%; margin-bottom: 1em">
				  <tr style="width: 99%">
				    <th style="width: 10%; text-align: center">Min Age</th>
				    <th style="width: 10%; text-align: center">Max Age</th>
				    <th style="width: 10%; text-align: center">Gender</th>
				  </tr>
				  <tr style="width: 99%">
		    		<td style="width: 10%; text-align: center">
		    			<select id="minAgeFilter" name="minAge"></select>
		    		</td>
		    		<td style="width: 10%; text-align: center">
		    			<select id="maxAgeFilter" name="maxAge"></select>
		    		</td>
		    		<td style="width: 10%; text-align: center">
		    			<select id="genderFilter" name="gender">
		    				<option value="all">All</option>
		    				<option value="male">Male</option>
		    				<option value="female">Female</option>
		    				<option value="undefined">Undefined</option>
		    			</select>
		    		</td>
		    		<td align="center" colspan="3"
								style="width: 10%; text-align: center">
		    			<input style="margin-top: 10px" class="smallButton"
								id="filterButton" type="button" value="Filter" />
		    		</td>
				  </tr>
				</table>
			</div>
				<div id="tabNavBar" style="margin-bottom: 2em; width: 70%">
		 			<!-- These comments are required to fix html bug which moves tab down to next line -->
		 			<span class="tabNav active" data-tab="demographicsTab">Demographics</span><!--
		 			--><span class="tabNav" data-tab="tagsTab">Tags</span>
	 			</div>
	 			<div class="analyticsTab active" id="demographicsTab">
	 			<p><h3>Event Demographic Analytics</h3></p>
	 				<div class="column-left">
						<canvas id="gender" width="300" height="300"></canvas>
						<div class="event-desc">Attendee Gender Statistics</div>
	 				</div>
					<div class="column-center">
						<canvas id="age" width="300" height="300"></canvas>
						<div class="event-desc">Attendee Age Statistics</div>
					</div>
				</div>
				<div class="analyticsTab" id="tagsTab">
				<p><h3>Event Tag Analytics</h3></p>
					<div class="column-left">
		 				<canvas id="popularTags" width="300" height="300"></canvas>
						<div class="event-desc">Popular Tags Among People who Joined Events</div>
					</div>
					<div class="column-center">
						<canvas id="watchedTags" width="300" height="300"></canvas>
						<div class="event-desc">Popular Tags Among People who Watched Events</div>
					</div>
				</div>
			</div>
		</div>
		<div id="eventInfoTab" class="eventTab active">
			<div style="width: 50%">
				<table>
			    	<tr>
			    		<td><b>Event Title</b></td>
			    		<td><b>Start Time</b></td>
			    		<td><b>End Time</b></td>  
			    	</tr>
			    	<tr>
			    		<td><input type="text" id="title"
							style="display: table-cell;" /></td>
			    		<td><input type='text' class="form-control picker"
							style="display: table-cell;" id='startTime' /></td>
			    		<td><input type='text' class="form-control picker"
							style="display: table-cell;" id='endTime' /></td>
			    	</tr>
			    	<tr>
						<td style="padding-top: 1em"><b>Select Tags</b></td>
					</tr>
			    	<tr id="tagsRow">
			    		<td colspan="3"><div id="tagsContainer"></div></td>
			    	</tr>
			    	<tr>
			    		<td id="newTagRow" colspan="3"><input type='text'
							placeholder='New Tag' id='tagText' /> <input type="button"
							class="smallButton" id="newTagBtn" value="Add Tag" /></td>
			    	</tr>
			    	<tr>
			    		<td style="padding-top: 1em"><b>Description</b></td>    		 
			    	</tr>
			    	 <tr>
			    		<td colspan="3"><textarea class="description"
								id="description"></textarea></td>
			    	</tr>
			    	<tr>
			    		<td style="padding-top: 1em"><b>Address</b></td> 		 
			    	</tr>
			    	<tr>
			    		<td colspan="3">
			    			<input id="addressInput" class="controls" type="text"
							placeholder="Enter a location">
		    				<div id="map"></div>
		    			</td>   		 
			    	</tr>
			    	<tr id="editButtonRow">
						<td style="padding: 10px;"><input type="button" id="editBtn"
							value="Edit" onclick="makeEditable()" /></td>
					</tr> 	
			    	<tr id="submitButtonRow">
						<td style="padding: 10px;"><input type="submit" id="submitBtn"
							onclick="editEvent()" /></td>
					</tr> 	
		    	</table>
		    </div>
	    </div>
	</jsp:body>

</t:ZeppaBase>