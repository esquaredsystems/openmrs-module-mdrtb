<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>

<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/findPatient.htm" />

<!-- <h2><table><tr><td><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/who_logo.bmp" alt="logo WHO" style="height:50px; width:50px;" border="0"/></td><td>&nbsp;<spring:message code="mdrtb.title" /></td></tr></table></h2> -->
<h2><table>
<tr>
<td align="left">
<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/TJK_logo.jpg" alt="logo Tajikistan" style="height:78px; width:87px;" border="0"/>
<c:choose>
<c:when test="${locale=='ru' }">
<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/USAID_logo_ru_t.jpg" alt="logo USAID" style="height:78px; width:259px;" border="0"/>
</c:when>
<c:when test="${locale=='tj' }">
<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/USAID_logo_tj_t.jpg" alt="logo USAID" style="height:78px; width:259px;" border="0"/>
</c:when>
<c:otherwise>
<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/USAID_logo_en.jpg" alt="logo USAID" style="height:78px; width:259px;" border="0"/>
</c:otherwise>
</c:choose>
<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/WHO_Euro_logo.jpg" alt="logo WHO Euro" style="height:78px; width:60px;" border="0"/>
<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/gfatm_square.jpg" alt="logo GFATM" style="height:78px; width:83px;" border="0"/>


<!-- <img src="${pageContext.request.contextPath}/moduleResources/mdrtb/USAID_logo_${locale}.jpg" alt="logo USAID" style="height:78px; width:259px;" border="0"/> -->
</td>
</tr>
<tr>
<td>&nbsp;</td>
</tr>
</table></h2>

