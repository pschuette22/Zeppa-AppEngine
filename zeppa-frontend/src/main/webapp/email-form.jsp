<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Send Emails</title>

<script src="js/jquery-2.1.4.min.js"></script>
<script type="text/javascript">

function sendEmail() {
    var toAddress = document.getElementById("txtToAddress").value;
    var subject = document.getElementById("txtSubject").value;
    var body = document.getElementById("txtBody").value;
    
    console.log("To Address: " + toAddress);
    console.log("Subject: " + subject);
    console.log("Body: " + body);
    
    var data = {'toAddress': toAddress, 'subject': subject, 'body': body};
    $.post( "/email-form", data, function( resp ) {
    	 	console.log("success");
    	 	console.log(resp);
    	 	document.getElementById("successDiv").innerHTML = "Your email sent successfully";
    	}).fail(function() {
    	    console.log( "error" );
    	    document.getElementById("errorDiv").innerHTML = "Your email was unable to be sent";
    	});
}
</script>
</head>
<body>

	<div>
		<a href="/create-event.jsp">Create an Event</a><br/>
		<a href="/get-events.jsp">View Events</a><br/>
		<a href="/compare-tags.jsp">Compare Tags</a><br/>
	</div><br/><br/>
	<h1>Create an email to send</h1>

	<div id="errorDiv" style="color:red; font-size:18px;"></div>
	<div id="successDiv" style="color:green; font-size:18px;"></div><br/>
	
	<form action="javascript:sendEmail()">
		<div>
			<span>To Email</span><br/>
			<input id="txtToAddress" type="text" name="toAddress" />
		</div><br/>
		<div>
			<span>Subject</span><br/>
			<input id="txtSubject" type="text" name="subject" />
		</div><br/>
		<div>
			<span>Email Body</span><br/>
			<input id="txtBody" type="text" name="body" />
		</div><br/>
		<div>
			<input type="submit" value="Send Email" />
		</div>
		

	</form>
	
</body>
</html>