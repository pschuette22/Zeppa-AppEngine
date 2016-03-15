<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->

<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script type="text/javascript">
var tagQueue = [];
$(document).ready(function() {
	parseEvents('${allEvents}');
	
	$("body").on("click", ".event", function(){
		$(this).toggleClass("active");
	});
});

function parseEvents(eventsString){
	var events = jQuery.parseJSON(eventsString);
	for (var i = 0; i < events.length; i++){
		var id = events[i].id;
		var title = events[i].title;
		var description = events[i].description;
		var start = events[i].start;
		var location = events[i].displayLocation;
		$("#eventsTable tbody").append("<tr class='eventRow' data-eventid='"+id+"'><td>"+title+"</td><td>"+description+"</td><td>"+start+"</td><td>"+location+"</td><td><a href='/individual-event?event-id="+id+"'>More info</a></td></tr>");
	}
}

</script>
<t:ZeppaBase>
	<jsp:attribute name="title">
	  <h2>Events</h2>
	</jsp:attribute>

<jsp:body>
	<div>
	    <table style="width:60%;" id="eventsTable">
	    	<tr>
	    		<td>Event Title</td>
	    		<td>Description</td>
	    		<td>Start Time</td>
	    		<td>Location</td>
	    		<td></td>
	    	</tr>
	    </table>
    </div>
</jsp:body>
</t:ZeppaBase>