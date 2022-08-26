<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<%@ taglib prefix="mdrtb" uri="/WEB-INF/view/module/mdrtb/taglibs/mdrtb.tld" %>
<%@ taglib prefix="wgt" uri="/WEB-INF/view/module/htmlwidgets/resources/htmlwidgets.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="mdrtb.css"/>


<!-- only show the headers if we are in edit mode (i.e,. we have an existing patient id) -->
<c:if test="${! empty patientId && patientId != -1}">
	<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>
	<%-- <openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}" parameters="patientProgramId=${patientProgramId}"/> --%>
</c:if>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	th {vertical-align:top}
</style>

<!-- JQUERY FOR THIS PAGE -->
<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){
		
	/* 	$('#country').val(${countrySelected});
		$('#oblast').val(${oblastSelected});
		$('#district').val(${districtSelected});
		$('#facility').val(${facilitySelected}); */

		// toggle the estimated checkbox
		$j('#birthdateEstimatedCheckbox').click(function () {
			if ($j(this).is(':checked')){
				$j('#birthdateEstimated').attr('value', "true");
			}
			else {
				$j('#birthdateEstimated').attr('value', "false");
			} 
		});

		// handle the toggle of the dead checkbox
		$j('#deadCheckbox').click(function () {
			if ($j(this).is(':checked')){
				$j('#dead').attr('value', 'true');
				// show the death date and cause of death rows
				$j('#deathDateRow').show();
				$j('#causeOfDeathRow').show();
			}
			else {
				$j('#dead').attr('value', 'false');
				// hide the death date and cause of death rows
				$j('#deathDateRow').hide();
				$j('#causeOfDeathRow').hide();
				// reset the values of these fields
				$j('#deathDate').val('');
				$j('#causeOfDeath').val('');
				$j('#causeOfDeathTextField').val('');
			} 
		});

		// handle the toggle of the voided checkbox
		$j('#voidedCheckbox').click(function () {
			if ($j(this).is(':checked')){
				$j('#voided').attr('value', 'true');
				// show the void reason row
				$j('#voidReasonRow').show();
			}
			else {
				$j('#voided').attr('value', 'false');
				// show the void reason row
				$j('#voidReasonRow').hide();
				// reset the value of void reason
				$j('#voidReason').val('');
			} 
		});
		

	});
//http://localhost:8080/openmrs/module/mdrtb/mdrtbEditPatient.form?successURL=%2Fmodule%2Fmdrtb%2Fdashboard%2Ftbdashboard.form&addName=pata+chala&addBirthdate=&addAge=36&addGender=F&personType=patient&viewType=shortEdit&add=1	
	/* function fun0()
	{
		var c = document.getElementById("country");
		var val0 = c.options[c.selectedIndex].value;
		
		var url = "${pageContext.request.contextPath}/module/mdrtb/mdrtbEditPatient.form?";
		
		if(val0!="") {
			url = url + "c="+val0;
		}
		
		var givenName = document.getElementById("givenName").value;
		var familyName = document.getElementById("familyName").value;
		
		if(givenName!="") {
			url = url + "&givenName=" + givenName;
		}
		
		if(familyName!="") {
			url = url + "&familyName=" + familyName;
		}
			
			
		window.location.replace("${pageContext.request.contextPath}/module/mdrtb/mdrtbEditPatient.form?c="+val0)
	}
	
	
	function fun1()
	{
		var c = document.getElementById("country");
		var val0 = c.options[c.selectedIndex].value;
		var e = document.getElementById("oblast");
		var val = e.options[e.selectedIndex].value;
		

		if(val!="")
			window.location.replace("${pageContext.request.contextPath}/module/mdrtb/mdrtbEditPatient.form?c="+val0 + "&ob="+val)
	}

	function fun2()
	{
		var c = document.getElementById("country");
		var val0 = c.options[c.selectedIndex].value;
		var e1 = document.getElementById("oblast");
		var val1 = e1.options[e1.selectedIndex].value;
		var e = document.getElementById("district");
		var val2 = e.options[e.selectedIndex].value;
		
		if(val2!="")
			window.location.replace("${pageContext.request.contextPath}/module/mdrtb/mdrtbEditPatient.form?loc="+val2+"&ob="+val1+ "&c=" + val0)
	} */
