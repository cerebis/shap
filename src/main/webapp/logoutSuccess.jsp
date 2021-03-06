<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SHAP</title>
<meta http-equiv="refresh" content="5;url=<c:url value='/'/>"/>
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
</head>
<body>
<div id="app_container" class="clearfix">

<div id="app_header"><div class="header_title">SHAP <span class="header_comment">Logout</span></div></div>
<div class="app_bar ui-widget-header ui-corner-all " style="border-bottom: none"></div>
	
<div id="app_content" class="clearfix text_center">
	<div id="app_auth">
		<c:set var="isAuthenticated" value="${false}"/>
		<sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
			<c:set var="isAuthenticated" value="${true}"/>
		</sec:authorize>
		<c:choose> 
			<c:when test="${isAuthenticated}">
				<div>Logout failed, you still have an authenticated session.</div>
			</c:when>
			<c:otherwise> 
				<div>You have logged out.</div>
			</c:otherwise>
		</c:choose>
	<div class="text_center">Click <a href="<c:url value='/app/'/>">here</a> if not redirected shortly.</div>
	</div>
</div>

<div class="app_bar ui-widget-header ui-corner-all" style="border-top: none"></div>

</div> <!-- app_container -->
</body>
</html>
