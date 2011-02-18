<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
<script type="text/javascript">
$(function() {
	$("#username").focus();
	$("#app_form #login_button").button();
});
</script>
</head>
<body>
<div id="app_container" class="clearfix">

<div id="app_header"><div class="header_title">SHAP <span class="header_comment">Login</span></div></div>
<div class="app_bar ui-widget-header ui-corner-all " style="border-bottom: none"></div>

<div id="app_content" class="clearfix">
	<div id="app_auth" class="ui-widget-content clearfix">
		<c:set var="isAuthenticated" value="${false}"/>
		<sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
			<c:set var="isAuthenticated" value="${true}"/>
		</sec:authorize>
		<c:choose> 
			<c:when test="${isAuthenticated}">
				<div class="inline-div">Username <i><sec:authentication property="principal.username"/></i></div>
				<a id="logout" href="<c:url value='/logout'/>">Logout</a>
			</c:when>
			<c:otherwise> 
				<table id="app_form" class="clearfix">
					<form name="f" action="<c:url value='j_spring_security_check'/>" method="post">
						<tr><td>Username</td><td><input id="username" name="j_username" type="text" size="10" maxlength="10" tabindex="1" value="<c:if test='${not empty param.login_error}'><c:out value='${SPRING_SECURITY_LAST_USERNAME}'/></c:if>"/></td></tr>
						<tr><td>Password</td><td><input id="password" name="j_password" type="password" size="10" maxlength="10" tabindex="2"/></td></tr>
						<tr><td/><td><input id="login_button" name="submit" type="submit" tabindex="3" value="Login"/></td></tr>
					</form>
					<c:if test="${not empty param.login_error}">
						<div class="ui-widget">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em; margin-top: .2em">
								<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 .3em"></span>Login failed</p>
							</div>
						</div>
					</c:if>
				</table>
			</c:otherwise>
		</c:choose>
	</div>
</div>

<div class="app_bar ui-widget-header ui-corner-all" style="border-top: none"></div>

</div> <!-- app_container -->
</body>
</html>
