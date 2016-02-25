<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>-->

<style>
  .box {
	width: 90%;
	padding-top: 25px;
	padding-bottom: 25px;
    border: 1px solid rgb(31,169,255);
  }
  table{
    border-spacing: 10px 5px;
  }
  table tr {
	padding-top:5px;
    padding-bottom:5px;
  }
</style>

<t:ZeppaBase>
	<jsp:attribute name="title">
	  <h2>Billing</h2>
	</jsp:attribute>
	
	<jsp:body>
	  <h3 style="color:rgb(31,169,255);text-decoration: underline;">Current Bill</h3>
	  <div class="box">
	    <table width="100%" cellpadding="5px">
	      <tr style="text-decoration: underline;">
	        <th width="18%" align="center">Start Date</th>
	        <th width="18%" align="center">End Date</th>
	        <th width="18%" align="center">Amount Owed</th>
	        <th width="18%" align="center">Due Date</th>
	        <th width="18%" align="center">Paid Date</th>
		  </tr>
		  <tr>
		    <td width="18%" align="center">[Cycle Start Date]</td>
			<td width="18%" align="center">[Cycle End Date]</td>
			<td width="18%" align="center">[Amount owed for cycle]</td>
			<td width="18%" align="center">[Payment Due Date]</td>
			<td width="18%" align="center">[Paid Date]</td>
		  </tr>
	    </table>
	  </div>
	  <h3 style="color:rgb(31,169,255);text-decoration: underline;">Past Bills</h3>
	  <div class="box">
	    <table width="100%" cellpadding="5px">
	      <tr style="text-decoration: underline;">
	        <th width="18%" align="center">Start Date</th>
	        <th width="18%" align="center">End Date</th>
	        <th width="18%" align="center">Amount Owed</th>
	        <th width="18%" align="center">Due Date</th>
	        <th width="18%" align="center">Paid Date</th>
		  </tr>
		  <tr>
		    <td width="18%" align="center">[1.Cycle Start Date]</td>
			<td width="18%" align="center">[1.Cycle End Date]</td>
			<td width="18%" align="center">[1.Amount owed for cycle]</td>
			<td width="18%" align="center">[1.Payment Due Date]</td>
			<td width="18%" align="center">[1.Paid Date]</td>
		  </tr>
		  <tr>
		    <td width="18%" align="center">[2.Cycle Start Date]</td>
			<td width="18%" align="center">[2.Cycle End Date]</td>
			<td width="18%" align="center">[2.Amount owed for cycle]</td>
			<td width="18%" align="center">[2.Payment Due Date]</td>
			<td width="18%" align="center">[2.Paid Date]</td>
		  </tr>
	    </table>
	  </div>
	</jsp:body>
</t:ZeppaBase>