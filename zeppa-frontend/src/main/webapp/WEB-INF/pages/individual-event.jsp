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
	
	$("#title").val(title);
	$("#description").val(desc);
	
}
</script>

<t:ZeppaBase>
	<jsp:attribute name="title"><h2>Individual Event</h2></jsp:attribute>
	<jsp:body>
		<form id="eventForm" action="post">
			<div>
				<label for="title">Event Title</label>
				<input id="title" type="text" maxlength="255"/>
				<label for="title">Event Description</label>
				<textarea id="description"></textarea> 
				<label for="startTimePicker">Start Time</label>
				<input type='text' class="form-control picker" id='startTimePicker' />
				<label for="endTimePicker">End Time</label>
				<input type='text' class="form-control picker" id='endTimePicker' />
			</div>
			<input id="submitForm" type="submit" name="submit" value="Submit" />
		</form>
	</jsp:body>
	
	
	
</t:ZeppaBase>