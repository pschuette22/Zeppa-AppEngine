<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@ page import="com.zeppamobile.common.UniversalConstants" %>

<script src="https://apis.google.com/js/platform.js?onload=renderButton" async defer></script>

<meta name="google-signin-client_id" content="<%= UniversalConstants.WEB_CLIENT_ID %>">



<script src="js/jquery-2.1.4.min.js"></script>
<script type="text/javascript">

function login() {
    var email = document.getElementById("txtEmail").value,
    password = document.getElementById("txtPassword").value;
        
    var data = {'email': email, 'password': password};
    
    $.post( "/login", data, function( resp ) {
    	 	console.log("success");
    	 	console.log(resp);
    	 	document.getElementById("successDiv").innerHTML = "Your logged in successfully";
    	}).fail(function() {
    	    console.log( "error" );
    	    document.getElementById("errorDiv").innerHTML = "We were unable to create your account";
    	});
}

function onSuccess(googleUser) {
	var profile = googleUser.getBasicProfile();
    console.log(googleUser);
    //console.log(googleUser.hg.access_token);
    var access_token = googleUser.hg.id_token;
    var data = {'token': access_token};
    $.post( "/login", data, function( resp ) {
    	 	console.log("success");
    	 	console.log(resp);
    	 	if(resp.getResponseHeader('isAuthorized') == "true")
    	 	{
    	 		window.location = "/dashboard";
    	 	}
    	}).fail(function() {
    	    console.log( "error" );
    	});
}

  function onFailure(error) {
    console.log(error);
  }
  function renderButton() {
    gapi.signin2.render('my-signin2', {
      'scope': 'https://www.googleapis.com/auth/userinfo.email',
      'onsuccess': onSuccess,
      'onfailure': onFailure
    });
  }
  
</script>
<t:ZeppaBase>

<jsp:body>
	<h1>Login</h1>		
	<div id="my-signin2" class="g-signin2"></div>
	
	<!--  <div>	
		<div id="successDiv" style="color: green;"></div>
		<div id="errorDiv" style="color: red;"></div>
	    <table style="width:600px;">
	    	<tr>
	    		<td>Email Address</td>
	    		<td>Password</td>
	    	</tr>
	    	<tr>
	    		<td><input type="text" id="txtEmail" /></td>
	    		<td><input type="text" id="txtPassword" /></td>
	    	</tr>
	    	<tr><td style="padding:10px;"><input type="submit" onClick="javascript:login()" /></td></tr> 
	    </table>
    </div> -->
</jsp:body>
</t:ZeppaBase>