-->
</script>

<!-- END JQUERY -->

<!-- START PAGE DIV -->
<div align="center">

<!--  EDIT/ADD PATIENT BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.patientDetails" text="Patient Details"/></b>
<div class="box" style="margin:0px;">

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(errors.allErrors) > 0}">
	<c:forEach var="error" items="${errors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
	</c:forEach>
	<br/>
</c:if>

<form action="mdrtbEditPatient.form" method="POST">
<input type="hidden" name="successURL" value="${successURL}" />
<input type=hidden name="patientId" value="${patientId}"/>
<input type=hidden name="patientProgramId" value="${patientProgramId}"/>
<input type=hidden name="add" value="${add}"/>

<table style="padding:6px">

<tr>
<th style="headerCell">&nbsp;<!-- <spring:message code="mdrtb.name"/> --></th>
<td>
<table cellspacing="0" cellpadding="0" border="0">
<!-- <tr>
<td><spring:message code="mdrtb.familyName"/></td>
<td><input type="text" name="familyName" id="familyName"></td>
</tr>
<tr>
<td><spring:message code="mdrtb.givenName"/></td>
<td><input type="text" name="givenName" id="givenName"></td>
</tr> -->

<openmrs:portlet url="nameLayout" id="namePortlet" size="columnHeaders" parameters="layoutShowTable=false|layoutShowExtended=false" /></td>
<spring:nestedPath path="patient.personName">
<openmrs:portlet url="nameLayout" id="namePortlet" size="inOneRow" parameters="layoutMode=edit|layoutShowTable=false|layoutShowExtended=false" />
</spring:nestedPath>
</table>
</td>
</tr>

<tr height="5"><td colspan="2">&nbsp;</td></tr>


<c:choose>
<c:when test="${add eq '1' }">
<tr>
<th class="headerCell"><spring:message code="mdrtb.identifierNumbers"/></th>
<td>
<table>
<input name="identifierType" type="hidden" value="${dotsIdentifier.id}"/>
<input name="identifierId" type="hidden" value=""/>
<tr>
<td>${dotsIdentifier.name}</td>
<td><input name="identifierValue" type="text"></td>
</tr>
<tr>

<td>
<div style="display: none;">
<spring:message code="mdrtb.identifierLocation"/>
</div>
</td>

<td>
<div style="display: none;">
<select name="identifierLocation">
	<c:forEach var="location" items="${locations}">
		<option value="${location.id}">${location.displayString}</option>
	</c:forEach>
	</select>
</td>
</div>
</tr>
</table>
</c:when>
<c:otherwise>
<%-- <c:forEach var="type" items="${patientIdentifierTypesAutoAssigned}">
<input name="identifierId" type="hidden" value="${! empty patientIdentifierMap[type.id] ? patientIdentifierMap[type.id].id : ''}"/>
<input name="identifierType" type="hidden" value="${type.id}"/>
<input name="identifierValue" type="hidden" value="${! empty patientIdentifierMap[type.id] ? patientIdentifierMap[type.id].identifier : ''}"/>
<tr>
<td>${type.name}</td>
<td>
<c:choose>
	<c:when test="${! empty patientIdentifierMap[type.id]}">
		${patientIdentifierMap[type.id].identifier}
	</c:when>
	<c:otherwise>
		(<spring:message code="mdrtb.willBeAutoAssigned" text="Will be auto-assigned"/>)
	</c:otherwise>
</c:choose>
</td>
<c:if test="${showIdentifierLocationSelector}">
	<td><select name="identifierLocation">
	<option value=""/>
	<c:forEach var="location" items="${locations}">
		<option value="${location.id}" <c:if test="${location == patientIdentifierMap[type.id].location}">selected</c:if>>${location.displayString}</option>
	</c:forEach>
	</select>
</c:if>
</td>
</tr>
</c:forEach>	


