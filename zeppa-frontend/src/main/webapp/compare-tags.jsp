<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Zeppa, Comare Tags</title>
</head>
<body>

	<h1>Comare Tags with Zeppa Smartfollow</h1>

	<form action="/compare-tags" method="post">
		<div>
			<input type="text" name="tag1" />
		</div>
		<div>
			<input type="text" name="tag2" />
		</div>
		<div>
			<input type="submit" value="Compare Tags" />
		</div>
		
		<p>${message}</p>

	</form>


</body>
</html>