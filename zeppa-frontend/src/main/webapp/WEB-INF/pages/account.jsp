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

function enablePrivaKey() {
    var email = document.getElementById("txtPrivaKeyEmail").value;
        
    var data = {'email': email, 'isEnablePrivaKey': "true"};
    
    var url = "https://idp.privakeyapp.com/identityserver/connect/authorize?";
    url += "response_type=id_token";
    url += "&response_mode=form_post";
    url += "&client_id=" + "<%= UniversalConstants.PRIVAKEY_CLIENT_ID %>";
    url += "&scope=openid";
    url += "&redirect_uri=" + "<%= URLEncoder.encode("https://1-dot-zeppa-api-dot-zeppa-cloud-1821.appspot.com/privakey/", "UTF-8") %>";
   	url += "&nonce=" + "<%= URLEncoder.encode("bken123@gmail.com", "UTF-8") %>";
   	url += "&login_hint=" + "<%= URLEncoder.encode("bken123@gmail.com", "UTF-8") %>";
	
   	//window.open(url);
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
      	  <td width="50%">
            <h3><u>Billing Settings</u></h3>
		  	Card Number:<br>
			<input type="text" name="number"><br><br>
			Name on Card:<br>
			<input type="text" name="nameoncard"><br><br>
			Expiration Month:  <select>
			  <option value="one">01</option>
			  <option value="two">02</option>
			  <option value="three">03</option>
			  <option value="four">04</option>
			  <option value="five">05</option>
			  <option value="six">06</option>
			  <option value="seven">07</option>
			  <option value="eight">08</option>
			  <option value="nine">09</option>
			  <option value="ten">10</option>
			  <option value="eleven">11</option>
			  <option value="twelve">12</option>
			</select><br><br>
			Expiration Year:  <select>
			  <option value="sixteen">2016</option>
			  <option value="seventeen">2017</option>
			  <option value="eighteen">2018</option>
			  <option value="nineteen">2019</option>
			  <option value="twenty">2020</option>
			</select><br><br>
			Billing Address:<br>
			<input type="text" name="current_password"><br><br>
			Zip Code:<br>
			<input type="text" name="zip"><br><br>
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