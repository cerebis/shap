<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SHAP</title>
<style type="text/css" title="currentStyle">
	@import "<c:url value='/media/css/smoothness/jquery-ui-1.8.5.custom.css'/>";
	@import "<c:url value='/media/css/demo_table_jui.css'/>";
	@import "<c:url value='/media/css/demo_table.css'/>";
	@import "<c:url value='/media/css/app.css'/>";
</style>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-1.4.2.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-ui-1.8.5.custom.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.validate.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.dataTables.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/app.js"/>'></script>
<script type="text/javascript">
	/* Session objects disabled */
	/*
	var countUrl = "<c:url value='/app/bench/count_ajax'/>";
	var clearAllUrl = "<c:url value='/app/bench/clear_ajax'/>";
	*/
	var sessionTimeoutRedirectUrl = "<c:url value='/app/search'/>";
	
	$(document).ready(function() {
		/*$.get("<c:url value='/app/search/form'/>", function (data) {
			$("#app_dynamic").html(data);
		});*/
	
		/*ajaxSessionItemCount();*/
		/*$("#clearAll").click(clearAll);*/
	
		$('body').ajaxError(handleAjaxSessionTimeout);

		$("#queryText").focus();
		
		/* Search Tab validation and submission */
		$("#searchQuery").validate({
			rules: {
				queryText: {
					required: true,
					remote: "<c:url value='/app/search/validate_ajax'/>"
					}
				},
			messages: {
				queryText: {
					required: "Please enter a search query",
					remote: "Queries must be longer than 1 character and cannot begin with *"
					}
				},
			errorLabelContainer: "#errors",
			submitHandler: function(form) {
				$.post("<c:url value='/app/search/query'/>",
					$("#searchQuery").serialize(),
					function(data) {$("#app_result").html(data);}
				);
				/*ajaxSessionItemCount();*/
				}
		});
	});
</script>
</head>
<body>
<div id="app_container" class="clearfix">

	<div id="app_header">
		<span id="app_title">SHAP Search</span>
		<span id="app_logout"><span class="black_italics">${user.username} : </span><a href="<c:url value='/logout'/>">Logout</a></span>
	</div>
	
	<div class="app_bar ui-widget-header ui-corner-all " style="border-bottom: none"></div>
	
	<div id="app_nav"> 
		<ul>
			<li>Search</li>
			<li><a href="<c:url value='/app/browse'/>">Browse</a></li>
		</ul>
	</div>
	
	<div id="app_content" class="clearfix">
		<!--
		<div id="app_session">
			<b>Session Store: </b><span id="app_session_count"></span>
			<button id="clearAll">Clear All</button>
		</div>
		-->

		<div id="app_form">
			<h3>Search the system</h3>
			<form id="searchQuery">
				<input id="queryText" name="queryText" size="40" tabindex="1" value="${queryText}"/>
				<input type="submit" tabindex="2" value="Search"/>
				<div id="errors"></div>
			</form>
		</div>
		
		<div id="app_result"></div>

	</div> <!-- app_content -->
	
	<div class="app_bar ui-widget-header ui-corner-all" style="border-top: none"></div>

</div>
</body>
</html>