<c:forEach var="type" items="${patientIdentifierTypes}">
<input name="identifierId" type="hidden" value="${! empty patientIdentifierMap[type.id] ? patientIdentifierMap[type.id].id : ''}"/>
<input name="identifierType" type="hidden" value="${type.id}"/>
<tr>
<td>${type.name}</td>
<td><input name="identifierValue" type="text" value="${! empty patientIdentifierMap[type.id] ? patientIdentifierMap[type.id].identifier : ''}"/></td>
<td>&nbsp;</td>
</tr>

<c:if test="${showIdentifierLocationSelector}">
	<tr>
	<td><spring:message code="mdrtb.identifierLocation"/></td>
	<td><select name="identifierLocation">
	<option value=""/>
	<c:forEach var="location" items="${locations}">
		<option value="${location.id}" <c:if test="${location == patientIdentifierMap[type.id].location}">selected</c:if>>${location.displayString}</option>
	</c:forEach>
	</select></td>
	</tr>
</c:if>

</c:forEach>	
 --%>
</c:otherwise>
</c:choose>


<tr height="5"><td colspan="2">&nbsp;</td></tr>

<tr>
<th class="headerCell"><spring:message code="mdrtb.address"/></th>
<td>
<spring:nestedPath path="patient.personAddress">
<openmrs:portlet url="addressLayout" id="addressPortlet" size="full" parameters="layoutMode=edit|layoutShowTable=true|layoutShowExtended=false" />
</spring:nestedPath>
<%-- <table>
        <tr id="countryDiv">
			<td align="left"><spring:message code="mdrtb.country" /></td>
			<td><select name="country" id="country" onchange="fun0()">
					<option value=""></option>
					<c:forEach var="c" items="${countries}">
						<option value="${c.id}" >${c.name}</option>
					</c:forEach>
			</select></td>
			<!-- <td><input type="text" name="otherCountry" id="otherCountry"></td> -->
		</tr>


		<tr id="oblastDiv">
			<td align="left"><spring:message code="mdrtb.oblast" /></td>
			<td><select name="oblast" id="oblast" onchange="fun1()">
					<option value=""></option>
					<c:forEach var="o" items="${oblasts}">
						<option value="${o.id}" >${o.name}</option>
					</c:forEach>
			</select></td>
			<!-- <td><input type="text" name="otherOblast" id="otherOblast"></td> -->
		</tr>
		
		<tr id="districtDiv">
			<td align="left"><spring:message code="mdrtb.district" /></td>
			<td><select name="district" id="district" onchange="fun2()">
					<option value=""></option>
					<c:forEach var="dist" items="${districts}">
						<option value="${dist.id}" >${dist.name}</option>
					</c:forEach>
			</select></td>
			<!-- <td><input type="text" name="otherDistrict" id="otherDistrict"></td> -->
		</tr>
		
		<tr id="facilityDiv">
			<td align="left"><spring:message code="mdrtb.facility" /></td>
			<td><select name="facility" id="facility">
					<option value=""></option>
					<c:forEach var="f" items="${facilities}">
						<option value="${f.id}" >${f.name}</option>
					</c:forEach>
			</select>
			</td>
			<!-- <td><input type="text" name="otherFacility" id="otherFacility"></td> -->
		</tr>
		<tr>
			<td align="left"><spring:message code="mdrtb.address1" /></td>
			<td><input type="text" name="address1" id="address1"></td>	
		</tr>
		<tr>
			<td align="left"><spring:message code="mdrtb.address2" /></td>
			<td><input type="text" name="address1" id="address2"></td>	
		</tr>
	</table> --%>

</td>
</tr>

<tr height="5"><td colspan="2">&nbsp;</td></tr>

