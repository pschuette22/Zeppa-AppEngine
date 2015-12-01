<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Zeppa, Create Event</title>
<script src="js/jquery-2.1.4.min.js"></script>
<script type="text/javascript">

function createEvent() {
    var eventName = document.getElementById("eventName").value;
    
    console.log("Event Name: " + eventName);
    
    var data = {'eventName': eventName};
    $.post( "/create-event", data, function( resp ) {
    	 	console.log("success");
    	 	console.log(resp);
    	 	document.getElementById("successDiv").innerHTML = eventName + " was added successfully";
    	}).fail(function() {
    	    console.log( "error" );
    	    document.getElementById("errorDiv").innerHTML = "We were unable to add this event";
    	});
}
</script>
</head>
<body>

	<div>
		<a href="/email-form.jsp">Send Email</a><br/>
		<a href="/get-events.jsp">View Events</a><br/>
		<a href="/compare-tags.jsp">Compare Tags</a><br/>
	</div><br/><br/>
	
	<h1>Add an Event to the Datastore</h1>

	<div id="errorDiv" style="color:red; font-size:18px;"></div>
	<div id="successDiv" style="color:green; font-size:18px;"></div><br/>
	
	<form action="javascript:createEvent()" >
		<div>
			<input id="eventName" type="text" name="eventName" />
		</div><br/>
		<div>
			<input type="submit" value="Create Event" />
		</div>

	</form>


</body>
</html>