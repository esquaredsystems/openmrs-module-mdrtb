<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ page import="org.openmrs.web.WebConstants" %>

<%
	pageContext.setAttribute("msg", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	pageContext.setAttribute("msgArgs", session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
	pageContext.setAttribute("err", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	pageContext.setAttribute("errArgs", session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
	session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
%>



<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<openmrs:htmlInclude file="/openmrs.js" />
		<openmrs:htmlInclude file="/openmrs.css" />
		<openmrs:htmlInclude file="/style.css" />
		<openmrs:htmlInclude file="/mdrtb.css"/>
		<openmrs:htmlInclude file="/dwr/engine.js" />
		<openmrs:htmlInclude file="/dwr/util.js" />
		<openmrs:htmlInclude file="/dwr/interface/DWRAlertService.js" />

		<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
		
		<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js"/>
		<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js" />
		<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui.custom.css" />
		<link href="<openmrs:contextPath/>/scripts/jquery-ui/css/<spring:theme code='jqueryui.theme.name' />/jquery-ui.custom.css" type="text/css" rel="stylesheet" />
		<!-- set the date format to use throughout the module -->
		<c:set var="_dateFormatDisplay" value="dd.MM.yyyy" scope="request"/>
		
		<c:choose>
			<c:when test="${!empty pageTitle}">
				<title>${pageTitle}</title>
			</c:when>
			<c:otherwise>
				<title><spring:message code="openmrs.title" /></title>
			</c:otherwise>
		</c:choose>
		
		<script type="text/javascript">
			/* variable used in js to know the context path */
			var openmrsContextPath = '${pageContext.request.contextPath}';
			
			/* js date format (required by openmrs calendar widget) */
			var jsDateFormat = '<openmrs:datePattern localize="false"/>';
			var jsLocale = '<%= org.openmrs.api.context.Context.getLocale() %>';
			
			var $j = jQuery.noConflict();

        </script>

        <openmrs:extensionPoint pointId="org.openmrs.headerFullIncludeExt" type="html" requiredClass="org.openmrs.module.web.extension.HeaderIncludeExt">
            <c:forEach var="file" items="${extension.headerFiles}">
                <openmrs:htmlInclude file="${file}" />
            </c:forEach>
        </openmrs:extensionPoint>

	</head>

<body>
<div id="pageBody">
<div id="userBar">
  <openmrs:authentication>
		<c:if test="${authenticatedUser != null}">
			<span id="userLoggedInAs" class="firstChild">
				<spring:message code="header.logged.in"/> ${authenticatedUser.personName}
			</span>
			<span id="userLogout">
				<a href='${pageContext.request.contextPath}/logout'><spring:message code="header.logout" /></a>
			</span>
			<span>
				<a href="${pageContext.request.contextPath}/options.form"><spring:message code="Navigation.options"/></a>
			</span>
		</c:if>
		<c:if test="${authenticatedUser == null}">
			<span id="userLoggedOut" class="firstChild">
				<spring:message code="header.logged.out"/>
			</span>
			<span id="userLogIn">
				<a href='${pageContext.request.contextPath}/login.htm'><spring:message code="header.login"/></a>
			</span>
		</c:if>
	</openmrs:authentication>

	<span id="userHelp">
		<a href='<%= request.getContextPath() %>/help.htm'><spring:message code="header.help"/></a>
	</span>
	
	<span id="classicViewLink">
		<c:set var="id" value="${!empty patientId ? patientId : program.patient.id}"/>
		<c:if test="${!empty id}">
			<a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${id}">
		</c:if>
		<c:if test="${empty id}">
			<a href="${pageContext.request.contextPath}/index.htm">
		</c:if>
				<spring:message code="mdrtb.switchToclassicView"/>
			</a>
	</span>
	
	<openmrs:extensionPoint pointId="org.openmrs.headerFull.userBar" type="html">
		<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
			<span>
				<a href="${extension.url}"><spring:message code="${extension.label}"/></a>
			</span>
			<c:if test="${extension.portletUrl != null}">
				<openmrs:portlet url="${extension.portletUrl}" moduleId="${extension.moduleId}" id="${extension.portletUrl}" />
			</c:if>
		</openmrs:hasPrivilege>
	</openmrs:extensionPoint>
	
</div>

<%@ include file="mdrtbBanner.jsp"%>

<div id="content">

	<openmrs:forEachAlert>
		<c:if test="${varStatus.first}"><div id="alertOuterBox"><div id="alertInnerBox"></c:if>
			<div class="alert">
				<a href="#markRead" onClick="return markAlertRead(this, '${alert.alertId}')" HIDEFOCUS class="markAlertRead">
					<img src="${pageContext.request.contextPath}/images/markRead.gif" alt='<spring:message code="Alert.mark"/>' title='<spring:message code="Alert.mark"/>'/> <span class="markAlertText"><spring:message code="Alert.markAsRead"/></span>
				</a>
				${alert.text} ${alert.dateToExpire} <c:if test="${alert.satisfiedByAny}"><i class="smallMessage">(<spring:message code="Alert.mark.satisfiedByAny"/>)</i></c:if>
			</div>
		<c:if test="${varStatus.last}">
			</div>
			<div id="alertBar">
				<img src="${pageContext.request.contextPath}/images/alert.gif" align="center" alt='<spring:message code="Alert.unreadAlert"/>' title='<spring:message code="Alert.unreadAlert"/>'/>
				<c:if test="${varStatus.count == 1}"><spring:message code="Alert.unreadAlert"/></c:if>
				<c:if test="${varStatus.count != 1}"><spring:message code="Alert.unreadAlerts" arguments="${varStatus.count}" /></c:if>
			</div>
			</div>
		</c:if>
	</openmrs:forEachAlert>

	<c:if test="${msg != null}">
		<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" /></div>
	</c:if>
	<c:if test="${err != null}">
		<div id="openmrs_error"><spring:message code="${err}" text="${err}" arguments="${errArgs}"/></div>
	</c:if>
	<div id="openmrs_dwr_error" style="display:none" class="error">
		<div id="openmrs_dwr_error_msg"></div>
		<div id="openmrs_dwr_error_close" class="smallMessage">
			<i><spring:message code="error.dwr.stacktrace"/></i> 
			<a href="#" onclick="this.parentNode.parentNode.style.display='none'"><spring:message code="error.dwr.hide"/></a>
		</div>
	</div>
	
	
	