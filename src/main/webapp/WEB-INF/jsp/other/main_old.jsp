<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SHAP</title>
<style type="text/css" title="currentStyle">
	@import "<c:url value='/media/css/smoothness/jquery-ui-1.8.5.custom.css'/>";
	@import "<c:url value='/media/css/table_jui.css'/>";
	@import "<c:url value='/media/css/table.css'/>";
	@import "<c:url value='/media/css/app.css'/>";
</style>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-1.4.2.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-ui-1.8.5.custom.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.validate.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.dataTables.js"/>'></script>
<script type="text/javascript">

function getSessionItemCount() {
	$.post("<c:url value='/app/bench/ajax_count'/>", 
			function(data) {
				$("#app_session").html("Selected " + data + " items");
			}
	);
}

$(function() {

	$("#queryText").focus();
	
	getSessionItemCount();
	
	/*Navigation bar*/
	$("#app_content").tabs(1);

	/* Search Tab validation and submission */
	$("#searchQuery").validate({
		rules: {
			queryText: {
				required: true,
				remote: "<c:url value='/app/search/ajax_validate'/>"
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
			$.post("<c:url value='/app/search/ajax_query'/>",
				$("#searchQuery").serialize(),
				function(data) {$("#app_result").html(data);}
			);
			getSessionItemCount();
			}
	});
});
</script>
</head>
<body>
<div id="app_container" class="clearfix">

<div id="app_header">SHAP Main</div>
<div class="app_bar ui-widget-header ui-corner-all " style="border-bottom: none"></div>

<div id="app_content" class="clearfix">

	<div id="app_session"></div>
	
	<ul id="nav" class="anchors">
		<li><a href="#search">Search</a></li>
		<li><a href="#fetch">Fetch</a></li>
		<li><a href="#browse">Browse</a></li>
		<li><a href="#bench">Workbench</a></li>
	</ul>

	<div id="search" class="tabContent">
		<div id="app_form">
			<h3>Search the system</h3>
			<form id="searchQuery">
				<input id="queryText" name="queryText" size="40" tabindex="1" value="${queryText}"/>
				<input type="submit" tabindex="2" value="Search"/>
				<div id="errors"></div>
			</form>
		</div>
		<div id="app_result" class="dynamic" ></div>
	</div>

	<div id="fetch" class="tabContent">
	<h3 style="text-align: center;">Not implemented</h3>
	</div>

	<div id="browse" class="tabContent">
	<h3 style="text-align: center;">Not implemented</h3>
	</div>
	
	<div id="bench" class="tabContent">
	<h3 style="text-align: center;">Not implemented</h3>
	</div>

</div> <!-- app_content -->
<div class="app_bar ui-widget-header ui-corner-all" style="border-top: none"></div>

</div>
</body>
</html>