<tr>
<th class="headerCell"><spring:message code="patientDashboard.demographics"/></th>
<td>
<table cellspacing="3" cellpadding="3">
<tr>
<td><spring:message code="mdrtb.gender"/></td>
<td>
<openmrs:forEachRecord name="gender">
<input type="radio" name="gender" id="${record.key}" value="${record.key}" <c:if test="${record.key == patient.gender}">checked</c:if> />
<label for="${record.key}"> <spring:message code="mdrtb.${record.value}"/> </label>
</openmrs:forEachRecord>
</td>
</tr>
<tr>
<td><spring:message code="mdrtb.birthdate"/></td>
<td>
<openmrs_tag:dateField formFieldName="birthdate" startValue="${patient.birthdate}"/>
<spring:message code="mdrtb.estimated"/>
<input type="hidden" id="birthdateEstimated" name="birthdateEstimated" value="${patient.birthdateEstimated}"/>
<input type="checkbox" id="birthdateEstimatedCheckbox" name="birthdateEstimatedCheckbox" value="true" <c:if test="${patient.birthdateEstimated == true}">checked</c:if> />
</td>
</tr>
</table>
</td>
</tr>

<tr height="5"><td colspan="2">&nbsp;</td></tr>

<tr>
<th><spring:message code="mdrtb.deceased" text="Deceased"/></th>
<td>
<input type="hidden" id="dead" name="dead" value="${patient.dead}"/>
<input type="checkbox" id="deadCheckbox" name="deadCheckbox" value="true" <c:if test="${patient.dead == true}">checked</c:if> />
</td>
</tr>
<tr id="deathDateRow" <c:if test="${!patient.dead}">style="display:none"</c:if>>
<th><spring:message code="mdrtb.deathDate"/></th>
<td><openmrs_tag:dateField formFieldName="deathDate" startValue="${patient.deathDate}"/></td>
</tr>
<tr id="causeOfDeathRow" <c:if test="${!patient.dead}">style="display:none"</c:if>>
<th><spring:message code="mdrtb.causeOfDeath"/></th>
<td><wgt:widget id="causeOfDeath" name="causeOfDeath" type="org.openmrs.Concept" object="${patient}" property="causeOfDeath"/></td>
</tr>

<tr height="5"><td colspan="2">&nbsp;</td></tr>

<openmrs:forEachDisplayAttributeType personType="patient" displayType="viewing" var="attrType">
	<tr>
		<th class="headerCell">
		<spring:message code="PersonAttributeType.${fn:replace(attrType.name, ' ', '')}" text="${attrType.name}"/></th>
		<td class="inputCell">
			<openmrs:fieldGen 
					type="${attrType.format}" 
					formFieldName="attributeMap[${attrType.name}].value" 
					val="${patient.attributeMap[attrType.name].hydratedObject}" 
					parameters="optionHeader=[blank]|showAnswers=${attrType.foreignKey}" />
		</td>
	</tr>
</openmrs:forEachDisplayAttributeType>

<tr height="5"><td colspan="2">&nbsp;</td></tr>

<tr>
<th><spring:message code="general.voided"/></th>
<td>
<input type="hidden" id="voided" name="voided" value="${patient.voided}"/>
<input type="checkbox" id="voidedCheckbox" name="voidedCheckbox" value="true" <c:if test="${patient.voided}">checked</c:if> />
</td>
</tr>

<tr id="voidReasonRow" <c:if test="${!patient.voided}">style="display:none"</c:if>>
<th><spring:message code="general.voidReason"/></th>
<td><input id="voidReason" name="voidReason" value="${patient.voidReason}"/></td>
</tr>

<tr height="5"><td colspan="2">&nbsp;</td></tr>

<c:if test="${add ne '1' }">
<tr>
<th><spring:message code="general.createdBy"/></th>
<td>${patient.personCreator.person.personName.fullName} - ${patient.personDateCreated}</td>
</tr>
<tr>
<th><spring:message code="general.changedBy"/></th>
<td>${patient.personChangedBy.person.personName.fullName} - ${patient.personDateChanged}</td>
</tr>
</c:if>

<tr><td colspan="2">
<button type="submit"><spring:message code="Patient.save" text="Save Patient"/></button>
<button id="reset" type="reset"><spring:message code="general.cancel" text="Cancel"/></button></td></tr>

</table>

</form>


</div>

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>