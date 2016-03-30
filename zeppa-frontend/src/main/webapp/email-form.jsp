<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<h1>Create an email to send</h1>

	<form action="/email-form" method="post">
		<div>
			<span>To Email</span></br>
			<input type="text" name="toAddress" />
		</div></br>
		<div>
			<span>From Email</span></br>
			<input type="text" name="fromAddress" />
		</div>
		<div>
			<span>Subject</span>
			<input type="text" name="subject" />
		</div>
		<div>
			<span>Email Body</span>
			<input type="text" name="body" />
		</div>
		<div>
			<input type="submit" value="Send Email" />
		</div>
		

	</form>
	
</body>
</html>