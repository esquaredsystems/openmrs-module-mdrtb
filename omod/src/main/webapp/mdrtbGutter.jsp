<ul class="navList">
	<li id="homeNavLink" class="firstChild">
		<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbIndex.form"><spring:message code="mdrtb.title.homepage" /></a>
	</li>
	<openmrs:hasPrivilege privilege="View Administration Functions">
		<li><a href="${pageContext.request.contextPath}/admin">
			<spring:message code="Navigation.administration" /></a></li>
	</openmrs:hasPrivilege>
</ul>