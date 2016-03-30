<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->

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
	  </table>
	  <div id="buttonDiv" style="width:60%;text-align:center"><input type="button" value="Submit"></div>
	</jsp:body>
</t:ZeppaBase>