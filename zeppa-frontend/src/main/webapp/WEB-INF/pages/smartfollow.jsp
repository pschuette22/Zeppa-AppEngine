<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->
<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Zeppa, Comare Tags</title>
<script src="js/jquery-2.1.4.min.js"></script>
<script type="text/javascript">
function compareTags() {
    var tag1 = document.getElementById("tag1").value;
    var tag2 = document.getElementById("tag2").value;
    
    console.log("Tag 1: " + tag1);
    console.log("Tag 2: " + tag2);
    
    var data = {'tag1': tag1, 'tag2': tag2};
    $.post( "/compare-tags", data, function( resp ) {
    	 	console.log("success");
    	 	console.log(resp);
    	 	document.getElementById("successDiv").innerHTML = resp;
    	}).fail(function() {
    	    console.log( "error" );
    	    document.getElementById("errorDiv").innerHTML = "We were unable to compare these tags";
    	});
}
</script>

</head>

<t:ZeppaBase>
	<jsp:attribute name="title">
	  <h2>Smartfollow</h2>
	</jsp:attribute>
	
	<jsp:body>
		<h1>Compare Tags with Zeppa Smartfollow</h1>
		<div id="errorDiv" style="color:red; font-size:18px;"></div>
		<div id="successDiv" style="color:green; font-size:18px;"></div><br/>
		<form action="javascript:compareTags()">
			<input id="tag1" type="text" name="tag1" /><br />
			<input id="tag2" type="text" name="tag2" /><br />
			<input type="submit" value="Compare Tags" /><br />
			<p>${message}</p>
		</form>
	</jsp:body>
</t:ZeppaBase>

</html>