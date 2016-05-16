<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<head>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<title>Index Tag Example</title>
</head>
<body>

	<%
		String tagText = request.getParameter("tag-text");
		if (tagText == null) {
			tagText = "";
		}
	%>
	
	<h1>Index Tag Example</h1>

	<form action="/index-tag-example" method="post">
		<div>
			<input id="tag-text" type="text" name="tag-text" value="${fn:escapeXml(tagText)}" />
		</div>
		<br />
		<div>
			<input type="submit" value="Index Tag" />
		</div>

	</form>



</body>
</html>