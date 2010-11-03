<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Find Features Result</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css" title="currentStyle">
	@import "<c:url value='/media/css/demo_page.css'/>";
	@import "<c:url value='/media/css/demo_table.css'/>";
	@import "<c:url value='/media/css/app.css'/>";
</style>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.dataTables.js"/>'></script>
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#results').dataTable( {
		"sPaginationType": "full_numbers" } );
} );
</script>
</head>
<body id="dt_example">
	<div id="container">
		<div class="full_width big"><i>Search Results</i></div>
		<h1>Located the following entities</h1>
		<div class="demo">
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="results">
				<thead><tr>
				<th>Id</th>
				<th>Record Type</th>
				</tr></thead>
				<tbody>
					<c:forEach items="${projects}" var="it" varStatus="status">
					<c:url var="entityURL" value="/app/browse/project/${it.id}"/>
					<c:choose>
					<c:when test="${status.count % 2 == 0}"><tr class="even"></c:when>
					<c:otherwise><tr class="odd"></c:otherwise>
					</c:choose>
					<td><a href="${entityURL}">${it.id}</a></td>
					<td>Project</td>
					</tr>
					</c:forEach>
					<c:forEach items="${samples}" var="it" varStatus="status">
					<c:url var="entityURL" value="/app/browse/project/${it.project.id}/sample/${it.id}"/>
					<c:choose>
					<c:when test="${status.count % 2 == 0}"><tr class="even"></c:when>
					<c:otherwise><tr class="odd"></c:otherwise>
					</c:choose>
					<td><a href="${entityURL}">${it.id}</a></td>
					<td>Sample</td>
					</tr>
					</c:forEach>
					<c:forEach items="${sequences}" var="it" varStatus="status">
					<c:url var="entityURL" value="/app/browse/project/${it.sample.project.id}/sample/${it.sample.id}/sequence/${it.id}"/>
					<c:choose>
					<c:when test="${status.count % 2 == 0}"><tr class="even"></c:when>
					<c:otherwise><tr class="odd"></c:otherwise>
					</c:choose>
					<td><a href="${entityURL}">${it.id}</a></td>
					<td>Sequence</td>
					</tr>
					</c:forEach>
					<c:forEach items="${features}" var="it" varStatus="status">
					<c:url var="entityURL" value="/app/browse/project/${it.sequence.sample.project.id}/sample/${it.sequence.sample.id}/sequence/${it.sequence.id}/feature/${it.id}"/>
					<c:choose>
					<c:when test="${status.count % 2 == 0}"><tr class="even"></c:when>
					<c:otherwise><tr class="odd"></c:otherwise>
					</c:choose>
					<td><a href="${entityURL}">${it.id}</a></td>
					<td>Feature</td>
					</tr>
					</c:forEach>
				</tbody>
				<tfoot><tr>
				<th>Id</th>
				<th>Record Type</th>
				</tr></tfoot>
			</table>
		</div>
	</div>
</body>
</html>
