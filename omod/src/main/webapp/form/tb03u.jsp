<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<%-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script> --%>
<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />

<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<!-- SPECIALIZED STYLES FOR THIS PAGE -->

<!-- CUSTOM JQUERY  -->
<c:set var="defaultReturnUrl" value="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${patientProgramId}&patientId=${tb03u.patient.id}"/>

<script type="text/javascript">
	var $j = jQuery.noConflict();	

	$j(document).ready(function(){
		$j('#edit').click(function(){
			$j('#viewVisit').hide();
			$j('#editVisit').show();
			hivToggle();
			codToggle();
		});
		$j('#cancel').click(function(){
			if (${(empty intake.id) || (intake.id == -1) || fn:length(errors.allErrors) > 0}) {
				// if we are in the middle of a validation error, or doing an "add" we need to do a page reload on cancel
				window.location="${!empty returnUrl ? returnUrl : defaultReturnUrl}";
			} 
			else {
				// otherwise, just hide the edit popup and show the view one	
				$j('#editVisit').hide();
				$j('#viewVisit').show();
			}
		});
		
		if(${mode eq 'edit'}) {
			$j('#viewVisit').hide();
			$j('#editVisit').show();
		}
		// $('#oblast').val(${oblastSelected});
		<c:if test="${! empty oblastSelected}">
			document.getElementById('oblast').value = ${oblastSelected};
		</c:if>
		// $('#district').val(${districtSelected});
		<c:if test="${! empty districtSelected}">
			document.getElementById('district').value = ${districtSelected};
		</c:if>
		// $('#facility').val(${facilitySelected});
		<c:if test="${! empty facilitySelected}">
			document.getElementById('facility').value = ${facilitySelected};
		</c:if>
		<c:if test="${! empty patientGroup}" >
			document.getElementById('classificationAccordingToPatientGroups').value = ${patientGroup};
		</c:if>
		<c:if test="${! empty previousDrugUse}" >
			document.getElementById('classificationAccordingToPreviousDrugUse').value = ${previousDrugUse};
		</c:if>
		// document.getElementById('oblast').disabled = true;
		// document.getElementById('district').disabled = true;
		// document.getElementById('facility').disabled = true;
		document.getElementById('encounterDatetime').disabled = true;
	});
	
	function hivToggle () {
		var statusBox = document.getElementById('hivStatus');
		var choice  = statusBox.options[statusBox.selectedIndex].value;
		showHideHivDates(choice);
	}
	
	function showHideHivDates(val) {
		var e = document.getElementById('hivDateDiv');
       	if(val==48) {
       		document.getElementById('artStartDate').disabled = false;
       		document.getElementById('pctStartDate').disabled = false;
       	} else {
			$j('#artStartDate').val("");
			$j('#pctStartDate').val("");
			document.getElementById('artStartDate').disabled = true;
      		document.getElementById('pctStartDate').disabled = true;
       	}
     }
	
	function codToggle () {
		var statusBox = document.getElementById('causeOfDeath');
		var choice  = statusBox.options[statusBox.selectedIndex].value;
		showHideOtherCod(choice);
	}
	
	function showHideOtherCod(val) {
       	if(val==291) {
       		document.getElementById('otherCauseOfDeath').disabled = false;
       	} else {
			$j('#otherCauseOfDeath').val("");
			document.getElementById('otherCauseOfDeath').disabled = true;
       	}
     }
	
	function enable() {
		document.getElementById('oblast').disabled = false;
		document.getElementById('district').disabled = false;
		document.getElementById('facility').disabled = false;
		document.getElementById('encounterDatetime').disabled = false;
	}
	
	function fill1() {
		var e = document.getElementById("oblast");
		var val = e.options[e.selectedIndex].value;
		
		if(val != "")
			window.location.replace("${pageContext.request.contextPath}/module/mdrtb/form/tb03u.form?mode=edit&ob="+val+"&patientProgramId="+${patientProgramId}+"&encounterId=" + ${!empty tb03u.id ? tb03u.id : -1})
	}

	function fill2() {
		var e = document.getElementById("oblast");
		var val1 = e.options[e.selectedIndex].value;
		var e = document.getElementById("district");
		var val2 = e.options[e.selectedIndex].value;
		
		if(val2 != "")
			window.location.replace("${pageContext.request.contextPath}/module/mdrtb/form/tb03u.form?mode=edit&loc="+val2+"&ob="+val1+"&patientProgramId="+${patientProgramId}+"&encounterId=" + ${!empty tb03u.id ? tb03u.id : -1})
	}
	
	function dateInFuture(dateStr) {
		try {
			var format = "${_dateFormatDisplay}";
			var date = new Date();
			var now = new Date();
			if (format === "dd.MM.yyyy") {
				var parts = dateStr.split(".");
				var day = parts[0];
				var month = parts[1]-1;
				var year = parts[2];
				var date = new Date(year, month, day);
			}
			if (format === "dd/MM/yyyy") {
				var parts = dateStr.split("/");
				var day = parts[0];
				var month = parts[1]-1;
				var year = parts[2];
				var date = new Date(year, month, day);
			}
			if (format === "MM/dd/yyyy") {
				var parts = dateStr.split("/");
				var month = parts[0];
				var day = parts[1]-1;
				var year = parts[2];
				var date = new Date(year, month, day);
			}
			return date.getTime() > now.getTime();
		}
		catch(err) {
			console.log(err)
		}
		return true;
	}
	
	function validate() {
		var encDate = document.getElementById("encounterDatetime").value;
		var errorText = "";
		if(encDate=="") {
			errorText = ""  + '<spring:message code="mdrtb.error.missingRegistrationDate"/>' + "";
			alert(errorText);
			return false;
		}
		
		if (dateInFuture(encDate)) {
			errorText = ""  + '<spring:message code="mdrtb.error.registrationDateInFuture"/>' + "";
			alert(errorText);
			return false;
		}

		var confDate = document.getElementById("confirmationDate").value;
		if (dateInFuture(confDate)) {
				errorText = ""  + '<spring:message code="mdrtb.error.confirmationDateInFuture"/>' + "";
			alert(errorText);
			return false;
		}

		var txDate = document.getElementById("mdrTreatmentStartDate").value;
		if(txDate!="") {
			if (dateInFuture(txDate)) {
				errorText = ""  + '<spring:message code="mdrtb.error.treatmentStartDateInFuture"/>' + "";
				alert(errorText);
				return false;
			}
		}
		
		var hivDate = document.getElementById("hivTestDate").value;
		if(hivDate!="") {
			if (dateInFuture(hivDate)) {
				errorText = ""  + '<spring:message code="mdrtb.error.hivTestDateInFuture"/>' + "";
				alert(errorText);
				return false;
			}
		}
		
		var artDate = document.getElementById("artStartDate").value;
		if(artDate!="") {
			if (dateInFuture(artDate)) {
				errorText = ""  + '<spring:message code="mdrtb.error.artStartDateInFuture"/>' + "";
				alert(errorText);
				return false;
			}
		}
		
		var pctDate = document.getElementById("pctStartDate").value;
		if(pctDate!="") {
			if (dateInFuture(pctDate)) {
				errorText = ""  + '<spring:message code="mdrtb.error.pctStartDateInFuture"/>' + "";
				alert(errorText);
				return false;
			}
		}
		
		var txOutcomeDate = document.getElementById("treatmentOutcomeDate").value;
		if(txOutcomeDate!="") {
			if (dateInFuture(txOutcomeDate)) {
				errorText = ""  + '<spring:message code="mdrtb.error.treatmentOutcomeDateInFuture"/>' + "";
				alert(errorText);
				return false;
			}
		}
		
		var deathDate = document.getElementById("dateOfDeathAfterOutcome").value;	
		if(deathDate!="") {
			if (dateInFuture(deathDate)) {
				errorText = ""  + '<spring:message code="mdrtb.error.deathDateInFuture"/>' + "";
				alert(errorText);
				return false;
			}
		}
		enable();
		return true;
	}

	var tableToExcel = (function() {
		  var uri = 'data:application/vnd.ms-excel;base64,'
		    , template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>TB03u</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--><meta http-equiv="content-type" content="text/plain; charset=UTF-8"/></head><body><table>{table}</table></body></html>'
		    , base64 = function(s) { return window.btoa(unescape(encodeURIComponent(s))) }
		    , format = function(s, c) { return s.replace(/{(\w+)}/g, function(m, p) { return c[p]; }) }
		  return function(table, name) {
		    if (!table.nodeType) table = document.getElementById(table)
		    var ctx = {worksheet: name || 'Worksheet', table: table.innerHTML}
		    window.location.href = uri + base64(format(template, ctx))
		  }
		}
	)()
		
	function printForm() {
		var mywindow = window.open('', 'PRINT', 'height=400,width=600');

	    mywindow.document.write('<html><head><title><spring:message code="mdrtb.tb03u" text="TB03"/></title>');
	    mywindow.document.write('</head><body >');
	    mywindow.document.write('<h1><spring:message code="mdrtb.tb03u" text="TB03"/></h1>');
	    mywindow.document.write(document.getElementById("tb03u").innerHTML);
	    
	    mywindow.document.write('</body></html>');

	    mywindow.document.close();
	    mywindow.focus();

	    mywindow.print();
	    mywindow.close();

	    return true;
	}	

