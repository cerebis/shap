<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
	$(document).ready(function() {
		$(".header_action a").button();
	});
</script>
<div id="app_header">
	<div class="header_title">SHAP <span class="header_comment">Browse</span></div>
	<div class="header_action"><a href="<c:url value='/logout'/>"><em>[${user.username}]</em> logout</a></div>
</div>
