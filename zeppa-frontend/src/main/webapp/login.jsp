<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="google-signin-client_id" content="9207387879-274c5k1el52drcg0ou6l0josl5klbv8s.apps.googleusercontent.com">
<title>Zeppa, Comare Tags</title>

<script src="https://apis.google.com/js/platform.js?onload=renderButton" async defer></script>
<script src="js/jquery-2.1.4.min.js"></script>
<script type="text/javascript">
$( document ).ready(function() {
    console.log( "ready!" );
});
/*function onSignIn(googleUser) {
	  var profile = googleUser.getBasicProfile();
	  console.log(profile);
	  console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
	  console.log('Name: ' + profile.getName());
	  console.log('Image URL: ' + profile.getImageUrl());
	  console.log('Email: ' + profile.getEmail());
	}*/
function onSuccess(googleUser) {
	var profile = googleUser.getBasicProfile();
    console.log(googleUser);
    console.log(googleUser.po.access_token);
    var access_token = googleUser.po.id_token;
    var data = {'token': access_token};
    $.post( "/login", data, function( resp ) {
    	 	console.log("success");
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
</head>
<body>

	<h1>Login</h1>		
	<div id="my-signin2" class="g-signin2"></div>


</body>
</html>