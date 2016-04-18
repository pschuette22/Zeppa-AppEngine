<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
    	    document.getElementById("errorDiv").innerHTML = "We were unable to comapre these tags";
    	});
}
</script>

</head>
<body>

<div>
	<a href="/create-event.jsp">Create an Event</a><br/>
	<a href="/email-form.jsp">Send Email</a><br/>
	<a href="/compare-tags.jsp">Compare Tags</a><br/>
</div><br/><br/>

	<h1>Comare Tags with Zeppa Smartfollow</h1>

	<div id="errorDiv" style="color:red; font-size:18px;"></div>
	<div id="successDiv" style="color:green; font-size:18px;"></div><br/>
	
	<form action="javascript:compareTags()">
		<div>
			<input id="tag1" type="text" name="tag1" />
		</div>
		<div>
			<input id="tag2" type="text" name="tag2" />
		</div>
		<div>
			<input type="submit" value="Compare Tags" />
		</div>
		
		<p>${message}</p>

	</form>


</body>
</html>