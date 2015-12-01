<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Zeppa, Create Event</title>
</head>
<body>

	<h1>Add an Event to the Datastore</h1>

	<form action="/create-event" method="post">
		<div>
			<input type="text" name="eventName" />
		</div><br/>
		<div>
			<input type="submit" value="Create Event" />
		</div>

	</form>


</body>
</html>