<br />
<table class="indexTable">
	<tr>
		<td width=60% valign='top'>
			<openmrs:portlet id="mdrtbFindPatient" url="mdrtbFindPatient" parameters="size=full|postURL=${pageContext.request.contextPath}/module/mdrtb/program/enrollment.form|showIncludeVoided=false|viewType=shortEdit" moduleId="mdrtb"/>
			<openmrs:hasPrivilege privilege="Edit DOTS-MDR Data">
				<br/><br/>
				<openmrs:portlet id="mdrtbAddPatient" url="mdrtbAddPatient" parameters="personType=patient|postURL=mdrtbEditPatient.form|successURL=/module/mdrtb/dashboard/tbdashboard.form|viewType=shortEdit|add=1" moduleId="mdrtb" />
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
			
			<table class="indexInner">
			    <openmrs:hasPrivilege privilege="Edit DOTS-MDR Data">
			    <!-- General Operations -->
				<tr><td style="padding:2px 2px 2px 2px;">
					<b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">&nbsp;&nbsp;
						<spring:message code="mdrtb.general"/>&nbsp;&nbsp;
					</b>
				</td></tr>
				
				<tr><td>
					<a href="${pageContext.request.contextPath}/admin/patients/findDuplicatePatients.htm">
						<spring:message code="mdrtb.mergePatients"/>
					</a>
					<br/>
				 	<a href="${pageContext.request.contextPath}/module/mdrtb/reporting/patientLists.form"><spring:message code="mdrtb.patientLists"/></a>
					<br/>

					<%-- <a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbListPatients.form">
						<spring:message code="mdrtb.viewListPatientPage"/>
					</a>
					<br/>
					<c:forEach var="e" items="${patientLists}">
						<a href="${pageContext.request.contextPath}/${e.value}"><spring:message code="${e.key}"/></a><br/>
					</c:forEach>
					<br/>
					<a href="${pageContext.request.contextPath}/module/dotsreports/dotsListPatients.form">
						<spring:message code="mdrtb.viewDotsListPatientPage"/>
					</a>
					<br/><br/> --%>
					<!-- <c:if test="${showCohortBuilder}">
						<a href="${pageContext.request.contextPath}/cohortBuilder.list">
							<spring:message code="mdrtb.cohortBuilder" text="Cohort Builder"/>
						</a><br/>
					</c:if> -->

					<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.listPatientLinks" type="html">
						<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
							<spring:message code="${extension.title}"/>:
							<c:forEach items="${extension.links}" var="link">
								<a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a>&nbsp;&nbsp;
							</c:forEach>
							<br/>
						</openmrs:hasPrivilege>
					</openmrs:extensionPoint>
				</td></tr>
			    </openmrs:hasPrivilege>
				
				<tr><td style="padding:2px 2px 2px 2px;">
					 <b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">
						&nbsp;&nbsp;<spring:message code="mdrtb.dotsreports" />&nbsp;&nbsp;
					</b>
				</td></tr>
				
				<tr><td>
					<a href="../mdrtb/reporting/viewClosedReports.form?type=DOTSTB"><spring:message code="mdrtb.viewClosedReports" /></a><br/>
					<!-- <a href="drugforecast/simpleUsage.list"><spring:message code="mdrtb.simpleDrugUsage"/></a><br/>
					<a href="drugforecast/patientsTakingDrugs.list"><spring:message code="mdrtb.numberofpatientstakingeachdrug" /></a><br/> -->
					<!-- <a href="../dotsreports/reporting/reports.form?type=org.openmrs.module.mdrtb.reporting.data.DOTS07TJKUpdatedTB03"><spring:message code="mdrtb.dotsreport07" /></a><br/> -->
					<a href="../mdrtb/reporting/tb07.form"><spring:message code="mdrtb.dotsreport07" /></a><br/>
					<!-- <a href="../dotsreports/reporting/reports.form?type=org.openmrs.module.mdrtb.reporting.data.DOTS08TJKUpdatedTB03"><spring:message code="mdrtb.dotsreport08" /></a><br/> -->
					<a href="../mdrtb/reporting/tb08.form"><spring:message code="mdrtb.dotsreport08" /></a><br/>
					<a href="../mdrtb/reporting/tb03.form"><spring:message code="mdrtb.tb03Export" /></a><br/>
					<a href="../mdrtb/reporting/tb03Single.form"><spring:message code="mdrtb.tb03ExportSingleLine" /></a><br/>
					<a href="../mdrtb/reporting/form89Single.form"><spring:message code="mdrtb.f89ExportSingleLine" /></a><br/>
					<a href="../mdrtb/reporting/dotsdq.form"><spring:message code="mdrtb.dotsdq.title" /></a><br/>
					<a href="../mdrtb/reporting/missingTb03.form"><spring:message code="mdrtb.dq.missingtb03" /></a><br/>
				</td></tr>
				
				<!-- <c:set var="reportsFound" value="f"/> -->
				<tr><td style = "padding:2px 2px 2px 2px;">
					<b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">&nbsp;&nbsp;
						<spring:message code="mdrtb.mdrreports"/>&nbsp;&nbsp;
					</b>
				</td></tr>
				<tr class="${rowClass}"><td>
					<a href="../mdrtb/reporting/viewClosedReports.form?type=MDRTB"><spring:message code="mdrtb.viewClosedReports" /></a><br/>
					<a href="../mdrtb/reporting/tb07u.form"><spring:message code="mdrtb.tb07u" /></a><br/>
					<a href="../mdrtb/reporting/tb08u.form"><spring:message code="mdrtb.tb08Fast" /></a><br/>
					<a href="../mdrtb/reporting/tb03u.form"><spring:message code="mdrtb.tb03uExport" /></a><br/>
					<a href="../mdrtb/reporting/tb03uSingle.form"><spring:message code="mdrtb.tb03uExportSingleLine" /></a><br/>
					<a href="../mdrtb/reporting/dq.form"><spring:message code="mdrtb.dq.title" /></a><br/>
					<a href="../mdrtb/reporting/regimen.form"><spring:message code="mdrtb.sldreport" /><br/>
					<a href="../mdrtb/reporting/missingTb03u.form"><spring:message code="mdrtb.dq.missingtb03u" /></a><br/>
					<%-- <openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.reportLinks" type="html">
						<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
							<c:forEach items="${extension.links}" var="link">
								<c:set var="reportsFound" value="t"/>
								<a href="${pageContext.request.contextPath}/${link.key}">
									${link.value}
								</a><br/>
							</c:forEach>
						</openmrs:hasPrivilege>
					</openmrs:extensionPoint>
					<c:if test="${reportsFound == 'f'}">
						<i> &nbsp; <spring:message code="mdrtb.noReports"/></i><br/>
					</c:if> --%>
				</td></tr>
				
				
				
				<tr><td style="padding:2px 2px 2px 2px;">
					 <b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">
						&nbsp;&nbsp;<spring:message code="mdrtb.otherreports" />&nbsp;&nbsp;
					</b>
				</td></tr>
				
				<tr><td>
					<!-- <a href="drugforecast/simpleUsage.list"><spring:message code="mdrtb.simpleDrugUsage"/></a><br/>
					<a href="drugforecast/patientsTakingDrugs.list"><spring:message code="mdrtb.numberofpatientstakingeachdrug" /></a><br/>
					<a href="drugforecast/patientsOnSLD.list"><spring:message code="mdrtb.sldreport" /></a><br/> -->
					<a href="../mdrtb/reporting/form8.form"><spring:message code="mdrtb.form8.title" /></a><br/>
					<a href="../mdrtb/reporting/pv/ae.form"><spring:message code="mdrtb.pv.qtrReportTitle" /></a><br/>
					<a href="../mdrtb/reporting/pv/aeRegister.form"><spring:message code="mdrtb.pv.register.title" /></a><br/>
					<!-- <a href="../dotsreports/reporting/reports.form?type=org.openmrs.module.mdrtb.reporting.data.DOTSMDRReport"><spring:message code="mdrtb.dotsmdrreport" /></a><br/> -->
				</td></tr>
			
				
			
			</table>
		</td>
	</tr>
</table>
<br>&nbsp;<br>
<div><font size="2"><spring:message code="mdrtb.disclaimerusaid" /></font></div>
<%@ include file="mdrtbFooter.jsp"%>
