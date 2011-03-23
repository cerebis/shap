<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SHAP</title>
<style type="text/css" title="currentStyle">
	@import "<c:url value='/media/css/smoothness/jquery-ui-1.8.5.custom.css'/>";
	@import "<c:url value='/media/css/table_jui.css'/>";
	@import "<c:url value='/media/css/table.css'/>";
	@import "<c:url value='/media/css/pagination.css'/>";
	@import "<c:url value='/media/css/app.css'/>";
</style>
<style type="text/css">
body {font-size: 70%;}
div#app_header { font-size: 280%;}
div#app_nav { font-size: 143%;}
label, input { display:block; }
input.text { margin-bottom:12px; width:95%; padding: .4em; }
fieldset { padding:0; border:0; margin-top:25px; }
div#app_content { width: 350px; margin: 20px 0; }
.ui-dialog .ui-state-error { padding: .3em; }
.validateTips { border: 1px solid transparent; padding: 0.3em; }
</style>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-1.4.2.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery-ui-1.8.5.custom.min.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.validate.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.pagination.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/jquery.dataTables.js"/>'></script>
<script type="text/javascript" language="javascript" src='<c:url value="/media/js/app.js"/>'></script>
<script type="text/javascript">

	$(document).ready(function() {
	var tips = $(".validateTips");
	var existingUsers;

	function updateTips( t ) {
		tips
			.text( t )
			.addClass( "ui-state-highlight" );
		setTimeout(function() {
			tips.removeClass( "ui-state-highlight", 1500 );
		}, 500 );
	}

	function checkLength( o, n, min, max ) {
		if ( o.val().length > max || o.val().length < min ) {
			o.addClass( "ui-state-error" );
			updateTips( "Length of " + n + " must be between " +
				min + " and " + max + "." );
			return false;
		} else {
			return true;
		}
	}
	
	function verifyPassword(p, v) {
		if ( p.val() != v.val() ) {
			v.addClass( "ui-state-error" );
			updateTips( "Passwords do not match.");
			return false;
		}
		return true;
	}

	function getSelectedUser() {
		var su = $("#select-form form select option:selected");
		if (su.length == 0) {
			$("#no-user-dialog").dialog("open");
			return null;
		}
		return existingUsers[su.attr("id").substr(4)];
	}

	function fetchExistingUsers() {
		$.getJSON(
			"<c:url value='/app/admin/userlist'/>",
			function(data) {
				existingUsers = data;
				var items = [];
				$.each(data, function(key,val) {
					items.push("<option id='user" + key + "'>" + val.username + "</option>");
				});
				var ulist = $("#select-form form");
				ulist.empty();
				$("<select/>", {size: 10, html: items.join('')}).appendTo(ulist);
			}
		);
	}
	
	function fetchRoles() {
		$.getJSON(
			"<c:url value='/app/admin/rolelist'/>",
			function(data) {
				var items = []
				$.each(data, function(key,val) {
					items.push("<option>" + val + "</option>");
				});
				$(".select-roles").empty().append(items.join(''));
			}
		);
	}
	
	fetchRoles();
	fetchExistingUsers();

		$(".header_action a").button();
		
		$("#no-user-dialog").dialog({
			autoOpen: false,
			height: 150,
			width: 350,
			modal: true,
			buttons: {
				"Ok": function() {
					$(this).dialog("close");
				}
			}
		});
		
		$("#create-form").dialog({
			autoOpen: false,
			height: 430,
			width: 350,
			modal: true,
			buttons: {
				"Create": function() {
					var bValid = true;
					$("#create-form input").removeClass("ui-state-error");
					bValid = bValid && checkLength($("#create-username"), "username", 3, 10);
					bValid = bValid && checkLength($("#create-password"), "password", 6, 16);
					bValid = bValid && verifyPassword($("#create-password"), $("#create-verify"));
					if (bValid) {
						$.ajax({
							type: 'POST',
							url: "<c:url value='/app/admin/create'/>",
							data: $("#create-form form").serialize(),
							success: function(data){
								if (data.statusOk) {
									$("#create-form").dialog("close");
									fetchExistingUsers();
								}
								else {
									updateTips(data.message);
								}
							},
							error: function(jqXHR,textStatus,errorThrown) {
								$("#create-form").dialog("close");
							}
						});
					}
				},
				Cancel: function() {
					$(this).dialog("close");
				}
			},
			close: function() {
				$("#create-form input").val("").removeClass("ui-state-error");
			}
		});
		
		$("#modify-form").dialog({
			autoOpen: false,
			height: 430,
			width: 350,
			modal: true,
			buttons: {
				"Modify": function() {
					var bValid = true;
					$("#modify-form input").removeClass("ui-state-error");
					bValid = bValid && checkLength($("#modify-username"), "username", 3, 10);
					bValid = bValid && checkLength($("#modify-password"), "password", 6, 16);
					bValid = bValid && verifyPassword($("#modify-password"), $("#modify-verify"));
					if (bValid) {
						$.ajax({
							type: 'POST',
							url: "<c:url value='/app/admin/update'/>",
							data: $("#modify-form form").serialize(),
							success: function(data){
								if (data.statusOk) {
									$("#modify-form").dialog("close");
								}
								else {
									updateTips(data.message);
								}
							},
							error: function(jqXHR,textStatus,errorThrown) {
								alert(textStatus);
								$("#modify-form").dialog("close");
							}
						});
					}
				},
				Cancel: function() {
					$(this).dialog("close");
				}
			},
			close: function() {
				$("#modify-form input").val("").removeClass("ui-state-error");
			}
		});
		
		$("#delete-form").dialog({
			autoOpen: false,
			height: 150,
			width: 350,
			modal: true,
			buttons: {
				"Delete": function() {
					$.ajax({
						type: 'GET',
						url: "<c:url value='/app/admin/delete'/>",
						data: "username=" + $("#delete-form #delete-select").text(),
						success: function(data) {
							if (data.statusOk) {
								$("#delete-form").dialog("close");
								fetchExistingUsers();
							}
							else {
								updateTips(data.message);
							}
						},
						error: function(jqXHR,textStatus,errorThrown) {
							alert(textStatus);
							$("#delete-form").dialog("close");
						}
					});
				},
				"Cancel": function() {
					$(this).dialog("close");
				}
			}
		});

		$("#create-user")
			.button()
			.click(function() {
				$("#create-form").dialog("open");
			});

		$("#modify-user")
			.button()
			.click(function() {
				var su = getSelectedUser()
				if (su != null) {
					$("#modify-name").val(su.name);
					$("#modify-username").val(su.username);
					$("#modify-password").val(su.password);
					$("#modify-verify").val(su.password);
					$("#modify-form .select-roles option").each(function() {
						$(this).removeAttr("selected");
						if ($.inArray($(this).text(), su.roles) > -1) {
							$(this).attr("selected","selected");
						}
					});
					$("#modify-form").dialog("open");
				}
			});

		$("#delete-user")
			.button()
			.click(function() {
				var su = getSelectedUser();
				if (su != null) {
					$("#delete-form #delete-select").text(su.username);
					$("#delete-form").dialog("open");
				}
			});

		$("#user-list").selectable();
	});
