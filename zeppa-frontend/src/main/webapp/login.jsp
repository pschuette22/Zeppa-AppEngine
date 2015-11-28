<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="google-signin-client_id" content="9207387879-274c5k1el52drcg0ou6l0josl5klbv8s.apps.googleusercontent.com">
<title>Zeppa, Comare Tags</title>

<script src="https://apis.google.com/js/platform.js" async defer></script>

<script type="text/javascript">
function onSignIn(googleUser) {
	  var profile = googleUser.getBasicProfile();
	  console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
	  console.log('Name: ' + profile.getName());
	  console.log('Image URL: ' + profile.getImageUrl());
	  console.log('Email: ' + profile.getEmail());
	}
</script>
</head>
<body>

	<h1>Comare Tags with Zeppa Smartfollow</h1>

	<form action="/compare-tags" method="post">
	
		<div class="g-signin2" data-onsuccess="onSignIn"></div>

	</form>


</body>
</html>