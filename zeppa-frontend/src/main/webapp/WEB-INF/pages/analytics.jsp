<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->
<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="lib/js/moment.js"></script>
<script type="text/javascript" src="lib/js/transition.js"></script>
<script type="text/javascript" src="lib/js/collapse.js"></script>
<script type="text/javascript" src="lib/js/bootstrap.min.js"></script>
<script type="text/javascript" src="lib/js/bootstrap-datetimepicker.min.js"></script>
<link rel="stylesheet" href="lib/css/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" href="lib/css/bootstrap/css/bootstrap-theme.min.css" />
<link rel="stylesheet" href="lib/css/bootstrap-datetimepicker.min.css" />
<script src="../js/Chart.js/Chart.js"></script>

<script type="text/javascript">
var genderChart = -1;
var ageChart = -1;
var popDaysChart = -1;
var popEventsChart = -1;
var popEventsWatchedChart = -1;
var tagChart = -1;
var watchedChart = -1;

	$(document).ready(function(){
		$('#startTimePicker').datetimepicker();
		$('#endTimePicker').datetimepicker();
		
		$(".tabNav").click(function(){
			//Styling stuff (changing colors and hiding/showing)
			$(".analyticsTab").removeClass("active");
			var tabID = $(this).data("tab");
			$("#"+tabID).addClass("active");
			$(".tabNav").removeClass("active");
			$(this).addClass("active");
		});
	});
	
	$(document).ready(function(){
		createGraphs(${genderData}, ${ageData}, ${popDays}, ${popEvents}, ${popEventsWatched}, ${tagData}, ${watchedTagData});
		populateAgeDropdowns();
		
		// When the filter button is clicked, get updated info without refreshing
		$("#filterButton").click(function() {
			var start = $('#startTimePicker').val();
			var end = $('#endTimePicker').val();
			var min = $('#minAgeFilter').val();
	 		var max = $('#maxAgeFilter').val();
	 		var gen = $('#genderFilter').val();
	 		console.log("start: "+start+" end: "+end+" min: "+min+" max: "+" gen: "+gen);
	 		var dataObj = {'startDate':start, 'endDate':end, 'minAge':min, 'maxAge':max, 'gender':gen };
	 	    
	 		$.ajax({
	            url : '/analytics',
	            tpye: "GET",
	            data : dataObj,
	            success : function(data, status, xhr) {
					var newGen =eval("("+xhr.getResponseHeader('genderGraph')+")");
					var newAge = eval("("+xhr.getResponseHeader('ageGraph')+")");
					var newTag = eval("("+xhr.getResponseHeader('tagGraph')+")");
					var newWatch = eval("("+xhr.getResponseHeader('tagWatchGraph')+")");
					var newPopEvents = eval("("+xhr.getResponseHeader('popEventsGraph')+")");
					var newPopEventsWatch = eval("("+xhr.getResponseHeader('popEventsWatchedGraph')+")");
					var newPopDays = eval("("+xhr.getResponseHeader('popDaysGraph')+")");
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
					if(popDaysChart != -1) {
						popDaysChart.destroy();
					}
					if(popEventsChart != -1) {
						popEventsChart.destroy();
					}
					if(popEventsWatchedChart != -1) {
						popEventsWatchedChart.destroy();
					}
	            	createGraphs(newGen, newAge, newPopDays, newPopEvents, newPopEventsWatch, newTag, newWatch);
	            }
	 		});
        });
	});
	
	
	// Override default Chart.js options here
	var options = {
		// Responsive is commented out because it prevents graphs from showing correctly when tab is changed
		//responsive: true,
		animationEasing: "easeOutQuart"
	};
	
	function createGraphs(genParam, ageParam, daysParam, eventsParam, eventsWatchedParam, tagsParam, tagsWatchedParam) {
		// Get the context of the canvas element we want to select
		var genderContext = document.getElementById("gender").getContext("2d");
		var ageContext = document.getElementById("age").getContext("2d");
		var daysContext = document.getElementById("dayOfWeek").getContext("2d");
		var eventsContext = document.getElementById("popularEvents").getContext("2d");
		var eventsWatchedContext = document.getElementById("popularEventsWatched").getContext("2d");
		var tagsContext = document.getElementById("popularTags").getContext("2d");
		var tagsWatchedContext = document.getElementById("watchedTags").getContext("2d");
		if(genParam != "none") {
			genderChart = new Chart(genderContext).Doughnut(genParam, options);
		} else {
			genderContext.font = "12px Arial";
			genderContext.fillText("There is no event data for this vendor", 10,50);
		}
		if(ageParam.labels.length > 0) {
			ageChart = new Chart(ageContext).Bar(ageParam, options);
		} else {
			ageContext.font = "12px Arial";
			ageContext.fillText("There is no event data for this vendor", 10,50);
		}
		if(daysParam.labels.length > 0) {
			popDaysChart = new Chart(daysContext).Bar(daysParam, options);
		} else {
			daysContext.font = "12px Arial";
			daysContext.fillText("There are no events for this vendor", 10,50);
		}
		if(eventsParam.labels.length > 0) {
			popEventsChart = new Chart(eventsContext).Bar(eventsParam, options);
		} else {
			eventsContext.font = "12px Arial";
			eventsContext.fillText("There are no events for this vendor", 10,50);
		}
		if(eventsWatchedParam.labels.length > 0) {
			popEventsWatchedChart = new Chart(eventsWatchedContext).Bar(eventsWatchedParam, options);
		} else {
			eventsWatchedContext.font = "12px Arial";
			eventsWatchedContext.fillText("There are no events for this vendor", 10,50);
		}
		if(tagsParam.labels.length > 0) {
			tagChart = new Chart(tagsContext).Bar(tagsParam, options);
		} else {
			tagsContext.font = "12px Arial";
			tagsContext.fillText("There are no common tags", 10,50);
		}
		if(tagsWatchedParam.labels.length > 0) {
			watchedChart = new Chart(tagsWatchedContext).Bar(tagsWatchedParam, options);
		} else {
			tagsWatchedContext.font = "12px Arial";
			tagsWatchedContext.fillText("No users have watched an event for this vendor",10,50);
		}
	}
	
	function populateAgeDropdowns() {
		var min = document.getElementById("minAgeFilter");
		var max = document.getElementById("maxAgeFilter");
		var none = document.createElement("option");
		none.value = "None";
		none.textContent = "None";
		none.selected = "selected"
		var none2 = document.createElement("option");
		none2.value = "None";
		none2.textContent = "None";
		none2.selected = "selected"
		min.appendChild(none);
		max.appendChild(none2);
		var under18 = document.createElement("option");
		under18.value = "under18";
		under18.textContent = "under18";
		var under18two = document.createElement("option");
		under18two.value = "under18";
		under18two.textContent = "under18";
		min.appendChild(under18);
		max.appendChild(under18two);
		for(var i=18; i < 61; i++) {
			var opt = document.createElement("option");
			opt.value = i;
			opt.textContent = i;
			var opt2 = document.createElement("option");
			opt2.value = i;
			opt2.textContent = i;
			min.appendChild(opt);
			max.appendChild(opt2);
		}
		var over60 = document.createElement("option");
		over60.value = "over60";
		over60.textContent = "over60";
		min.appendChild(over60);
		var over60two = document.createElement("option");
		over60two.value = "over60";
		over60two.textContent = "over60";
		max.appendChild(over60two);
	}
	
