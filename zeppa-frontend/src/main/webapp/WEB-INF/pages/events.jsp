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
		var location = events[i].displayLocation;
		
		var start = new Date(events[i].start);
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
	    var startTimeString = month+"/"+date+"/"+year+"\t"+hour+":"+minutes+" "+ampm;
		$("#eventsTable tbody").append("<tr class='eventRow' data-eventid='"+id+"'><td>"+title+"</td><td>"+description+"</td><td>"+startTimeString+"</td><td>"+location+"</td><td><a href='/individual-event?event-id="+id+"'>More info</a></td></tr>");

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
	    	<tbody>
		    	<tr>
		    		<td>Event Title</td>
		    		<td>Description</td>
		    		<td>Start Time</td>
		    		<td>Location</td>
		    		<td></td>
		    	</tr>
	    	</tbody>
	    </table>
    </div>
</jsp:body>
</t:ZeppaBase>