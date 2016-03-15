<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->

<link rel="stylesheet" href="lib/css/bootstrap-datetimepicker.min.css" />
<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script>
$( document ).ready(function() {
	parseEventInfo('${eventInfo}');
});

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
    if (hour>12){
    	hour = hour-12;
    	ampm = "PM";
    }
    var dateString = month+"/"+date+"/"+year+"\t"+hour+":"+minutes+" "+ampm;
    return dateString;
}
</script>

<t:ZeppaBase>
	<jsp:attribute name="title"><h2>Individual Event</h2></jsp:attribute>
	<jsp:body>
		<form id="eventForm" action="post">
			<div style="width:50%">
				<label for="title">Event Title</label>
				<input id="title" type="text" maxlength="255"/>
				<label for="description">Event Description</label>
				<textarea id="description"></textarea> 
				<label for="location">Location</label>
				<input id="location" type="text" maxlength="255"/>
				<label for="startTimePicker">Start Time</label>
				<input type='text' class="form-control picker" id='startTime' />
				<label for="endTimePicker">End Time</label>
				<input type='text' class="form-control picker" id='endTime' /><br />
				<input id="submitForm" type="submit" name="submit" value="Submit" />
			</div>
		</form>
	</jsp:body>
	
	
	
</t:ZeppaBase>