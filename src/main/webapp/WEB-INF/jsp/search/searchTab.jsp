<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<script type="text/javascript">
	$(document).ready(function() {
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
			errorLabelContainer: "#errors"/*,
			submitHandler: function(form) {
				$.post("<c:url value='/app/search/query_ajax'/>",
					$("#searchQuery").serialize(),
					function(data) {$("#app_result").html(data);}
				);
				ajaxSessionItemCount();
				}*/
		});
	});
</script>

<div id="app_form">
	<h3>Search the system</h3>
	<form id="searchQuery" action="<c:url value='/app/search/query'/>" method="get">
		<input id="queryText" name="queryText" size="40" tabindex="1" value="${queryText}"/>
		<input type="submit" tabindex="2" value="Search"/>
		<div id="errors"></div>
	</form>
</div>
