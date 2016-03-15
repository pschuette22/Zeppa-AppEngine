<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->

<link rel="stylesheet" href="lib/css/bootstrap-datetimepicker.min.css" />
<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script>
var tagQueue = [];
$( document ).ready(function() {
	parseEventInfo('${eventInfo}');
	parseTags('${tags}');
	
	/*$("body").on("click", ".tag", function(){
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
	});*/
});

function parseTags(tagsString){
	var tags = jQuery.parseJSON(tagsString);
	for (var i = 0; i < tags.length; i++){
		var text = tags[i].tagText;
		var id = tags[i].id;
		$("#tagsContainer").append("<div class='tag active' data-tagid='"+id+"'>"+text+"</div> ");
		tagQueue.push($("#tagsContainer").last());
	}
}

function parseEventInfo(eventString){
	var eventInfo = jQuery.parseJSON(eventString);
	var title = eventInfo[0].title;
	var desc = eventInfo[0].description;
	var location = eventInfo[0].displayLocation;
	var start = getDateString(eventInfo[0].start);
	var end = getDateString(eventInfo[0].end);
	
	$("#title").val(title);
	$("#description").val(desc);
	$("#location").val(location);
	$("#startTime").val(start);
	$("#endTime").val(start);
	
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
</script>
<style>
	.tag{
    	float:left!important;
    	border: 1px solid rgb(31,169,255)!important;
    	border-radius: 3px!important; 
    	width:auto!important;
    	padding: 5px;
    	margin: 3px;
    	cursor: pointer;
    	
    }
    .tag.active{
    	background-color:rgb(31,169,255)!important;
    	color:#FFF!important;'
    }
    textarea:focus, input:focus,textarea,input{
    	outline: none;
	}
	.label{
		font-weight:bold;
		color:#000;
		margin-top:5px;
	}
</style>
<t:ZeppaBase>
	<jsp:attribute name="title"><h2>Individual Event</h2></jsp:attribute>
	<jsp:body>
		<form id="eventForm" action="post">
			<div style="width:50%">
				<div class="label">Event Title</div>
				<input id="title" type="text" maxlength="255" readonly/>
				<div class="label" >Event Description</div>
				<textarea id="description" readonly></textarea> 
				<div class="label">Location</div>
				<input id="location" type="text" maxlength="255" readonly/>
				<div class="label" >Start Time</div>
				<input type='text' class="form-control picker" id='startTime' readonly/>
				<div class="label">End Time</div>
				<input type='text' class="form-control picker" id='endTime' readonly/><br />
			</div>
			<div class="label">Event Tags</label>
			<div id="tagsContainer"></div>
		</form>
	</jsp:body>
	
	
	
</t:ZeppaBase>