</script>

<br/>

<div> <!-- start of page div -->

&nbsp;&nbsp;<a href="${!empty returnUrl ? returnUrl : defaultReturnUrl}"><spring:message code="mdrtb.back" text="Back"/></a>
<br/><br/>

<!-- VIEW BOX -->
<div id="viewVisit" <c:if test="${(empty tb03u.id) || (tb03u.id == -1) || fn:length(errors.allErrors) > 0}"> style="display:none" </c:if>>
<b class="boxHeader"><spring:message code="mdrtb.tb03uForm" />
<span style="position: absolute; right:30px;"><a id="print" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'" onclick="printForm()"><spring:message code="mdrtb.print" text="TB03"/></a>&nbsp;&nbsp;<a id="export" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'" onclick="tableToExcel('tb03u', 'TB03u')"><spring:message code="mdrtb.exportToExcel" text="TB03"/></a>
<openmrs:hasPrivilege privilege="Edit DOTS-MDR Data">
&nbsp;&nbsp;<a id="edit" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"><spring:message code="mdrtb.edit" text="edit"/></a>&nbsp;&nbsp;<a href="${pageContext.request.contextPath}/module/mdrtb/visits/delete.form?visitId=${tb03u.id}&patientProgramId=${patientProgramId}&patientId=${tb03u.patient.id}" class="delete" onclick="return confirm('<spring:message code="mdrtb.confirmDeleteVisit" text="Are you sure you want to delete this visit?"/>')"><spring:message code="mdrtb.delete" text="delete"/></a>
</openmrs:hasPrivilege>
</span>
</b>
<div id="tb03u" class="box">