</script>

<style>
	#tabNavBar{
 		width:100%;
 	}
 	.tabNav{
 		color: rgb(31,169,255);
 		width: 25%;
 		display:inline-block;
     	border-top-right-radius: 1em;
     	border-top-left-radius: 1em;
     	text-align:center;
     	border-style: solid;
    	border-width: 1px;
    	border-color: rgb(31,169,255);
 	}
 	.tabNav.active{
 		background-color: rgb(31,169,255);
 		color:#FFF
 	}
 	.tabNav:hover{
 		background-color: rgb(31,169,255);
 		color:#FFF;
 		cursor:pointer;
 	}
 	.analyticsTab{
 		display:none;
 	}
 	.analyticsTab.active{
 		display:block;
 	}
 	
	.container {
		overflow-y: auto;
		overflow-x: hidden;
	}	
	.column-left { 
		float: left; 
		width: 33%;
		padding: 25px;
	}
	.column-right { 
		float: right; 
		width: 33%;
		padding: 25px;
	}
	.column-center { 
		display: inline-block; 
		width: 33%; 
		padding: 25px;
	}
	.event-desc {
		text-align: center;
		padding: 10px;
	}
	.filterDiv {
/*     	width: 99%; */
/* 		height: 50px; */
    	padding-bottom : 1%; /* = width for a 1:1 aspect ratio */
    	margin: 2% 0% 1% 0%;
  	}
  	
  	.smallButton{
    	font-size:13px!important;
    	padding:5px 10px!important;
    }
  	
  	form input[type="text"],
  	form select {
  		border-radius: 8px;
		border: solid 1px #eee;
  	}
