package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.module.mdrtb.reporting.custom.Form89Data;
import org.openmrs.module.mdrtb.web.controller.reporting.Form89SingleExportController;
import org.openmrs.module.mdrtb.web.dto.SimpleForm89Data;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/form89report", supportedClass = SimpleForm89Data.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class Form89DataResourceController extends BaseReportResource<SimpleForm89Data> {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("identifier");
		description.addProperty("tb03RegistrationDate");
		description.addProperty("ageAtTB03Registration");
		description.addProperty("dateOfBirth");
		description.addProperty("siteOfDisease");
		description.addProperty("dateFirstSeekingHelp");
		description.addProperty("dateOfReturn");
		description.addProperty("dateOfDecaySurvey");
		description.addProperty("cmacDate");
		description.addProperty("form89Date");
		description.addProperty("diagnosticSmearResult");
		description.addProperty("diagnosticSmearTestNumber");
		description.addProperty("diagnosticSmearDate");
		description.addProperty("diagnosticSmearLab");
		description.addProperty("xpertMTBResult");
		description.addProperty("xpertRIFResult");
		description.addProperty("xpertTestDate");
		description.addProperty("xpertTestNumber");
		description.addProperty("xpertLab");
		description.addProperty("hainMTBResult");
		description.addProperty("hainINHResult");
		description.addProperty("hainRIFResult");
		description.addProperty("hainTestDate");
		description.addProperty("hainTestNumber");
		description.addProperty("hainLab");
		description.addProperty("hain2MTBResult");
		description.addProperty("hain2InjResult");
		description.addProperty("hain2FqResult");
		description.addProperty("hain2TestDate");
		description.addProperty("hain2TestNumber");
		description.addProperty("hain2Lab");
		description.addProperty("form89Uuid");
		return description;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected PageableResult doSearch(RequestContext context) {
		final Map<String, Object> params = processParams(context);
		if (params == null) {
			return new EmptySearchResult();
		}
		List<Location> locList = (List<Location>) params.get("locations");
		Integer year = (Integer) params.get("year");
		Integer quarter = (Integer) params.get("quarter");
		Integer month = (Integer) params.get("month");
		Integer month2 = (Integer) params.get("month2");
		List<Form89Data> patientSet = Form89SingleExportController.getForm89PatientSet(year, quarter, month, month2, locList);
		List<SimpleForm89Data> list = new ArrayList<>();
		for (Form89Data form89Data : patientSet) {
			list.add(new SimpleForm89Data(form89Data));
		}
		return new NeedsPaging<>(list, context);
	}
}
