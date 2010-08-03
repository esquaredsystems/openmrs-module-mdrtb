<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>		
		
<table class="portletTable" style="border-spacing:20px;border-collapse:separate;border-style:none;">
<tr><td style="vertical-align:top;">	

			<openmrs:extensionPoint pointId="org.openmrs.mdrtb.formsPortlet.links" type="html">
				<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
					<div style="font-size:80%;">
							<c:forEach items="${extension.links}" var="link">
								<c:choose>
									<c:when test="${fn:startsWith(link.key, 'module/')}">
										<%-- Added for backwards compatibility for most links --%>
										<a href="${pageContext.request.contextPath}/${link.key}?patientId=${obj.patient.patientId}"><spring:message code="${link.value}"/></a><br/>
									</c:when>
									<c:otherwise>
										<%-- Allows for external absolute links  --%>
										<a href='<c:url value="${link.key}?patientId=${obj.patient.patientId}"/>'><spring:message code='${link.value}'/></a><br/>
									</c:otherwise>
								</c:choose>
							</c:forEach>
					</div>
				</openmrs:hasPrivilege>
			</openmrs:extensionPoint>
			<br/>		
			<c:if test="${!empty mdrtbForms}">
				<table class="widgetOut" style="font-size:80%;">
						<tr nowrap><th nowrap style="background-color:white;"><spring:message code="mdrtb.availablemdrtbforms" /></th></tr>
						<c:forEach items="${mdrtbForms}" var="form" varStatus="varStatus">
							<c:set var="rowClass" scope="page">
								<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>
							</c:set>
								<Tr><td class="${rowClass}"><a href='${pageContext.request.contextPath}/moduleServlet/formentry/formDownload?target=formentry&patientId=${obj.patient.patientId}&formId=${form.formId}'>${form.name}</a></td></Tr>
								

						
						</c:forEach>
				</table>
			</c:if>
			<c:if test="${!empty htmlForms}">		
				<table class="widgetOut" style="font-size:80%;">
						<tr><th nowrap style="background-color:white;"><spring:message code="mdrtb.availablemdrtbforms" /></th></tr>
						<c:forEach items="${htmlForms}" var="form" varStatus="varStatus">
							<c:set var="rowClass" scope="page">
								<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>
							</c:set>
								
								<Tr><td class="${rowClass}"><a href='${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?personId=${obj.patient.patientId}&formId=${form.formId}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/visits/visits.form?patientId=${obj.patient.patientId}'>${form.name} </a></td></Tr>

						
						</c:forEach>
						
						
				</table>
			</c:if>	
			<c:if test="${empty mdrtbForms && empty htmlForms}">
				<span style="font-size:90%;">&nbsp;&nbsp;&nbsp;<i><spring:message code="mdrtb.none" /></i></span>
			</c:if>
			
			
</td><td style="vertical-align:top;">
			<c:if test="${!empty obj.htmlEncList}">
				<table class="widgetOut" style="font-size:80%;">
					<tr><th colspan="4" nowrap style="background-color:white;"><spring:message code="Patient.encounters" /></th></tr>
					<c:forEach items="${obj.htmlEncList}" var="enc" varStatus="varStatus">
						<c:set var="rowClass" scope="page">
									<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>
								</c:set>
						<tr class="${rowClass}">
							<td class="${rowClass}" nowrap><a href='${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?personId=${obj.patient.patientId}&formId=${enc.form.formId}&encounterId=${enc.encounterId}&mode=VIEW&returnUrl=${pageContext.request.contextPath}/module/mdrtb/visits/visits.form?patientId=${obj.patient.patientId}'>${enc.form.name}</a></td>
							<td class="${rowClass}" nowrap> <openmrs:formatDate date="${enc.encounterDatetime}" format="${dateFormat}" /></td>
							<td class="${rowClass}" nowrap>${enc.provider.personName}</td>
							<td class="${rowClass}" nowrap>${enc.location}</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
			<!--<c:if test="${empty obj.htmlEncList}">
				<span style="font-size:90%;">&nbsp;&nbsp;&nbsp;<i><spring:message code="mdrtb.none" /></i></span>
			</c:if>-->
</td></tr></table>
			


<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>