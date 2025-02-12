<%@page import="org.openmrs.module.mdrtb.service.MdrtbService"%>
<%@page import="org.openmrs.api.context.Context"%>
<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<html>
	<head>
		<title>TB-07y</title>
	</head>
	<body>
		<script type="text/javascript" src="<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.min.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/moduleResources/mdrtb/tableExport/js/tableExport.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/moduleResources/mdrtb/tableExport/js/jquery.base64.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/moduleResources/mdrtb/tableExport/js/jspdf/libs/sprintf.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/moduleResources/mdrtb/tableExport/js/jspdf/jspdf.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/moduleResources/mdrtb/tableExport/js/jspdf/libs/base64.js"></script>

		<script type="text/javascript">
		function printForm() {
			var mywindow = window.open('', 'PRINT', 'height=400,width=600');

		    mywindow.document.write('<html><head><title><spring:message code="mdrtb.tb07" text="TB07u"/></title>');
		    mywindow.document.write('</head><body >');
		   // mywindow.document.write('<h1><spring:message code="mdrtb.pv.aeForm" text="AE"/></h1>');
		    mywindow.document.write(document.getElementById("tb07u").innerHTML);
		    
		    mywindow.document.write('</body></html>');

		    mywindow.document.close(); // necessary for IE >= 10
		    mywindow.focus(); // necessary for IE >= 10*/

		    mywindow.print();
		    mywindow.close();

		    return true;
		}
		var tableToExcel = (function() {
		  var uri = 'data:application/vnd.ms-excel;base64,'
		    , template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>TB07u</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--><meta http-equiv="content-type" content="text/plain; charset=UTF-8"/></head><body><table>{table}</table></body></html>'
		    , base64 = function(s) { return window.btoa(unescape(encodeURIComponent(s))) }
		    , format = function(s, c) { return s.replace(/{(\w+)}/g, function(m, p) { return c[p]; }) }
		  return function(table, name) {
		    if (!table.nodeType) table = document.getElementById(table)
		    var ctx = {worksheet: name || 'Worksheet', table: table.innerHTML}
		    window.location.href = uri + base64(format(template, ctx))
		  }
		})()
		function savePdf(action, reportName, formPath) {
			var tableData = (document.getElementById("tb07u")).innerHTML.toString();
			var oblast = "${oblast}";
			var district = "${location.locationId}";
			var year = "${year}";
			var quarter = "${quarter}";
			var month = "${month}";
			var reportDate = "${reportDate}";
			
			var form = document.createElement("FORM");

			form.setAttribute("id", "closeReportForm");
		    form.setAttribute("name", "closeReportForm");
		    form.setAttribute("method", "post");
		    form.setAttribute("action", action);
		    
		    document.body.appendChild(form);
		    
		    var input = document.createElement("INPUT");
		    input.setAttribute("type", "hidden");
		    input.setAttribute("id", "oblast");
		    input.setAttribute("name", "oblast");
		    input.setAttribute("value", oblast);
		    form.appendChild(input);

		    var input = document.createElement("INPUT");
		    input.setAttribute("type", "hidden");
		    input.setAttribute("id", "location");
		    input.setAttribute("name", "location");
		    input.setAttribute("value", district);
		    form.appendChild(input);
		    
		    var input = document.createElement("INPUT");
		    input.setAttribute("type", "hidden");
		    input.setAttribute("id", "year");
		    input.setAttribute("name", "year");
		    input.setAttribute("value", year);
		    form.appendChild(input);

		    var input = document.createElement("INPUT");
		    input.setAttribute("type", "hidden");
		    input.setAttribute("id", "quarter");
		    input.setAttribute("name", "quarter");
		    input.setAttribute("value", quarter);
		    form.appendChild(input);

		    var input = document.createElement("INPUT");
		    input.setAttribute("type", "hidden");
		    input.setAttribute("id", "month");
		    input.setAttribute("name", "month");
		    input.setAttribute("value", month);
		    form.appendChild(input);
		    
		    var input = document.createElement("INPUT");
		    input.setAttribute("type", "hidden");
		    input.setAttribute("id", "reportDate");
		    input.setAttribute("name", "reportDate");
		    input.setAttribute("value", reportDate);
		    form.appendChild(input);

		    var input = document.createElement("INPUT");
		    input.setAttribute("type", "hidden");
		    input.setAttribute("id", "table");
		    input.setAttribute("name", "table");
		    input.setAttribute("value", tableData);
		    form.appendChild(input);

		    var input = document.createElement("INPUT");
		    input.setAttribute("type", "hidden");
		    input.setAttribute("id", "formPath");
		    input.setAttribute("name", "formPath");
		    input.setAttribute("value", formPath);
		    form.appendChild(input);

		    var input = document.createElement("INPUT");
		    input.setAttribute("type", "hidden");
		    input.setAttribute("id", "reportName");
		    input.setAttribute("name", "reportName");
		    input.setAttribute("value", reportName);
		    form.appendChild(input);
		    
		    var input = document.createElement("INPUT");
		    input.setAttribute("type", "hidden");
		    input.setAttribute("id", "reportType");
		    input.setAttribute("name", "reportType");
		    input.setAttribute("value", "MDRTB");
		    form.appendChild(input);
		    
		    form.submit();
		}
		$(document).ready(function(){
			$("#tableToSql").bind("click", function() {
				if(confirm('<spring:message code="mdrtb.closeReportMessage" />') ) {
					savePdf("closeReport.form", "TB-07u", "tb07uResults");
				}
			});
			/* $("#tableToPdf").click(function(){
				savePdf("exportReport.form", "TB-07u", "tb07uResults");
			}); */
		});
		</script>
		<div id="tb07u" style="font-size:smaller; width:980px;">
			<style>
				th {vertical-align:middle; text-align:center;}
				th, td {font-size:smaller;}
			</style>
			<table width="90%"><tr>
				<td width="90" align="left" style="font-size:14px; font-weight:bold;">
					<spring:message code="mdrtb.tb07u.title"/>
				</td>
				<td width="10%" align="right" style="font-size:14px; font-weight:bold;">TB 07y</td>
			</tr></table>
			<br/><br/>
			<table border="1" width="100%">
			
			<td>
			<spring:message code="mdrtb.tb07u.nameOfFacility"/> <u>&nbsp; ${fName} &nbsp;</u> <br/>
			<spring:message code="mdrtb.tb07u.regionCityDistrict"/>  <u> ${oName}/${dName} </u><br/>
			<spring:message code="mdrtb.tb07u.tbCoordinatorName"/> ____________________<spring:message code="mdrtb.tb07u.signature"/> ____________<br/>
			</td>
		
			<td>
			<spring:message code="mdrtb.tb07u.tbCasesDetectedDuringQuarterYear" arguments="${quarter},${year}"/> <br/>
			<spring:message code="mdrtb.tb07u.dateOfReport"/> ${reportDate }
			</td>
			</tr>
			</table>	
			<br/><br/>
			
			<span style="font-weight:bold;">
				<spring:message code="mdrtb.tb07u.table1"/>
			</span>
			<span style="font-weight:bold;">
				<br/><br/>
				<center><table width="100%" border="1">
					<tr>
					    <td rowspan="2" align="center"><spring:message code="mdrtb.tb07u.totalCasesDetected"/></td>
					    <td colspan="4" align="center"><spring:message code="mdrtb.tb07u.ofThem"/></td>
					</tr>
					<tr>
						
						<td align="center"><spring:message code="mdrtb.tb07u.pdr"/></td>
						<td align="center"><spring:message code="mdrtb.tb07u.mdr"/></td>
						<td align="center"><spring:message code="mdrtb.tb07u.prexdr"/></td>
						<td align="center"><spring:message code="mdrtb.tb07u.xdr"/></td>
					</tr>
					<tr>
					    <td style="border: 1px solid black; width: 50px; height: 20px; text-align:center;">
							${table1.totalDetections}
						</td>
						<td style="border: 1px solid black; width: 50px; height: 20px; text-align:center;">
							${table1.pdrDetections}
						</td>
						<td style="border: 1px solid black; width: 50px; height: 20px; text-align:center;">
							${table1.mdrDetections}
						</td>
						<td style="border: 1px solid black; width: 50px; height: 20px; text-align:center;">
							${table1.preXdrDetections}
						</td>
						<td style="border: 1px solid black; width: 50px; height: 20px; text-align:center;">
							${table1.xdrDetections}
						</td>
					</tr>
				</table></center>
			</span>
			<br/><br/>
			
			<span style="font-weight:bold;">
				<spring:message code="mdrtb.tb07u.table2"/>
			</span>
			<br/><br/>
			
			<table border="1" cellpadding="5" width="100%">
				<tr align="center">
				 	<td colspan="2" rowspan="4">&nbsp;</td>
					<td colspan="7"><spring:message code="mdrtb.tb07u.registrationGroup"/></td>
					<td rowspan="4"><spring:message code="mdrtb.tb07u.other"/></td>
					<td rowspan="4"><spring:message code="mdrtb.tb07u.total"/></td>
				</tr>
				<tr align="center">
					<td rowspan="3"><spring:message code="mdrtb.tb07u.newCases"/></td>
					<td colspan="6"><spring:message code="mdrtb.tb07u.retreatmentCases"/></td>
					
				</tr>
				<tr align="center">
					<td colspan="2"><spring:message code="mdrtb.tb07u.relapse"/></td>
					<td colspan="2"><spring:message code="mdrtb.tb07u.afterDefault"/></td>
					<td colspan="2"><spring:message code="mdrtb.tb07u.afterFailure"/></td>
				
				</tr>
				<tr align="center">
					<td>I</td> <!-- relapse -->
					<td>II</td> <!-- relapse -->
					<td>I</td> <!-- after default -->
					<td>II</td> <!-- after default -->
					<td>I</td> <!-- previously treated with Cat 1 -->
					<td>II</td>  <!-- previously treated with Cat 2 -->
				</tr>
				
				<tr>
					<td colspan="2"><spring:message code="mdrtb.tb07u.pdr"/></td>
					<td>${table1.newPdr }</td>
					<td>${table1.relapse1Pdr }</td>
					<td>${table1.relapse2Pdr }</td>
					<td>${table1.default1Pdr }</td>
					<td>${table1.default2Pdr }</td>
					<td>${table1.failure1Pdr }</td>
					<td>${table1.failure2Pdr }</td>
					<td>${table1.otherPdr }</td>
					<td>${table1.totalPdr }</td>
				</tr>
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				    <td>${table1.newPdr04 }</td>
					<td>${table1.relapse1Pdr04 }</td>
					<td>${table1.relapse2Pdr04 }</td>
					<td>${table1.default1Pdr04 }</td>
					<td>${table1.default2Pdr04 }</td>
					<td>${table1.failure1Pdr04 }</td>
					<td>${table1.failure2Pdr04 }</td>
					<td>${table1.otherPdr04 }</td>
					<td>${table1.totalPdr04 }</td>
				</tr>
				<tr>
				     <td>5-14</td>
				    <td>${table1.newPdr0514 }</td>
					<td>${table1.relapse1Pdr0514 }</td>
					<td>${table1.relapse2Pdr0514 }</td>
					<td>${table1.default1Pdr0514 }</td>
					<td>${table1.default2Pdr0514 }</td>
					<td>${table1.failure1Pdr0514 }</td>
					<td>${table1.failure2Pdr0514 }</td>
					<td>${table1.otherPdr0514 }</td>
					<td>${table1.totalPdr0514 }</td>
				</tr>
				<tr>
				     <td>15-17</td>
				     <td>${table1.newPdr1517 }</td>
					<td>${table1.relapse1Pdr1517 }</td>
					<td>${table1.relapse2Pdr1517 }</td>
					<td>${table1.default1Pdr1517 }</td>
					<td>${table1.default2Pdr1517 }</td>
					<td>${table1.failure1Pdr1517 }</td>
					<td>${table1.failure2Pdr1517 }</td>
					<td>${table1.otherPdr1517 }</td>
					<td>${table1.totalPdr1517 }</td>
				</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				     <td>${table1.newPdrHiv }</td>
					<td>${table1.relapse1PdrHiv }</td>
					<td>${table1.relapse2PdrHiv }</td>
					<td>${table1.default1PdrHiv }</td>
					<td>${table1.default2PdrHiv }</td>
					<td>${table1.failure1PdrHiv }</td>
					<td>${table1.failure2PdrHiv }</td>
					<td>${table1.otherPdrHiv }</td>
					<td>${table1.totalPdrHiv }</td>
				</tr>
				<tr>
					<td colspan="2"><spring:message code="mdrtb.tb07u.mdr"/></td>
					<td>${table1.newMdr }</td>
					<td>${table1.relapse1Mdr }</td>
					<td>${table1.relapse2Mdr }</td>
					<td>${table1.default1Mdr }</td>
					<td>${table1.default2Mdr }</td>
					<td>${table1.failure1Mdr }</td>
					<td>${table1.failure2Mdr }</td>
					<td>${table1.otherMdr }</td>
					<td>${table1.totalMdr }</td>
				</tr>
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				    <td>${table1.newMdr04 }</td>
					<td>${table1.relapse1Mdr04 }</td>
					<td>${table1.relapse2Mdr04 }</td>
					<td>${table1.default1Mdr04 }</td>
					<td>${table1.default2Mdr04 }</td>
					<td>${table1.failure1Mdr04 }</td>
					<td>${table1.failure2Mdr04 }</td>
					<td>${table1.otherMdr04 }</td>
					<td>${table1.totalMdr04 }</td>
				</tr>
				<tr>
				     <td>5-14</td>
				    <td>${table1.newMdr0514 }</td>
					<td>${table1.relapse1Mdr0514 }</td>
					<td>${table1.relapse2Mdr0514 }</td>
					<td>${table1.default1Mdr0514 }</td>
					<td>${table1.default2Mdr0514 }</td>
					<td>${table1.failure1Mdr0514 }</td>
					<td>${table1.failure2Mdr0514 }</td>
					<td>${table1.otherMdr0514 }</td>
					<td>${table1.totalMdr0514 }</td>
				</tr>
				<tr>
				     <td>15-17</td>
				     <td>${table1.newMdr1517 }</td>
					<td>${table1.relapse1Mdr1517 }</td>
					<td>${table1.relapse2Mdr1517 }</td>
					<td>${table1.default1Mdr1517 }</td>
					<td>${table1.default2Mdr1517 }</td>
					<td>${table1.failure1Mdr1517 }</td>
					<td>${table1.failure2Mdr1517 }</td>
					<td>${table1.otherMdr1517 }</td>
					<td>${table1.totalMdr1517 }</td>
				</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				     <td>${table1.newMdrHiv }</td>
					<td>${table1.relapse1MdrHiv }</td>
					<td>${table1.relapse2MdrHiv }</td>
					<td>${table1.default1MdrHiv }</td>
					<td>${table1.default2MdrHiv }</td>
					<td>${table1.failure1MdrHiv }</td>
					<td>${table1.failure2MdrHiv }</td>
					<td>${table1.otherMdrHiv }</td>
					<td>${table1.totalMdrHiv }</td>
				</tr>
				<tr>
					<td colspan="2"><spring:message code="mdrtb.tb07u.prexdr"/></td>
					<td>${table1.newPreXdr }</td>
					<td>${table1.relapse1PreXdr }</td>
					<td>${table1.relapse2PreXdr }</td>
					<td>${table1.default1PreXdr }</td>
					<td>${table1.default2PreXdr }</td>
					<td>${table1.failure1PreXdr }</td>
					<td>${table1.failure2PreXdr }</td>
					<td>${table1.otherPreXdr }</td>
					<td>${table1.totalPreXdr }</td>
				</tr>
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				    <td>${table1.newPreXdr04 }</td>
					<td>${table1.relapse1PreXdr04 }</td>
					<td>${table1.relapse2PreXdr04 }</td>
					<td>${table1.default1PreXdr04 }</td>
					<td>${table1.default2PreXdr04 }</td>
					<td>${table1.failure1PreXdr04 }</td>
					<td>${table1.failure2PreXdr04 }</td>
					<td>${table1.otherPreXdr04 }</td>
					<td>${table1.totalPreXdr04 }</td>
				</tr>
				<tr>
				     <td>5-14</td>
				    <td>${table1.newPreXdr0514 }</td>
					<td>${table1.relapse1PreXdr0514 }</td>
					<td>${table1.relapse2PreXdr0514 }</td>
					<td>${table1.default1PreXdr0514 }</td>
					<td>${table1.default2PreXdr0514 }</td>
					<td>${table1.failure1PreXdr0514 }</td>
					<td>${table1.failure2PreXdr0514 }</td>
					<td>${table1.otherPreXdr0514 }</td>
					<td>${table1.totalPreXdr0514 }</td>
				</tr>
				<tr>
				     <td>15-17</td>
				     <td>${table1.newPreXdr1517 }</td>
					<td>${table1.relapse1PreXdr1517 }</td>
					<td>${table1.relapse2PreXdr1517 }</td>
					<td>${table1.default1PreXdr1517 }</td>
					<td>${table1.default2PreXdr1517 }</td>
					<td>${table1.failure1PreXdr1517 }</td>
					<td>${table1.failure2PreXdr1517 }</td>
					<td>${table1.otherPreXdr1517 }</td>
					<td>${table1.totalPreXdr1517 }</td>
				</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				     <td>${table1.newPreXdrHiv }</td>
					<td>${table1.relapse1PreXdrHiv }</td>
					<td>${table1.relapse2PreXdrHiv }</td>
					<td>${table1.default1PreXdrHiv }</td>
					<td>${table1.default2PreXdrHiv }</td>
					<td>${table1.failure1PreXdrHiv }</td>
					<td>${table1.failure2PreXdrHiv }</td>
					<td>${table1.otherPreXdrHiv }</td>
					<td>${table1.totalPreXdrHiv }</td>
				</tr>
				<tr>
					<td colspan="2"><spring:message code="mdrtb.tb07u.xdr"/></td>
					<td>${table1.newXdr }</td>
					<td>${table1.relapse1Xdr }</td>
					<td>${table1.relapse2Xdr }</td>
					<td>${table1.default1Xdr }</td>
					<td>${table1.default2Xdr }</td>
					<td>${table1.failure1Xdr }</td>
					<td>${table1.failure2Xdr }</td>
					<td>${table1.otherXdr }</td>
					<td>${table1.totalXdr }</td>
				</tr>
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				    <td>${table1.newXdr04 }</td>
					<td>${table1.relapse1Xdr04 }</td>
					<td>${table1.relapse2Xdr04 }</td>
					<td>${table1.default1Xdr04 }</td>
					<td>${table1.default2Xdr04 }</td>
					<td>${table1.failure1Xdr04 }</td>
					<td>${table1.failure2Xdr04 }</td>
					<td>${table1.otherXdr04 }</td>
					<td>${table1.totalXdr04 }</td>
				</tr>
				<tr>
				     <td>5-14</td>
				    <td>${table1.newXdr0514 }</td>
					<td>${table1.relapse1Xdr0514 }</td>
					<td>${table1.relapse2Xdr0514 }</td>
					<td>${table1.default1Xdr0514 }</td>
					<td>${table1.default2Xdr0514 }</td>
					<td>${table1.failure1Xdr0514 }</td>
					<td>${table1.failure2Xdr0514 }</td>
					<td>${table1.otherXdr0514 }</td>
					<td>${table1.totalXdr0514 }</td>
				</tr>
				<tr>
				     <td>15-17</td>
				     <td>${table1.newXdr1517 }</td>
					<td>${table1.relapse1Xdr1517 }</td>
					<td>${table1.relapse2Xdr1517 }</td>
					<td>${table1.default1Xdr1517 }</td>
					<td>${table1.default2Xdr1517 }</td>
					<td>${table1.failure1Xdr1517 }</td>
					<td>${table1.failure2Xdr1517 }</td>
					<td>${table1.otherXdr1517 }</td>
					<td>${table1.totalXdr1517 }</td>
				</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				     <td>${table1.newXdrHiv }</td>
					<td>${table1.relapse1XdrHiv }</td>
					<td>${table1.relapse2XdrHiv }</td>
					<td>${table1.default1XdrHiv }</td>
					<td>${table1.default2XdrHiv }</td>
					<td>${table1.failure1XdrHiv }</td>
					<td>${table1.failure2XdrHiv }</td>
					<td>${table1.otherXdrHiv }</td>
					<td>${table1.totalXdrHiv }</td>
				</tr>
				<tr>
				    <td colspan="2"><spring:message code="mdrtb.tb07u.total"/></td>
				    <td>${ table1.newTotal }</td>
					 <td>${table1.relapse1Total }</td>
					 <td>${table1.relapse2Total }</td>
					 <td>${table1.default1Total }</td>
					 <td>${table1.default2Total }</td>
					 <td>${table1.failure1Total }</td>
					 <td>${table1.failure2Total }</td>
					 <td>${table1.otherTotal }</td>
					 <td>${table1.newTotal + table1.relapse1Total + table1.relapse2Total + table1.default1Total + table1.default2Total + table1.failure1Total + table1.failure2Total + table1.otherTotal }</td> 
				</tr>
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				      <td>${ table1.newTotal04 }</td>
					 <td>${table1.relapse1Total04 }</td>
					 <td>${table1.relapse2Total04 }</td>
					 <td>${table1.default1Total04 }</td>
					 <td>${table1.default2Total04 }</td>
					 <td>${table1.failure1Total04 }</td>
					 <td>${table1.failure2Total04 }</td>
					 <td>${table1.otherTotal04 }</td>
					 <td>${table1.newTotal04 + table1.relapse1Total04 + table1.relapse2Total04 + table1.default1Total04 + table1.default2Total04 + table1.failure1Total04 + table1.failure2Total04 + table1.otherTotal04 }</td> 
				</tr>
				</tr>
				<tr>
				     <td>5-14</td>
				      <td>${ table1.newTotal0514 }</td>
					 <td>${table1.relapse1Total0514 }</td>
					 <td>${table1.relapse2Total0514 }</td>
					 <td>${table1.default1Total0514 }</td>
					 <td>${table1.default2Total0514 }</td>
					 <td>${table1.failure1Total0514 }</td>
					 <td>${table1.failure2Total0514 }</td>
					 <td>${table1.otherTotal0514 }</td>
					 <td>${table1.newTotal0514 + table1.relapse1Total0514 + table1.relapse2Total0514 + table1.default1Total0514 + table1.default2Total0514 + table1.failure1Total0514 + table1.failure2Total0514 + table1.otherTotal0514 }</td> 
				</tr>
				</tr>
				<tr>
				     <td>15-17</td>
				     <td>${ table1.newTotal1517 }</td>
					 <td>${table1.relapse1Total1517 }</td>
					 <td>${table1.relapse2Total1517 }</td>
					 <td>${table1.default1Total1517 }</td>
					 <td>${table1.default2Total1517 }</td>
					 <td>${table1.failure1Total1517 }</td>
					 <td>${table1.failure2Total1517 }</td>
					 <td>${table1.otherTotal1517 }</td>
					 <td>${table1.newTotal1517 + table1.relapse1Total1517 + table1.relapse2Total1517 + table1.default1Total1517 + table1.default2Total1517 + table1.failure1Total1517 + table1.failure2Total1517 + table1.otherTotal1517 }</td> 
				</tr>
				</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				      <td>${ table1.newTotalHiv }</td>
					 <td>${table1.relapse1TotalHiv }</td>
					 <td>${table1.relapse2TotalHiv }</td>
					 <td>${table1.default1TotalHiv }</td>
					 <td>${table1.default2TotalHiv }</td>
					 <td>${table1.failure1TotalHiv }</td>
					 <td>${table1.failure2TotalHiv }</td>
					 <td>${table1.otherTotalHiv }</td>
					 <td>${table1.newTotalHiv + table1.relapse1TotalHiv + table1.relapse2TotalHiv + table1.default1TotalHiv + table1.default2TotalHiv + table1.failure1TotalHiv + table1.failure2TotalHiv + table1.otherTotalHiv }</td> 
				</tr>
				</tr>
				
				</table>
			<br/><br/>	
			<span style="font-weight:bold;">
				<spring:message code="mdrtb.tb07u.table3"/>
			</span>
			<br/><br/>
			<table border="1" cellpadding="5" width="100%">
				<tr align="center">
				 	<td colspan="2" rowspan="4">&nbsp;</td>
					<td colspan="7"><spring:message code="mdrtb.tb07u.registrationGroup"/></td>
					<td rowspan="4"><spring:message code="mdrtb.tb07u.other"/></td>
					<td rowspan="4"><spring:message code="mdrtb.tb07u.total"/></td>
				</tr>
				<tr align="center">
					<td rowspan="3"><spring:message code="mdrtb.tb07u.newCases"/></td>
					<td colspan="6"><spring:message code="mdrtb.tb07u.retreatmentCases"/></td>
				</tr>
				<tr align="center">
					<td colspan="2"><spring:message code="mdrtb.tb07u.relapse"/></td>
					<td colspan="2"><spring:message code="mdrtb.tb07u.afterDefault"/></td>
					<td colspan="2"><spring:message code="mdrtb.tb07u.afterFailure"/></td>
				</tr>
				<tr align="center">
					<td>I</td> <!-- relapse -->
					<td>II</td> <!-- relapse -->
					<td>I</td> <!-- after default -->
					<td>II</td> <!-- after default -->
					<td>I</td> <!-- previously treated with Cat 1 -->
					<td>II</td>  <!-- previously treated with Cat 2 -->
				</tr>
				<tr>
				    <td colspan="11" align="center"><spring:message code="mdrtb.tb07u.mdr"/></td> 
				</tr>
				<tr>
				  	 <td colspan="2"><spring:message code="mdrtb.tb07u.short"/></td>
				     <td>${table1.newShortMdr }</td>
					 <td>${table1.relapse1ShortMdr }</td>
					 <td>${table1.relapse2ShortMdr }</td>
					 <td>${table1.default1ShortMdr }</td>
					 <td>${table1.default2ShortMdr }</td>
					 <td>${table1.failure1ShortMdr }</td>
					 <td>${table1.failure2ShortMdr }</td>
					 <td>${table1.otherShortMdr }</td>
					 <td>${table1.totalShortMdr }</td>
				</tr>
				
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				     <td>${table1.newShortMdr04 }</td>
					 <td>${table1.relapse1ShortMdr04 }</td>
					 <td>${table1.relapse2ShortMdr04 }</td>
					 <td>${table1.default1ShortMdr04 }</td>
					 <td>${table1.default2ShortMdr04 }</td>
					 <td>${table1.failure1ShortMdr04 }</td>
					 <td>${table1.failure2ShortMdr04 }</td>
					 <td>${table1.otherShortMdr04 }</td>
					 <td>${table1.totalShortMdr04 }</td>
				</tr>
				<tr>
				     <td>5-14</td>
				     <td>${table1.newShortMdr0514 }</td>
					 <td>${table1.relapse1ShortMdr0514 }</td>
					 <td>${table1.relapse2ShortMdr0514 }</td>
					 <td>${table1.default1ShortMdr0514 }</td>
					 <td>${table1.default2ShortMdr0514 }</td>
					 <td>${table1.failure1ShortMdr0514 }</td>
					 <td>${table1.failure2ShortMdr0514 }</td>
					 <td>${table1.otherShortMdr0514 }</td>
					 <td>${table1.totalShortMdr0514 }</td>
				</tr>
				<tr>
				     <td>15-17</td>
				     <td>${table1.newShortMdr1517 }</td>
					 <td>${table1.relapse1ShortMdr1517 }</td>
					 <td>${table1.relapse2ShortMdr1517 }</td>
					 <td>${table1.default1ShortMdr1517 }</td>
					 <td>${table1.default2ShortMdr1517 }</td>
					 <td>${table1.failure1ShortMdr1517 }</td>
					 <td>${table1.failure2ShortMdr1517 }</td>
					 <td>${table1.otherShortMdr1517 }</td>
					 <td>${table1.totalShortMdr1517 }</td>
				</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				     <td>${table1.newShortMdrHiv }</td>
					 <td>${table1.relapse1ShortMdrHiv }</td>
					 <td>${table1.relapse2ShortMdrHiv }</td>
					 <td>${table1.default1ShortMdrHiv }</td>
					 <td>${table1.default2ShortMdrHiv }</td>
					 <td>${table1.failure1ShortMdrHiv }</td>
					 <td>${table1.failure2ShortMdrHiv }</td>
					 <td>${table1.otherShortMdrHiv }</td>
					 <td>${table1.totalShortMdrHiv }</td>
					 
				</tr>
				
				<tr>
				    <td colspan="2"><spring:message code="mdrtb.tb07u.standard"/></td>
				    <td>${table1.newStandardMdr }</td>
					 <td>${table1.relapse1StandardMdr }</td>
					 <td>${table1.relapse2StandardMdr }</td>
					 <td>${table1.default1StandardMdr }</td>
					 <td>${table1.default2StandardMdr }</td>
					 <td>${table1.failure1StandardMdr }</td>
					 <td>${table1.failure2StandardMdr }</td>
					 <td>${table1.otherStandardMdr }</td>
					 <td>${table1.totalStandardMdr }</td>
				</tr>
				
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				     <td>${table1.newStandardMdr04 }</td>
					 <td>${table1.relapse1StandardMdr04 }</td>
					 <td>${table1.relapse2StandardMdr04 }</td>
					 <td>${table1.default1StandardMdr04 }</td>
					 <td>${table1.default2StandardMdr04 }</td>
					 <td>${table1.failure1StandardMdr04 }</td>
					 <td>${table1.failure2StandardMdr04 }</td>
					 <td>${table1.otherStandardMdr04 }</td>
					 <td>${table1.totalStandardMdr04 }</td>
				</tr>
				<tr>
				     <td>5-14</td>
				     <td>${table1.newStandardMdr0514 }</td>
					 <td>${table1.relapse1StandardMdr0514 }</td>
					 <td>${table1.relapse2StandardMdr0514 }</td>
					 <td>${table1.default1StandardMdr0514 }</td>
					 <td>${table1.default2StandardMdr0514 }</td>
					 <td>${table1.failure1StandardMdr0514 }</td>
					 <td>${table1.failure2StandardMdr0514 }</td>
					 <td>${table1.otherStandardMdr0514 }</td>
					 <td>${table1.totalStandardMdr0514 }</td>
				</tr>
				<tr>
				     <td>15-17</td>
				     <td>${table1.newStandardMdr1517 }</td>
					 <td>${table1.relapse1StandardMdr1517 }</td>
					 <td>${table1.relapse2StandardMdr1517 }</td>
					 <td>${table1.default1StandardMdr1517 }</td>
					 <td>${table1.default2StandardMdr1517 }</td>
					 <td>${table1.failure1StandardMdr1517 }</td>
					 <td>${table1.failure2StandardMdr1517 }</td>
					 <td>${table1.otherStandardMdr1517 }</td>
					 <td>${table1.totalStandardMdr1517 }</td>
				</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				     <td>${table1.newStandardMdrHiv }</td>
					 <td>${table1.relapse1StandardMdrHiv }</td>
					 <td>${table1.relapse2StandardMdrHiv }</td>
					 <td>${table1.default1StandardMdrHiv }</td>
					 <td>${table1.default2StandardMdrHiv }</td>
					 <td>${table1.failure1StandardMdrHiv }</td>
					 <td>${table1.failure2StandardMdrHiv }</td>
					 <td>${table1.otherStandardMdrHiv }</td>
					 <td>${table1.totalStandardMdrHiv }</td>
					 
				</tr>
				<tr>
				    <td colspan="2"><spring:message code="mdrtb.tb07u.totalMdr"/></td>
				     <td>${table1.newMdr }</td>
					<td>${table1.relapse1Mdr }</td>
					<td>${table1.relapse2Mdr }</td>
					<td>${table1.default1Mdr }</td>
					<td>${table1.default2Mdr }</td>
					<td>${table1.failure1Mdr }</td>
					<td>${table1.failure2Mdr }</td>
					<td>${table1.otherMdr }</td>
					<td>${table1.totalMdr }</td>
				</tr>
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				    <td>${table1.newMdr04 }</td>
					<td>${table1.relapse1Mdr04 }</td>
					<td>${table1.relapse2Mdr04 }</td>
					<td>${table1.default1Mdr04 }</td>
					<td>${table1.default2Mdr04 }</td>
					<td>${table1.failure1Mdr04 }</td>
					<td>${table1.failure2Mdr04 }</td>
					<td>${table1.otherMdr04 }</td>
					<td>${table1.totalMdr04 }</td>
				</tr>
				<tr>
				     <td>5-14</td>
				    <td>${table1.newMdr0514 }</td>
					<td>${table1.relapse1Mdr0514 }</td>
					<td>${table1.relapse2Mdr0514 }</td>
					<td>${table1.default1Mdr0514 }</td>
					<td>${table1.default2Mdr0514 }</td>
					<td>${table1.failure1Mdr0514 }</td>
					<td>${table1.failure2Mdr0514 }</td>
					<td>${table1.otherMdr0514 }</td>
					<td>${table1.totalMdr0514 }</td>
				</tr>
				<tr>
				     <td>15-17</td>
				     <td>${table1.newMdr1517 }</td>
					<td>${table1.relapse1Mdr1517 }</td>
					<td>${table1.relapse2Mdr1517 }</td>
					<td>${table1.default1Mdr1517 }</td>
					<td>${table1.default2Mdr1517 }</td>
					<td>${table1.failure1Mdr1517 }</td>
					<td>${table1.failure2Mdr1517 }</td>
					<td>${table1.otherMdr1517 }</td>
					<td>${table1.totalMdr1517 }</td>
				</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				     <td>${table1.newMdrHiv }</td>
					<td>${table1.relapse1MdrHiv }</td>
					<td>${table1.relapse2MdrHiv }</td>
					<td>${table1.default1MdrHiv }</td>
					<td>${table1.default2MdrHiv }</td>
					<td>${table1.failure1MdrHiv }</td>
					<td>${table1.failure2MdrHiv }</td>
					<td>${table1.otherMdrHiv }</td>
					<td>${table1.totalMdrHiv }</td>
				</tr>
				
				
				<tr>
				    <td colspan="11" align="center"><spring:message code="mdrtb.tb07u.preXdrXdr"/></td> 
				</tr>
				<tr>
				   <td colspan="2"><spring:message code="mdrtb.tb07u.indLzd"/></td>
				   <td>${table1.newIndLzdXdrPreXdr }</td>
					 <td>${table1.relapse1IndLzdXdrPreXdr }</td>
					 <td>${table1.relapse2IndLzdXdrPreXdr }</td>
					 <td>${table1.default1IndLzdXdrPreXdr }</td>
					 <td>${table1.default2IndLzdXdrPreXdr }</td>
					 <td>${table1.failure1IndLzdXdrPreXdr }</td>
					 <td>${table1.failure2IndLzdXdrPreXdr }</td>
					 <td>${table1.otherIndLzdXdrPreXdr }</td>
					 <td>${table1.totalIndLzdXdrPreXdr }</td>
				</tr>
				
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				     <td>${table1.newIndLzdXdrPreXdr04 }</td>
					 <td>${table1.relapse1IndLzdXdrPreXdr04 }</td>
					 <td>${table1.relapse2IndLzdXdrPreXdr04 }</td>
					 <td>${table1.default1IndLzdXdrPreXdr04 }</td>
					 <td>${table1.default2IndLzdXdrPreXdr04 }</td>
					 <td>${table1.failure1IndLzdXdrPreXdr04 }</td>
					 <td>${table1.failure2IndLzdXdrPreXdr04 }</td>
					 <td>${table1.otherIndLzdXdrPreXdr04 }</td>
					 <td>${table1.totalIndLzdXdrPreXdr04 }</td>
				</tr>
				<tr>
				     <td>5-14</td>
				     <td>${table1.newIndLzdXdrPreXdr0514 }</td>
					 <td>${table1.relapse1IndLzdXdrPreXdr0514 }</td>
					 <td>${table1.relapse2IndLzdXdrPreXdr0514 }</td>
					 <td>${table1.default1IndLzdXdrPreXdr0514 }</td>
					 <td>${table1.default2IndLzdXdrPreXdr0514 }</td>
					 <td>${table1.failure1IndLzdXdrPreXdr0514 }</td>
					 <td>${table1.failure2IndLzdXdrPreXdr0514 }</td>
					 <td>${table1.otherIndLzdXdrPreXdr0514 }</td>
					 <td>${table1.totalIndLzdXdrPreXdr0514 }</td>
				</tr>
				<tr>
				     <td>15-17</td>
				     <td>${table1.newIndLzdXdrPreXdr1517 }</td>
					 <td>${table1.relapse1IndLzdXdrPreXdr1517 }</td>
					 <td>${table1.relapse2IndLzdXdrPreXdr1517 }</td>
					 <td>${table1.default1IndLzdXdrPreXdr1517 }</td>
					 <td>${table1.default2IndLzdXdrPreXdr1517 }</td>
					 <td>${table1.failure1IndLzdXdrPreXdr1517 }</td>
					 <td>${table1.failure2IndLzdXdrPreXdr1517 }</td>
					 <td>${table1.otherIndLzdXdrPreXdr1517 }</td>
					 <td>${table1.totalIndLzdXdrPreXdr1517 }</td>
				</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				     <td>${table1.newIndLzdXdrPreXdrHiv }</td>
					 <td>${table1.relapse1IndLzdXdrPreXdrHiv }</td>
					 <td>${table1.relapse2IndLzdXdrPreXdrHiv }</td>
					 <td>${table1.default1IndLzdXdrPreXdrHiv }</td>
					 <td>${table1.default2IndLzdXdrPreXdrHiv }</td>
					 <td>${table1.failure1IndLzdXdrPreXdrHiv }</td>
					 <td>${table1.failure2IndLzdXdrPreXdrHiv }</td>
					 <td>${table1.otherIndLzdXdrPreXdrHiv }</td>
					 <td>${table1.totalIndLzdXdrPreXdrHiv }</td>
					 
				</tr>
				
				<tr>
				    <td colspan="2"><spring:message code="mdrtb.tb07u.indBdq"/></td>
				   <td>${table1.newIndBdqXdrPreXdr }</td>
					 <td>${table1.relapse1IndBdqXdrPreXdr }</td>
					 <td>${table1.relapse2IndBdqXdrPreXdr }</td>
					 <td>${table1.default1IndBdqXdrPreXdr }</td>
					 <td>${table1.default2IndBdqXdrPreXdr }</td>
					 <td>${table1.failure1IndBdqXdrPreXdr }</td>
					 <td>${table1.failure2IndBdqXdrPreXdr }</td>
					 <td>${table1.otherIndBdqXdrPreXdr }</td>
					 <td>${table1.totalIndBdqXdrPreXdr }</td>
				</tr>
				
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				     <td>${table1.newIndBdqXdrPreXdr04 }</td>
					 <td>${table1.relapse1IndBdqXdrPreXdr04 }</td>
					 <td>${table1.relapse2IndBdqXdrPreXdr04 }</td>
					 <td>${table1.default1IndBdqXdrPreXdr04 }</td>
					 <td>${table1.default2IndBdqXdrPreXdr04 }</td>
					 <td>${table1.failure1IndBdqXdrPreXdr04 }</td>
					 <td>${table1.failure2IndBdqXdrPreXdr04 }</td>
					 <td>${table1.otherIndBdqXdrPreXdr04 }</td>
					 <td>${table1.totalIndBdqXdrPreXdr04 }</td>
				</tr>
				<tr>
				     <td>5-14</td>
				     <td>${table1.newIndBdqXdrPreXdr0514 }</td>
					 <td>${table1.relapse1IndBdqXdrPreXdr0514 }</td>
					 <td>${table1.relapse2IndBdqXdrPreXdr0514 }</td>
					 <td>${table1.default1IndBdqXdrPreXdr0514 }</td>
					 <td>${table1.default2IndBdqXdrPreXdr0514 }</td>
					 <td>${table1.failure1IndBdqXdrPreXdr0514 }</td>
					 <td>${table1.failure2IndBdqXdrPreXdr0514 }</td>
					 <td>${table1.otherIndBdqXdrPreXdr0514 }</td>
					 <td>${table1.totalIndBdqXdrPreXdr0514 }</td>
				</tr>
				<tr>
				     <td>15-17</td>
				     <td>${table1.newIndBdqXdrPreXdr1517 }</td>
					 <td>${table1.relapse1IndBdqXdrPreXdr1517 }</td>
					 <td>${table1.relapse2IndBdqXdrPreXdr1517 }</td>
					 <td>${table1.default1IndBdqXdrPreXdr1517 }</td>
					 <td>${table1.default2IndBdqXdrPreXdr1517 }</td>
					 <td>${table1.failure1IndBdqXdrPreXdr1517 }</td>
					 <td>${table1.failure2IndBdqXdrPreXdr1517 }</td>
					 <td>${table1.otherIndBdqXdrPreXdr1517 }</td>
					 <td>${table1.totalIndBdqXdrPreXdr1517 }</td>
				</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				     <td>${table1.newIndBdqXdrPreXdrHiv }</td>
					 <td>${table1.relapse1IndBdqXdrPreXdrHiv }</td>
					 <td>${table1.relapse2IndBdqXdrPreXdrHiv }</td>
					 <td>${table1.default1IndBdqXdrPreXdrHiv }</td>
					 <td>${table1.default2IndBdqXdrPreXdrHiv }</td>
					 <td>${table1.failure1IndBdqXdrPreXdrHiv }</td>
					 <td>${table1.failure2IndBdqXdrPreXdrHiv }</td>
					 <td>${table1.otherIndBdqXdrPreXdrHiv }</td>
					 <td>${table1.totalIndBdqXdrPreXdrHiv }</td>
					 
				</tr>
				<tr>
				    <td colspan="2"><spring:message code="mdrtb.tb07u.totalPreXdrXdr"/></td>
				    <td>${table1.newPreXdr + table1.newXdr}</td>
					<td>${table1.relapse1PreXdr + table1.relapse1Xdr }</td>
					<td>${table1.relapse2PreXdr + table1.relapse2Xdr }</td>
					<td>${table1.default1PreXdr + table1.default1Xdr }</td>
					<td>${table1.default2PreXdr + table1.default2Xdr }</td>
					<td>${table1.failure1PreXdr + table1.failure1Xdr }</td>
					<td>${table1.failure2PreXdr + table1.failure2Xdr }</td>
					<td>${table1.otherPreXdr + table1.otherXdr }</td>
					<td>${table1.totalPreXdr + table1.totalXdr }</td>

			</tr>
				<tr>
				     <td rowspan=3"><spring:message code="mdrtb.tb07u.ofThem"/><br/><spring:message code="mdrtb.tb07u.children"/></td>
				     <td>0-4</td>
				    <td>${table1.newPreXdr04  + table1.newXdr04  }</td>
				<td>${table1.relapse1PreXdr04 + table1.relapse1Xdr04 }</td>
				<td>${table1.relapse2PreXdr04 + table1.relapse2Xdr04 }</td>
				<td>${table1.default1PreXdr04 + table1.default1Xdr04 }</td>
				<td>${table1.default2PreXdr04 + table1.default2Xdr04 }</td>
				<td>${table1.failure1PreXdr04 + table1.failure1Xdr04 }</td>
				<td>${table1.failure2PreXdr04 + table1.failure2Xdr04 }</td>
				<td>${table1.otherPreXdr04 + table1.otherXdr04 }</td>
				<td>${table1.totalPreXdr04 + table1.totalXdr04 }</td>

			</tr>
				<tr>
				     <td>5-14</td>
				    <td>${table1.newPreXdr0514  + table1.newXdr0514  }</td>
				<td>${table1.relapse1PreXdr0514 + table1.relapse1Xdr0514 }</td>
				<td>${table1.relapse2PreXdr0514 + table1.relapse2Xdr0514 }</td>
				<td>${table1.default1PreXdr0514 + table1.default1Xdr0514 }</td>
				<td>${table1.default2PreXdr0514 + table1.default2Xdr0514 }</td>
				<td>${table1.failure1PreXdr0514 + table1.failure1Xdr0514 }</td>
				<td>${table1.failure2PreXdr0514 + table1.failure2Xdr0514 }</td>
				<td>${table1.otherPreXdr0514 + table1.otherXdr0514 }</td>
				<td>${table1.totalPreXdr0514 + table1.totalXdr0514 }</td>

			</tr>
				<tr>
				     <td>15-17</td>
				     <td>${table1.newPreXdr1517 }</td>
				<td>${table1.relapse1PreXdr1517 }</td>
				<td>${table1.relapse2PreXdr1517 }</td>
				<td>${table1.default1PreXdr1517 }</td>
				<td>${table1.default2PreXdr1517 }</td>
				<td>${table1.failure1PreXdr1517 }</td>
				<td>${table1.failure2PreXdr1517 }</td>
				<td>${table1.otherPreXdr1517 }</td>
				<td>${table1.totalPreXdr1517 }</td>

			</tr>
				<tr>
				     <td colspan=2"><spring:message code="mdrtb.tb07u.withHiv"/></td>
				     <td>${table1.newPreXdrHiv }</td>
				<td>${table1.relapse1PreXdrHiv }</td>
				<td>${table1.relapse2PreXdrHiv }</td>
				<td>${table1.default1PreXdrHiv }</td>
				<td>${table1.default2PreXdrHiv }</td>
				<td>${table1.failure1PreXdrHiv }</td>
				<td>${table1.failure2PreXdrHiv }</td>
				<td>${table1.otherPreXdrHiv }</td>
				<td>${table1.totalPreXdrHiv }</td>

			</tr>
			</table>
		</div>
		
		<input type="button" onclick="tableToExcel('tb07u', 'TB07u')" value="<spring:message code='mdrtb.exportToExcelBtn' />" />
		<!-- <input type="button" id="tableToPdf" name="tableToPdf" value="<spring:message code='mdrtb.exportToPdfBtn' />" /> -->
		<openmrs:hasPrivilege privilege="Manage Report Closing">
		<input type="button" id="tableToSql" name="tableToSql" value="<spring:message code='mdrtb.closeReportBtn' />" />
		<input type="button" onclick="printForm()" value="<spring:message code='mdrtb.print' />" />
		</openmrs:hasPrivilege>
		<script> 
			console.log("${reportStatus}");
			if("${reportStatus}" === "true") { 
				document.getElementById("tableToSql").disabled = true; 
			} else { 
				document.getElementById("tableToSql").disabled = false; 
			}
		</script>
		
	</body>
</html>
