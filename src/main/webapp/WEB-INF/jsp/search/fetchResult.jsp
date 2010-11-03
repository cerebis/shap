<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Find By Id</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-ui-custom.min.js"/>'></script>
<style type="text/css" title="currentStyle">
	@import "<c:url value='/media/css/demo_page.css'/>";
	@import "<c:url value='/media/css/demo_table.css'/>";
	@import "<c:url value='/media/css/ui-lightness/jquery-ui-1.8.4.custom.css'/>";
	@import "<c:url value='/media/css/app.css'/>";
</style>
<script type="text/javascript">
$(function() {
	$("input:submit").button();
});
</script>
<style>
#app_form {
	background: #ddddee;
	padding: 20px;
	float: left;
	border: 2px solid #aaaacc;
}
#app_form div {
	margin-bottom: 0.7em;
}
input {
	margin: 0;
	border: 1px solid #aaaacc;
}
#radio span {
	margin-right: 1em;
	vertical-align: top;
}
#radio label {
	margin-right: 1em;
	vertical-align: top;
}
textarea {
	padding: 4px;
	border: 1px solid #aaaacc;
}
#sub {
float: right;
}
</style>
</head>
<body id="dt_example">
	<div id="container">
		<div class="full_width big"><i>Search</i></div>
		<h1>Find By Id</h1>
		<p>Lookup sequences or features by their identifier.</p>
		<p>Identifiers should be all of one type and one per line.</p>
		<div id="app_form">
			<c:url value="/app/search/id" var="formUrl"/>
			<form:form commandName="findByIdQuery" action="${formUrl}" enctype="multipart/form-data">
				<form:radiobuttons path="target" items="${targetTypes}"/>
				<form:textarea path="textIds" cols="40" rows="5"/>
				<input type="file" name="fileIds" size="10"/>
				<input type="submit" value="Search"/>
				<form:errors path="*"/>
			</form:form>
		</div>
	</div>
</body>
</html>
