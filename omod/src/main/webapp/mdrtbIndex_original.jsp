<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/findPatient.htm" />

<h2><table><tr><td><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/who_logo.bmp" alt="logo WHO" style="height:50px; width:50px;" border="0"/></td><td>&nbsp;<spring:message code="mdrtb.title" /></td></tr></table></h2>

<br />
<table class="indexTable">
	<tr>
		<td width=60% valign='top'>
			<openmrs:portlet id="mdrtbFindPatient" url="mdrtbFindPatient" parameters="size=full|postURL=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form|showIncludeVoided=false|viewType=shortEdit" moduleId="mdrtb"/>
			<openmrs:hasPrivilege privilege="Add Patients">
				<br/><br/>
				<openmrs:portlet id="mdrtbAddPatient" url="mdrtbAddPatient" parameters="personType=patient|postURL=mdrtbEditPatient.form|successURL=/module/mdrtb/dashboard/dashboard.form|viewType=shortEdit" moduleId="mdrtb" />
			</openmrs:hasPrivilege>
			
			<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.bottomLeft" type="html">
				<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
					<div class="box">
						
						<b class="boxHeader"><spring:message code="${extension.title}"/></b>
						<ul id="menu">
							<c:forEach items="${extension.links}" var="link">
								<c:choose>
									<c:when test="${fn:startsWith(link.key, 'module/')}">
										<%-- Added for backwards compatibility for most links --%>
										<li><a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a></li>
									</c:when>
									<c:otherwise>
										<%-- Allows for external absolute links  --%>
										<li><a href='<c:url value="${link.key}"/>'><spring:message code='${link.value}'/></a></li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</ul>
					</div>
				</openmrs:hasPrivilege>
			</openmrs:extensionPoint>
			
		</td>
		<td>&nbsp;&nbsp;&nbsp;
		</td>
		<td valign='top'>
			<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.upperRight" type="html">
				<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
					<div class="box">
						
						<b class="boxHeader"><spring:message code="${extension.title}"/></b>
						<ul id="menu">
							<c:forEach items="${extension.links}" var="link">
								<c:choose>
									<c:when test="${fn:startsWith(link.key, 'module/')}">
										<%-- Added for backwards compatibility for most links --%>
										<li><a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a></li>
									</c:when>
									<c:otherwise>
										<%-- Allows for external absolute links  --%>
										<li><a href='<c:url value="${link.key}"/>'><spring:message code='${link.value}'/></a></li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</ul>
					</div>
				</openmrs:hasPrivilege>
			</openmrs:extensionPoint>
			
				<b class="boxHeader" style="margin:0px">&nbsp;&nbsp;
					<spring:message code="mdrtb.patientLists"/>&nbsp;&nbsp;
				</b>
		
				<div class="box" style="margin:0px;">
					<table>
						<tr><td>
							<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbListPatients.form">
								<spring:message code="mdrtb.viewListPatientPage"/>
							</a>
						</td></tr>
						
						<c:forEach var="e" items="${patientLists}">
							<tr><td>
								<a href="${pageContext.request.contextPath}/${e.value}"><spring:message code="${e.key}"/></a>
							</td></tr>
						</c:forEach>
					
						<c:if test="${showCohortBuilder}">
							<tr><td>
								<a href="${pageContext.request.contextPath}/cohortBuilder.list">
									<spring:message code="mdrtb.cohortBuilder" text="Cohort Builder"/>
								</a>
							</td></tr>
						</c:if>
	
						<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.listPatientLinks" type="html">
							<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
								<spring:message code="${extension.title}"/>:
								<c:forEach items="${extension.links}" var="link">
									<tr><td>
										<a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a>&nbsp;&nbsp;
									</td></tr>
								</c:forEach>
							</openmrs:hasPrivilege>
						</openmrs:extensionPoint>
					</table>
				</div>
			
				<c:set var="reportsFound" value="f"/>
				<b class="boxHeader" style="margin:0px">&nbsp;&nbsp;
					<spring:message code="mdrtb.reports"/>&nbsp;&nbsp;
				</b>
				<div class="box" style="margin:0px;">
					<table>
						<c:forEach var="entry" items="${reports}" varStatus="varStatus">
							<c:set var="reportsFound" value="t"/>
								<tr><td>
									<a href="${pageContext.request.contextPath}/${entry.key}">
										${entry.value}
									</a>
								</td></tr>
						</c:forEach>
					
						<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.reportLinks" type="html">
							<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
								<c:forEach items="${extension.links}" var="link">
									<c:set var="reportsFound" value="t"/>
									<tr><td>
										<a href="${pageContext.request.contextPath}/${link.key}">
											${link.value}
										</a>
									</td></tr>
								</c:forEach>
							</openmrs:hasPrivilege>
						</openmrs:extensionPoint>
						
						<c:if test="${reportsFound == 'f'}">
							<tr><td>
								<i> &nbsp; <spring:message code="mdrtb.noReports"/></i>
							</td></tr>
						</c:if>
					</table>
				</div>
		
				<b class="boxHeader" style="margin:0px">
					&nbsp;&nbsp;<spring:message code="mdrtb.viewdrugrequirements" />&nbsp;&nbsp;
				</b>
				
				<div class="box" style="margin:0px;">
					<table>
						<tr><td>
							<a href="drugforecast/simpleUsage.list"><spring:message code="mdrtb.simpleDrugUsage"/></a>
						</td></tr>
						<tr><td>
							<a href="drugforecast/patientsTakingDrugs.list"><spring:message code="mdrtb.numberofpatientstakingeachdrug" /></a>
						</td></tr>
					</table>
				</div>
			
			</table>
		</td>
	</tr>
</table>
<br>&nbsp;<br>

<%@ include file="mdrtbFooter.jsp"%>