<table>
<tr><td>
 
<tr>
<td><spring:message code="mdrtb.date" text="Date"/>:</td>
<td><openmrs:formatDate date="${tb03u.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.oblast" text="Oblast"/>:</td>
<td>${tb03u.location.stateProvince}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.district" text="District"/>:</td>
<td>${tb03u.location.countyDistrict}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.facility" text="District"/>:</td>
<td>${tb03u.location.address4}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.name" text="Name"/>:</td>
<td>${tb03u.patientName}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.tb03RegistrationNumber" text="TB03 Reg Num"/>:</td>
<td>${tb03u.tb03RegistrationNumber}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.tb03RegistrationYear" text="TB03 Reg Year"/>:</td>
<td>${tb03u.tb03RegistrationYear}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.gender" text="Gender"/>:</td>
<td>${tb03u.gender}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.ageAtMdrRegistration" text="Age at MDR Regisration"/>:</td>
<td>${tb03u.ageAtMDRRegistration}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.dateOfBirth" text="Date of Birth"/>:</td>
<td><openmrs:formatDate date="${tb03u.dateOfBirth}" format="${_dateFormatDisplay}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.address" text="Residential Address"/>:</td>
<td>${tb03u.address }</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.sldRegisterNumber" text="Num in Reg2"/>:</td>
<td>${tb03u.sldRegisterNumber}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.siteOfDisease" text="Anatomical Type"/>:</td>
<td>${tb03u.anatomicalSite.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.registrationGroup" text="Registration Group"/>:</td>
<td>${tb03u.registrationGroup.displayString}</td>
</tr>

<%--
<tr>
<td><spring:message code="mdrtb.previousDrugClassification" text="Registration Group By Drug"/>:</td>
<td>${tb03u.registrationGroupByDrug.displayString}</td>
</tr>
 --%>

<tr>
<td><spring:message code="mdrtb.tb03.mdrStatus" text="MDRStatus"/>:</td>
<td>${tb03u.mdrStatus.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.confirmationDate" text="ConfDate"/>:</td>
<td><openmrs:formatDate date="${tb03u.confirmationDate}" format="${_dateFormatDisplay}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.patientCategory" text="Tx Regimen"/>:</td>
<td>${tb03u.patientCategory.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.mdrTreatmentStartDate" text="Mdr Treatment Start Date"/>:</td>
<td><openmrs:formatDate date="${tb03u.mdrTreatmentStartDate}" format="${_dateFormatDisplay}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.txLocation" text="Tx Location"/>:</td>
<td>${tb03u.txLocation.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.nameOfTreatmentLocation" text="Name ofTx Location"/>:</td>
<td>${tb03u.nameOfTxLocation}</td>
</tr>

</table>

<br/>

<spring:message code="mdrtb.dsts" text="DST"/>
<table border="1">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.monthOfTreatment"/></td>
<td style="font-weight:bold" colspan="2" align="center"><nobr><spring:message code="mdrtb.result"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.lab"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.specimenId"/></td>
</tr>

<c:forEach var="dst" items="${tb03u.dsts}">
<tr>
<td rowspan="2" valign="middle" align="center">${dst.monthOfTreatment }</td>
<td><spring:message code="mdrtb.resistant"/></td>
<td>${dst.di.resistantDrugs}</td>
<td rowspan="2" valign="middle" align="center"><openmrs:formatDate date="${dst.encounter.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
<td rowspan="2" valign="middle" align="center">${dst.encounter.location.displayString}</td>
<td rowspan="2" valign="middle" align="center"><a href="${pageContext.request.contextPath}/module/mdrtb/form/dst.form?encounterId=${dst.id}&patientProgramId=${patientProgramId}" target="_blank">${dst.specimenId}</a></td>
</tr>
<tr>
<td><spring:message code="mdrtb.sensitive"/></td>
<td>${dst.di.sensitiveDrugs}</td>
</tr>
</c:forEach>

</table>

<spring:message code="mdrtb.xperts" text="Xpert"/>
<table border="1">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.monthOfTreatment"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.lab"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.specimenId"/></td>
</tr>

<c:forEach var="xpert" items="${tb03u.xperts}">
<tr>
<td>${xpert.monthOfTreatment }</td>
<td>${xpert.resultString}</td>   <%-- mtbResult.displayString }/RIF: ${xpert.rifResult.displayString }</td> --%>
<td><openmrs:formatDate date="${xpert.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
<td>${xpert.location.displayString}</td>
<td><a href="${pageContext.request.contextPath}/module/mdrtb/form/xpert.form?encounterId=${xpert.id}&patientProgramId=${patientProgramId}" target="_blank">${xpert.specimenId}</a></td>
</c:forEach>
</tr>
</table>

<br/>

<spring:message code="mdrtb.hains" text="Hain"/>
<table border="1">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.monthOfTreatment"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.lab"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.specimenId"/></td>
</tr>

<c:forEach var="hain" items="${tb03u.hains}">
<tr>
<td>${hain.monthOfTreatment }</td>
<td>${hain.mtbResult.displayString }/RIF: ${hain.rifResult.displayString }/ INH: ${hain.inhResult.displayString }</td>
<td><openmrs:formatDate date="${hain.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
<td>${hain.location.displayString}</td>
<td><a href="${pageContext.request.contextPath}/module/mdrtb/form/hain.form?encounterId=${hain.id}&patientProgramId=${patientProgramId}" target="_blank">${hain.specimenId}</a></td>
</c:forEach>
</tr>
</table>

<br/>

<spring:message code="mdrtb.hain2s" text="Hain"/>
<table border="1">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.monthOfTreatment"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.lab"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.specimenId"/></td>
</tr>

<c:forEach var="hain2" items="${tb03u.hain2s}">
<tr>
<td>${hain2.monthOfTreatment }</td>
<td>${hain2.mtbResult.displayString }/FQ: ${hain2.fqResult.displayString }/ INJ: ${hain2.injResult.displayString }</td>
<td><openmrs:formatDate date="${hain2.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
<td>${hain2.location.displayString}</td>
<td><a href="${pageContext.request.contextPath}/module/mdrtb/form/hain2.form?encounterId=${hain2.id}&patientProgramId=${patientProgramId}" target="_blank">${hain2.specimenId}</a></td>
</c:forEach>
</tr>
</table>

<br/>

<table>

<tr>
<td><spring:message code="mdrtb.tb03u.resistanceType" text="Type of Resistance"/>:</td>
<td>${tb03u.resistanceType.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.basisForDiagnosis" text="Basis For Diagnosis"/>:</td>
<td>${tb03u.basisForDiagnosis.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.hivTestDate" text="HIV Test Date"/>:</td>
<td><openmrs:formatDate date="${tb03u.hivTestDate}" format="${_dateFormatDisplay}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.hivStatus" text="HIV Status"/>:</td>
<td>${tb03u.hivStatus.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.artStartDate" text="ART Start Date"/>:</td>
<td><openmrs:formatDate date="${tb03u.artStartDate}" format="${_dateFormatDisplay}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.cptStartDate" text="PCT Start Date"/>:</td>
<td><openmrs:formatDate date="${tb03u.pctStartDate}" format="${_dateFormatDisplay}"/></td>
</tr>



</table>

<br/>

<spring:message code="mdrtb.smears" text="Smear"/>
<table border="1">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.monthOfTreatment"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.lab"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.specimenId"/></td>
</tr>
<c:forEach var="smear" items="${tb03u.smears}">
<tr>
<td>${smear.monthOfTreatment }</td>
<td>${smear.smearResult.displayString }</td>
<td><openmrs:formatDate date="${smear.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
<td>${smear.location.displayString}</td>
<td><a href="${pageContext.request.contextPath}/module/mdrtb/form/smear.form?encounterId=${smear.id}&patientProgramId=${patientProgramId}" target="_blank">${smear.specimenId}</a></td>
</c:forEach>
</tr>
</table>

<br/>

<spring:message code="mdrtb.cultures" text="Culture"/>
<table border="1">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.monthOfTreatment"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.lab"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.specimenId"/></td>
</tr>

<c:forEach var="culture" items="${tb03u.cultures}">
<tr>
<td>${culture.monthOfTreatment }</td>
<td>${culture.cultureResult.displayString }</td>
<td><openmrs:formatDate date="${culture.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
<td>${culture.location.displayString}</td>
<td><a href="${pageContext.request.contextPath}/module/mdrtb/form/culture.form?encounterId=${culture.id}&patientProgramId=${patientProgramId}" target="_blank">${culture.specimenId}</a></td>
</c:forEach>
</tr>
</table>

<br/>

<spring:message code="mdrtb.tb03u.resistanceDuringTx" text="Drdt"/>
<table border="1">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.date"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.drdt"/></td>
<td style="font-weight:bold"></td>
</tr>

<c:forEach var="drdt" items="${tb03u.drdts}">
<tr>
<td><openmrs:formatDate date="${drdt.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
<td>${drdt.drugResistanceDuringTreatment.displayString}</td>
<td><a href="${pageContext.request.contextPath}/module/mdrtb/form/resistanceDuringTx.form?encounterId=${drdt.id}&patientProgramId=${patientProgramId}" target="_blank"><spring:message code="mdrtb.view"/></a></td>
</c:forEach>
</tr>
</table>

<br/>

<table>


<tr>
<td><spring:message code="mdrtb.tb03.treatmentOutcome" text="Tx Outcome"/>:</td>
<td>${tb03u.treatmentOutcome.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.treatmentOutcomeDateOnly" text="Outcome Date"/>:</td>
<td><openmrs:formatDate date="${tb03u.treatmentOutcomeDate}" format="${_dateFormatDisplay}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.causeOfDeath" text="Cause of Death"/>:</td>
<td>${tb03u.causeOfDeath.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.otherCauseOfDeath" text="Other Cause of Death"/>:</td>
<td>${tb03u.otherCauseOfDeath}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.afterOutcomeDeathDate" text="Date of Death after Outcome"/>:</td>
<td><openmrs:formatDate date="${tb03u.dateOfDeathAfterOutcome}"  format="${_dateFormatDisplay}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.relapsed" text="Relapsed"/>:</td>
<td>${tb03u.relapsed.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.relapseMonth" text="Relapse Month"/>:</td>
<td>${tb03u.relapseMonth}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.clinicalNotes" text="Clinical Notes"/>:</td>
<td>${tb03u.cliniciansNotes}</td>
</tr>


</table>
</td></tr>
</table>
</div>
</div>
<!-- END VIEW BOX -->

<!-- EDIT BOX -->
<div id="editVisit" <c:if test="${(!empty tb03u.id) && (tb03u.id != -1) && fn:length(errors.allErrors) == 0}"> style="display:none" </c:if>>
<b class="boxHeader"><spring:message code="mdrtb.tb03Form" text="tb03u Form"/></b>
<div class="box">

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(errors.allErrors) > 0}">
	<c:forEach var="error" items="${errors.allErrors}">
		<c:if test="${error.code != 'methodInvocation'}">
			<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
		</c:if>	
	</c:forEach>
	<br/>
</c:if>

<form name="tb03u" action="tb03u.form?patientId=${patientId}&patientProgramId=${patientProgramId}&encounterId=${!empty tb03u.id ? tb03u.id : -1}" method="post" onSubmit="return validate()">
<input type="hidden" name="returnUrl" value="${returnUrl}" />
<input type="hidden" name="patientProgramId" value="${patientProgramId}" />
<input type="hidden" name="provider" value="47" />
<%-- <input type="hidden" name="previousProgramId" <c:choose><c:when test="${!empty tb03u.previousProgramId }">value="${tb03u.previousProgramId}"</c:when><c:otherwise>value="${previousProgramId}"</c:otherwise></c:choose>/> --%>

<table>
 
<tr>
<td><spring:message code="mdrtb.date" text="Date"/>:</td>
<td><openmrs_tag:dateField formFieldName="encounterDatetime" startValue="${tb03u.encounterDatetime}"/></td>
</tr>

</table>

</table>

<table>
<tr id="oblastDiv">
			<td align="right"><spring:message code="mdrtb.oblast" /></td>
			<td><select name="oblast" id="oblast" onchange="fill1()">
					<option value=""></option>
					<c:forEach var="o" items="${oblasts}">
						<%-- <option value="${o.id}">${o.name}</option> --%>
						<option value="${o.id}" <c:if test="${o.name == tb03u.location.stateProvince}">selected</c:if> >${o.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		
		<tr id="districtDiv">
			<td align="right"><spring:message code="mdrtb.district" /></td>
			<td><select name="district" id="district" onchange="fill2()">
					<option value=""></option>
					<c:forEach var="dist" items="${districts}">
						<%-- <option value="${dist.id}">${dist.name}</option> --%>
						<option value="${dist.id}" <c:if test="${dist.name == tb03u.location.countyDistrict}">selected</c:if> >${dist.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		
		<tr id="facilityDiv">
			<td align="right"><spring:message code="mdrtb.facility" /></td>
			<td><select name="facility" id="facility">
					<option value=""></option>
					<c:forEach var="f" items="${facilities}">
						<%-- <option value="${f.id}">${f.name}</option> --%>
						<option value="${f.id}" <c:if test="${f.name == tb03u.location.address4}">selected</c:if> >${f.name}</option>
					</c:forEach>
			</select>
			</td>
		</tr>
	</table>
	
<table>

<tr>
<td><spring:message code="mdrtb.tb03.name" text="Name"/>:</td>
<td>${tb03u.patientName}</td>
</tr>

<tr>
<td valign="top"><spring:message code="mdrtb.tb03.tb03RegistrationNumber" text="TB03RegistrationNumber"/>:</td>
<%-- <td><input name="tb03RegistrationNumber" size="12" value="${tb03u.tb03RegistrationNumber}"/></td> --%>
<td>${tb03u.tb03RegistrationNumber}</td>
</tr>

<tr>
<td valign="top"><spring:message code="mdrtb.tb03.tb03RegistrationYear" text="year of tb03 registration"/>:</td>
<%-- <td><input name="tb03RegistrationYear" size="8" value="${tb03u.tb03RegistrationYear}"/></td> --%>
<td>${tb03u.tb03RegistrationYear}</td>
</tr>
 
<tr>
<td><spring:message code="mdrtb.gender" text="Gender"/>:</td>
<td>${tb03u.gender}</td>
</tr>
 
<tr>
<td valign="top"><spring:message code="mdrtb.tb03.ageAtMDRRegistration" text="Age at MDRRegisration"/>:</td>
<%-- <td><input name="ageAtMDRRegistration" size="8" value="${tb03u.ageAtMDRRegistration}"/></td> --%>
<td>${tb03u.ageAtMDRRegistration}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.dateOfBirth" text="Date of Birth"/>:</td>
<td><openmrs:formatDate date="${tb03u.dateOfBirth}" format="${_dateFormatDisplay}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.address" text="Residential Address"/>:</td>
<td>${tb03u.address }</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.sldRegisterNumber" text="Num in Reg2"/>:</td>
<td><input name="sldRegisterNumber" size="10" value="${tb03u.sldRegisterNumber}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.siteOfDisease" text="Anatomical Type"/>:</td>
<td>
<select name="anatomicalSite">
<option value=""></option>
<c:forEach var="site" items="${sites}">
	<option value="${site.answerConcept.id}" <c:if test="${tb03u.anatomicalSite == site.answerConcept}">selected</c:if> >${site.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.registrationGroup" text="Registration Group"/>:</td>
<td>${tb03u.registrationGroup.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.previousDrugClassification" text="Registration Group By Drug"/>:</td>
<td>${tb03u.registrationGroupByDrug.displayString}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.mdrStatus" text="MDRStatusz"/>:</td>
<td>
<select name="mdrStatus">
<option value=""></option>
<c:forEach var="status" items="${mdrstatuses}">
	<option value="${status.answerConcept.id}" <c:if test="${tb03u.mdrStatus == status.answerConcept}">selected</c:if> >${status.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.confirmationDate" text="ConfDate"/>:</td>
<td><openmrs_tag:dateField formFieldName="confirmationDate" startValue="${tb03u.confirmationDate}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.patientCategory" text="Regimen Type"/>:</td>
<td>
<select name="patientCategory">
<option value=""></option>
<c:forEach var="category" items="${categories}">
	<option value="${category.answerConcept.id}" <c:if test="${tb03u.patientCategory == category.answerConcept}">selected</c:if> >${category.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.mdrTreatmentStartDate" text="Mdr Tx Start Date"/>:</td>
<td><openmrs_tag:dateField formFieldName="mdrTreatmentStartDate" startValue="${tb03u.mdrTreatmentStartDate}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.txLocation" text="Tx Location"/>:</td>
<td>
<select name="txLocation">
<option value=""></option>
<c:forEach var="loc" items="${txlocations}">
	<option value="${loc.answerConcept.id}" <c:if test="${tb03u.txLocation == loc.answerConcept}">selected</c:if> >${loc.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.nameOfTreatmentLocation" text="Name of TxLocation"/>:</td>
<td><input name="nameOfTxLocation" size="10" value="${tb03u.nameOfTxLocation}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03u.resistanceType" text="Type of Resistance"/>:</td>
<td>
<select name="resistanceType">
<option value=""></option>
<c:forEach var="type" items="${resistancetypes}">
	<option value="${type.answerConcept.id}" <c:if test="${tb03u.resistanceType == type.answerConcept}">selected</c:if> >${type.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>


<tr>
<td><spring:message code="mdrtb.tb03.basisForDiagnosis" text="Basis For Diagnosis"/>:</td>
<td>
<select name="basisForDiagnosis">
<option value=""></option>
<c:forEach var="basis" items="${basesfordiagnosis}">
	<option value="${basis.answerConcept.id}" <c:if test="${tb03u.basisForDiagnosis == basis.answerConcept}">selected</c:if> >${basis.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.hivTestDate" text="HIVTestDate"/>:</td>
<td><openmrs_tag:dateField formFieldName="hivTestDate" startValue="${tb03u.hivTestDate}"/></td>
</tr>


<tr>
<td><spring:message code="mdrtb.tb03.hivStatus" text="HIV Status" />:</td>
<td>
<select name="hivStatus" id="hivStatus" onChange="hivToggle()">
<option value=""></option>
<c:forEach var="status" items="${hivstatuses}">
	<option value="${status.answerConcept.id}" <c:if test="${tb03u.hivStatus == status.answerConcept}">selected</c:if> >${status.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.artStartDate" text="ART Start Date"/>:</td>
<td><openmrs_tag:dateField formFieldName="artStartDate" startValue="${tb03u.artStartDate}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.pctStartDate" text="PCT Start Date"/>:</td>
<td><openmrs_tag:dateField formFieldName="pctStartDate" startValue="${tb03u.pctStartDate}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.treatmentOutcome" text="Tx Outcome"/>:</td>
<td>
<select name="treatmentOutcome">
<option value=""></option>
<c:forEach var="outcome" items="${outcomes}">
	<option value="${outcome.concept.id}" <c:if test="${tb03u.treatmentOutcome == outcome.concept}">selected</c:if> >${outcome.concept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.treatmentOutcomeDateOnly" text="Outcome Dates"/>:</td>
<td><openmrs_tag:dateField formFieldName="treatmentOutcomeDate" startValue="${tb03u.treatmentOutcomeDate}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.causeOfDeath" text="Cause of Death"/>:</td>
<td>
<select name="causeOfDeath" id="causeOfDeath" onChange="codToggle()">
<option value=""></option>
<c:forEach var="type" items="${causes}">
	<option value="${type.answerConcept.id}" <c:if test="${tb03u.causeOfDeath == type.answerConcept}">selected</c:if> >${type.answerConcept.displayString}</option>
</c:forEach>
</select>
&nbsp;&nbsp;&nbsp;&nbsp;<input name="otherCauseOfDeath" id="otherCauseOfDeath" size="15" value="${tb03u.otherCauseOfDeath}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.afterOutcomeDeathDate" text="Date of Death after Outcome"/>:</td>
<td><openmrs_tag:dateField formFieldName="dateOfDeathAfterOutcome" startValue="${tb03u.dateOfDeathAfterOutcome}"/></td>
</tr>



<tr>
<td><spring:message code="mdrtb.tb03.relapsed" text="Relapsed"/>:</td>
<td>
<select name="relapsed">
<option value=""></option>
<c:forEach var="relapseOption" items="${relapses}">
	<option value="${relapseOption.answerConcept.id}" <c:if test="${tb03u.relapsed == relapseOption.answerConcept}">selected</c:if> >${relapseOption.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.relapseMonth" text="RelapseMonth"/>:</td>
<td><input name="relapseMonth" size="10" value="${tb03u.relapseMonth}"/></td>
</tr>

<tr>
<td><spring:message code="mdrtb.tb03.clinicalNotes" text="Clinical Notes"/>:</td>
<td><textarea rows="4" cols="50" name="cliniciansNotes">${tb03u.cliniciansNotes}</textarea></td>
</tr>

</table>

<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button> <button id="cancel" type="reset"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
	
</form>

</div>
</div>
<!-- END OF EDIT BOX -->
</div>

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>