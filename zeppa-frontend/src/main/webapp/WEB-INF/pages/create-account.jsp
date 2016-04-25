<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<script src="https://apis.google.com/js/platform.js?onload=renderButton" async defer></script>

<meta name="google-signin-client_id" content="<%= com.zeppamobile.common.UniversalConstants.WEB_CLIENT_ID %>">


<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script type="text/javascript">

function onSuccess(googleUser) {
	var profile = googleUser.getBasicProfile();
    console.log(googleUser);
    console.log(profile);
    //console.log(googleUser.hg.access_token);
    var access_token = googleUser.hg.id_token;
    //var data = {'token': access_token, 'email': profile.email};
    toggleAccountInfo(profile.getGivenName(), profile.getFamilyName(), profile.getEmail())
    /*$.get( "/create-account", data, function( resp ) {
    	 	console.log("success");
    	}).fail(function() {
    	    console.log( "error" );
    	});*/
}

  function onFailure(error) {
    console.log(error);
  }
  
  function renderButton() {
    gapi.signin2.render('my-signin2', {
      'scope': 'https://www.googleapis.com/auth/userinfo.email',
      'call-to-action': 'Sign up with Google',
      'onsuccess': onSuccess,
      'onfailure': onFailure
    });
  }
  
function toggleAccountInfo(firstName, lastName, email)
{
    document.getElementById("txtFirst").value = firstName;
    document.getElementById("txtLast").value = lastName;
    document.getElementById("txtEmail").value = email;
    
    document.getElementById("divGoogleSignUp").style.display = "none";
    document.getElementById("divAccountInfo").style.display = "block";
}

function createAccount() {
    var firstName = document.getElementById("txtFirst").value,
    lastName = document.getElementById("txtLast").value,
    emailAddress = document.getElementById("txtEmail").value,
    companyName = document.getElementById("txtCompany").value,
    addressLine1 = document.getElementById("txtAddressLine1").value,
    addressLine2 = document.getElementById("txtAddressLine2").value,
    city = document.getElementById("txtCity").value,
    state = document.getElementById("ddlState").value,
    zipcode = document.getElementById("txtZipcode").value,
    password1 = document.getElementById("txtPassword1").value,
    password2 = document.getElementById("txtPassword2").value;
        
    var data = {'firstName': firstName, 'lastName': lastName, 'emailAddress': emailAddress,
    		'companyName': companyName, 'addressLine1': addressLine1, 'addressLine2': addressLine2,
    		'city': city, 'state': state, 'zipcode': zipcode, 'password': password1};
    
    console.log("Account Info: ", data);
    $.post( "/create-account", data, function( resp ) {
    	 	console.log("success");
    	 	console.log(resp);
    	 	console.log("Forbidden: " + resp.forbidden);
    	 	var obj = JSON.parse(resp);
    	 	if(obj.forbidden){
    	 		document.getElementById("errorDiv").innerHTML = "An account with this email address has already been created. Please use another email address or login.";
    	 	}
    	 	else if(obj.redirectURL != null && obj.redirectURL.length > 0){
    	 		window.location = obj.redirectURL;
    	 		//document.getElementById("successDiv").innerHTML = "Your account was added successfully";
    	 	}
    	}).fail(function() {
    	    console.log( "error" );
    	    document.getElementById("errorDiv").innerHTML = "We were unable to create your account";
    	});
}
</script>
<t:ZeppaLoggedOut>
	<jsp:attribute name="title">
	  <h2>Create Account</h2>
	</jsp:attribute>
<jsp:body>
	<div id="divGoogleSignUp">
		<div id="my-signin2" class="g-signin2"></div>
	</div>
	<div id="divAccountInfo" style="display:none;">
		<div id="successDiv" style="color: green;"></div>
		<div id="errorDiv" style="color: red;"></div>
	    <table style="width:600px;">
	    	<tr>
	    		<td>First Name</td>
	    		<td>Last Name</td>
	    		<td>Email Address</td>
	    	</tr>
	    	<tr>
	    		<td><input type="text" id="txtFirst" /></td>
	    		<td><input type="text" id="txtLast" /></td> 
	    		<td><input type="text" id="txtEmail" value="${email}" /></td>
	    	</tr>
	  	    <tr>
	    		<td>Company Name</td>
	    		<td>Address Line 1</td>
	    		<td>Address Line 2</td>	    		 
	    	</tr>
	    	 <tr>
	    		<td><input type="text" id="txtCompany" /></td>
	    		<td><input type="text" id="txtAddressLine1" /></td>
	    		<td><input type="text" id="txtAddressLine2" /></td> 
	    	</tr>
 		  	<tr>
	    		<td>City</td>
	    		<td>State</td>
	    		<td>Zip Code</td>	    		 
	    	</tr>
	    	 <tr>
	    		<td><input type="text" id="txtCity" /></td>
	    		<td><input type="select" id="ddlState" /></td>
	    		<td><input type="text" id="txtZipcode" /></td> 
	    	</tr>
	   	  	<tr>
	    		<td>Password</td>
	    		<td>Re-Enter Password</td>	    		 
	    	</tr>
	    	 <tr>
	    		<td><input type="text" id="txtPassword1" /></td>
	    		<td><input type="text" id="txtPassword2" /></td>
	    	</tr>
	    	<tr><td style="padding:10px;"><input type="submit" onClick="javascript:createAccount()" /></td></tr> 
	    </table>
    </div>
</jsp:body>
</t:ZeppaLoggedOut>