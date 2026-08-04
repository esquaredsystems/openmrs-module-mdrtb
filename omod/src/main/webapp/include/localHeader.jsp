<%--
  The contents of this file are subject to the OpenMRS Public License
  Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
  http://license.openmrs.org
  Software distributed under the License is distributed on an "AS IS"
  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
  License for the specific language governing rights and limitations
  under the License.
  Copyright (C) OpenMRS, LLC.  All Rights Reserved.
  --%>

<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu" >
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><mdrtb:message
				code="admin.title.short" /></a></li>
	<%-- <li
		<c:if test='<%=request.getRequestURI().contains("/commonlabtest")%>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/commonlabtest/commonlabtest.form"><mdrtb:message
				code="commonlabtest.general.about" /></a>
	</li> --%>
   
	<li
		<c:if test='<%=request.getRequestURI().contains("/manageLabTestTypes")%>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/commonlabtest/manageLabTestTypes.form"><mdrtb:message
				code="commonlabtest.labtesttype.manage" /></a>
	</li>
	<li
		<c:if test='<%=request.getRequestURI().contains("/manageLabTestAttributeTypes")%>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/commonlabtest/manageLabTestAttributeTypes.form">
		<mdrtb:message code="commonlabtest.labtestattributetype.manage" /></a>
	</li>
	<%-- <li
		<c:if test='<%=request.getRequestURI().contains("/manageLabTestSamples")%>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/commonlabtest/manageLabTestSamples.form"><mdrtb:message
				code="commonlabtest.labtestsample.manage" /></a>
	</li>
	<li
		<c:if test='<%=request.getRequestURI().contains("/manageLabTests")%>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/commonlabtest/manageLabTests.form"><mdrtb:message
				code="commonlabtest.labtest.manage" /></a>
	</li> --%>

</ul>