</script>
</head>
<body>
<sec:authorize access="hasRole('ROLE_ADMIN')">
<div id="app_container" class="clearfix">
	<div id="app_header">
		<div class="header_title">SHAP <span class="header_alert">Admin</span></div>
		<div class="header_action"><a href="<c:url value='/logout'/>"><em>[${user.username}]</em> logout</a></div>
	</div>
	<div class="app_bar ui-widget-header ui-corner-all " style="border-bottom: none"></div>

	<div id="app_nav"> 
		<ul>
			<li><a href="<c:url value='/app/'/>">Search</a></li>
			<li><a href="<c:url value='/app/browse'/>">Browse</a></li>
			<li>Admin</li>
		</ul>
	</div>

	<div id="app_content" class="clearfix">
		<button id="create-user">Create user</button>
		<button id="modify-user">Modify user</button>
		<button id="delete-user">Delete user</button>
		
		<div id="select-form" title="Existing users">
			<h3>Existing Users</h3>
			<form>
			</form>
		</div>
	
		<div id="no-user-dialog" class="text_center" title="No user selected">
			<p>Select a user first.</p>
		</div>
	
		<div id="create-form" title="Create user">
			<p class="validateTips">All form fields are required.</p>
			<form>
				<fieldset>
				<label for="name">Real Name</label>
				<input type="text" name="name" id="create-name" class="text ui-widget-content ui-corner-all" />
				<label for="username">Username</label>
				<input type="text" name="username" id="create-username" value="" class="text ui-widget-content ui-corner-all" />
				<label for="password">Password</label>
				<input type="password" name="password" id="create-password" value="" class="text ui-widget-content ui-corner-all" />
				<label for="verify">Verify Password</label>
				<input type="password" name="verify" id="create-verify" value="" class="text ui-widget-content ui-corner-all" />
				<label for="create-roles">User Role</label>
				<select name="roles" id="create-roles" class="select-roles" multiple="multiple" size="2"></select>
				</fieldset>
			</form>
		</div>

		<div id="modify-form" title="Modify user">
			<p class="validateTips">All form fields are required.</p>
			<form>
				<fieldset>
				<label for="name">Real Name</label>
				<input type="text" name="name" id="modify-name" class="text ui-widget-content ui-corner-all" />
				<label for="username">Username</label>
				<input type="text" name="username" id="modify-username" value="" class="text ui-widget-content ui-corner-all" />
				<label for="password">Password</label>
				<input type="password" name="password" id="modify-password" value="" class="text ui-widget-content ui-corner-all" />
				<label for="verify">Verify Password</label>
				<input type="password" name="verify" id="modify-verify" value="" class="text ui-widget-content ui-corner-all" />
				<label for="modify-roles">User Role</label>
				<select name="roles" id="modify-roles" class="select-roles" multiple="multiple" size="2"></select>
				</fieldset>
			</form>
		</div>
		
		<div id="delete-form" class="text_center" title="Delete user">
			<p>Really delete user <span id="delete-select"></span>?</p>
		</div>
	</div>
	
	<div class="app_bar ui-widget-header ui-corner-all" style="border-top: none"></div>
</div>
</sec:authorize>
</body>
</html>