</style>

<t:ZeppaBase>
	<jsp:attribute name="title">
	  <h2>Analytics</h2>
	</jsp:attribute>
	<jsp:attribute name="username">
	 (${userName})
  	</jsp:attribute>
	<jsp:body>
		<!-- <div id="filters">Filters</div> -->
		<div class="filterDiv">
			<table style="width:70%">
			  <tr style="width:99%">
			    <th style="width:20%; text-align:center">Start Filter Range</th>
			    <th style="width:20%; text-align:center">End Filter Range</th>
			    <th style="width:10%; text-align:center">Min Age</th>
			    <th style="width:10%; text-align:center">Max Age</th>
			    <th style="width:10%; text-align:center">Gender</th>
			  </tr>
			  <tr style="width:99%">
			    <td style="width:15%; text-align:center"><input type='text' style="display:table-cell;" id='startTimePicker' name="startDate"/></td>
	    		<td style="width:15%; text-align:center"><input type='text' style="display:table-cell;" id='endTimePicker' name="endDate"/></td>
	    		<td style="width:10%; text-align:center">
	    			<select id="minAgeFilter" name="minAge"></select>
	    		</td>
	    		<td style="width:10%; text-align:center">
	    			<select id="maxAgeFilter" name="maxAge"></select>
	    		</td>
	    		<td style="width:10%; text-align:center">
	    			<select id="genderFilter" name="gender">
	    				<option value="all">All</option>
	    				<option value="male">Male</option>
	    				<option value="female">Female</option>
	    				<option value="undefined">Undefined</option>
	    			</select>
	    		</td>
	    		<td style="width:10%; text-align:center"><input class="smallButton" id="filterButton" type="button" value="Filter"/></td>
			  </tr>
			</table>
		  </form>
		</div>
 		<div id="tabNavBar">
 			<!-- These comments are required to fix html bug which moves tab down to next line -->
 			<span class="tabNav active" data-tab="demographicsTab">Demographics</span><!--
 			--><span class="tabNav" data-tab="popularDaysTab">Popular Days</span><!--
 			--><span class="tabNav" data-tab="popularEventsTab">Popular Events</span><!--
 			--><span class="tabNav" data-tab="tagsTab">Tags</span>
 		</div>
 		
 		<div class="analyticsTab active" id="demographicsTab">
 			<p><h3>Event Demographics Data</h3></p>
			<div class="container">
				<div class="column-left">
					<canvas id="gender" width="300" height="300"></canvas>
					<div class="event-desc">Total Attendee Gender Breakdown</div>
				</div>
				<div class="column-center">
					<canvas id="age" width="300" height="300"></canvas>
					<div class="event-desc">Total Attendee Age Breakdown</div>
				</div>
			</div>
 		</div>
 		
 		<div class="analyticsTab" id="popularDaysTab">
 			<p><h3>Event Day-of-Week Data</h3></p>
 			<div class="container">
 				<div class="column-center">
 					<canvas id="dayOfWeek" width="300" height="300"></canvas>
					<div class="event-desc">Total Attendee Day-of-Week Breakdown</div>
				</div>
 			</div>
 		</div>
 		<div class="analyticsTab" id="popularEventsTab">
 			<p><h3>Popular Event Data</h3></p>
 			<div class="container">
 				<div class="column-center">
 					<canvas id="popularEvents" width="300" height="300"></canvas>
					<div class="event-desc">Most Joined Events</div>
				</div>
				<div class="column-center">
 					<canvas id="popularEventsWatched" width="300" height="300"></canvas>
					<div class="event-desc">Most Watched Events</div>
				</div>
 			</div>
 		</div>
 		<div class="analyticsTab" id="tagsTab">
 			<p><h3>Popular Tags Data</h3></p>
 			<div class="container">
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
		
	</jsp:body>
</t:ZeppaBase>