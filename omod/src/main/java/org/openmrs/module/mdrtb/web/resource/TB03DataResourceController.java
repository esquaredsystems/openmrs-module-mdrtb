package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.custom.TB03Data;
import org.openmrs.module.mdrtb.web.controller.reporting.TB03ExportController;
import org.openmrs.module.mdrtb.web.dto.SimpleTB03Data;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb03report", supportedClass = SimpleTB03Data.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class TB03DataResourceController extends DelegatingCrudResource<SimpleTB03Data> implements Searchable {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("patientUuid");
		description.addProperty("personName");
		description.addProperty("personAddress");
		description.addProperty("gender");
		description.addProperty("identifier");
		description.addProperty("tb03RegistrationDate");
		description.addProperty("ageAtTB03Registration");
		description.addProperty("dateOfBirth");
		description.addProperty("intensivePhaseFacility");
		description.addProperty("continuationPhaseFacility");
		description.addProperty("treatmentRegimen");
		description.addProperty("tb03TreatmentStartDate");
		description.addProperty("siteOfDisease");
		description.addProperty("regGroup");
		description.addProperty("hivTestResult");
		description.addProperty("hivTestDate");
		description.addProperty("artStartDate");
		description.addProperty("cpStartDate");
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
		description.addProperty("cultureResult");
		description.addProperty("cultureTestDate");
		description.addProperty("cultureTestNumber");
		description.addProperty("cultureLab");
		description.addProperty("drugResistance");
		description.addProperty("month2SmearResult");
		description.addProperty("month2SmearDate");
		description.addProperty("month2TestNumber");
		description.addProperty("month2TestLab");
		description.addProperty("month3SmearResult");
		description.addProperty("month3SmearDate");
		description.addProperty("month3TestNumber");
		description.addProperty("month3TestLab");
		description.addProperty("month4SmearResult");
		description.addProperty("month4SmearDate");
		description.addProperty("month4TestNumber");
		description.addProperty("month4TestLab");
		description.addProperty("month5SmearResult");
		description.addProperty("month5SmearDate");
		description.addProperty("month5TestNumber");
		description.addProperty("month5TestLab");
		description.addProperty("month6SmearResult");
		description.addProperty("month6SmearDate");
		description.addProperty("month6TestNumber");
		description.addProperty("month6TestLab");
		description.addProperty("month8SmearResult");
		description.addProperty("month8SmearDate");
		description.addProperty("month8TestNumber");
		description.addProperty("month8TestLab");
		description.addProperty("tb03TreatmentOutcome");
		description.addProperty("tb03TreatmentOutcomeDate");
		description.addProperty("diedOfTB");
		description.addProperty("notes");
		description.addProperty("reg1New");
		description.addProperty("reg1Rtx");
		description.addProperty("dstCollectionDate");
		description.addProperty("dstResultDate");
		description.addProperty("dstResults");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		return delegatingResourceDescription;
	}
	
	@Override
	public SimpleTB03Data newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB03Data save(SimpleTB03Data delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB03Data getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleTB03Data delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleTB03Data delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String yearStr = context.getRequest().getParameter("year");
		String quarterStr = context.getRequest().getParameter("quarter");
		String monthStr = context.getRequest().getParameter("month");
		String locationUuid = context.getRequest().getParameter("location");
		// If conditions don't meet
		if (yearStr == null || locationUuid == null) {
			return new EmptySearchResult();
		}
		// Get location by UUID
		Location parent = Context.getLocationService().getLocationByUuid(locationUuid);
		// Get all child locations
		List<Location> locList = Context.getService(MdrtbService.class).getLocationsInHierarchy(parent);
		Integer year = Integer.parseInt(yearStr);
		Integer quarter = quarterStr == null ? null : Integer.parseInt(quarterStr);
		Integer month = monthStr == null ? null : Integer.parseInt(monthStr);
		List<TB03Data> patientSet = TB03ExportController.getTB03PatientSet(year, quarter, month, locList);
		List<SimpleTB03Data> list = new ArrayList<SimpleTB03Data>();
		for (TB03Data tb03Data : patientSet) {
			list.add(new SimpleTB03Data(tb03Data));
		}
		return new NeedsPaging<SimpleTB03Data>(list, context);
	}
}
