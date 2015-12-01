<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Zeppa, Display Events</title>
</head>
<body>

	<div>
		<a href="/create-event.jsp">Create an Event</a><br/>
		<a href="/email-form.jsp">Send Email</a><br/>
		<a href="/compare-tags.jsp">Compare Tags</a><br/>
	</div><br/><br/>
	
	<h1>Display Events From the Database</h1>

	<form action="/get-events" method="get">
		<div>
			<input type="submit" value="Get Events" />
		</div>
		
		<p>${message}</p>

	</form>

</body>
</html>