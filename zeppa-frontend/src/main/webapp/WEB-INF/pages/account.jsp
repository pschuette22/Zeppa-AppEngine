<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->

<%@ page import="com.zeppamobile.common.UniversalConstants" %>
<%@ page import="java.net.URLEncoder" %>
<style>
	/* Spanning the columns */
	.column {
	  float: left;
	  width: 50%;
	  text-align: center; 
	  }
	
	  /* Removing the margin from the last column */
	  .column:last-child {
	    float: right;
	   }
</style>

<script type="text/javascript" src="lib/js/jquery-2.1.4.min.js"></script>
<script type="text/javascript">

$(document).ready(function() {
	parseUserInfo('${userInfo}');
});

function parseUserInfo(userInfo){
	var info = jQuery.parseJSON(userInfo);
	var givenName = info.givenName;
	var familyName = info.familyName;
	var imageURL = info.imageUrl;
	var gender = info.gender;
	var DOB = info.dateOfBirth;
	
	$("#firstNameText").val(givenName);
	$("#lastNameText").val(familyName);
	$("#DOBinput").val(DOB);
	$("#genderField").val(gender);
	
}

function enablePrivaKey() {
    var email = document.getElementById("txtPrivaKeyEmail").value;
        
    var data = {'email': email, 'isEnablePrivaKey': "true"};
	
    $.get( "/account-settings", data, function( resp ) {
	 	console.log("success");
	 	console.log(resp);
	 	document.getElementById("successDiv").innerHTML = "You have successfully enable PrivaKey";
	}).fail(function() {
	    console.log( "error" );
	    document.getElementById("errorDiv").innerHTML = "We were unable to enable PrivaKey";
	});
    
    /*$.post( "/account-settings", data, function( resp ) {
    	 	console.log("success");
    	 	console.log(resp);
    	 	document.getElementById("successDiv").innerHTML = "You have successfully enable PrivaKey";
    	}).fail(function() {
    	    console.log( "error" );
    	    document.getElementById("errorDiv").innerHTML = "We were unable to enable PrivaKey";
    	});*/
}

</script>

<t:ZeppaBase>
	<jsp:attribute name="title">
	  <h2>Account Settings</h2>
	</jsp:attribute>
	<jsp:body>
	  <table>
   		<tr>
   			<td>First Name</td>
   			<td>Last Name</td>
   		</tr>
   		<tr>
   			<td><input type="text" id="firstNameText"/></td>
   			<td><input type="text" id="lastNameText"/></td>
   		</tr>
   		<tr>
   			<td>Date of Birth</td>
   			<td>Gender</td>
   		</tr>
   		<tr>
   			<td><input type="text" id="DOBinput"/></td>
   			<td><input type="text" id="genderField"/></td>
   		</tr>
   		<tr>   		
      	  <td width="50%">
            <h3><u>User Settings</u></h3>
  			Username:<br>
			<input type="text" name="firstname"><br><br>
			Email Address:<br>
			<input type="text" name="lastname"><br><br>
			<u>Change Password</u><br><br>
			Enter Current Password:<br>
			<input type="text" name="current_password"><br>
			Enter New Password:<br>
			<input type="text" name="new_password"><br>
			Re-Enter Current Password:<br>
			<input type="text" name="retype_password">
      	  </td>
   		</tr>
   		<tr>
   			<td>
   			PrivaKey Email Address
   			</td>
   		</tr>
   		<tr>
   			<td>
   				<input type="text" id="txtPrivaKeyEmail">
   			</td>
   		</tr>
		<tr>
   			<td>
   				<input type="button" value="Enable PrivaKey" onClick="javascript:enablePrivaKey()" ><br/>
   				<div id="successDiv" style="color: green;"></div>
				<div id="errorDiv" style="color: red;"></div>
   			</td>
   		</tr>
	  </table>
	  <div id="buttonDiv" style="width:60%;text-align:center"><input type="button" value="Submit"></div>
	</jsp:body>
</t:ZeppaBase>