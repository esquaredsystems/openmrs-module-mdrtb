package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.custom.TB03uData;
import org.openmrs.module.mdrtb.web.controller.reporting.TB03uController;
import org.openmrs.module.mdrtb.web.dto.SimpleTB03uData;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb03ureport", supportedClass = SimpleTB03uData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class TB03uDataResourceController extends DelegatingCrudResource<SimpleTB03uData> implements Searchable {
	
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
		description.addProperty("identifierDOTS");
		description.addProperty("dotsYear");
		description.addProperty("identifierMDR");
		description.addProperty("tb03uRegistrationDate");
		description.addProperty("ageAtTB03uRegistration");
		description.addProperty("dateOfBirth");
		description.addProperty("reg2Number");
		description.addProperty("siteOfDisease");
		description.addProperty("regGroup");
		description.addProperty("mdrtbStatus");
		description.addProperty("mdrConfDate");
		description.addProperty("treatmentRegimen");
		description.addProperty("tb03uTreatmentStartDate");
		description.addProperty("treatmentLocation");
		description.addProperty("dstCollectionDate");
		description.addProperty("dstResultDate");
		description.addProperty("drugResistance");
		description.addProperty("diagnosticMethod");
		description.addProperty("hivTestResult");
		description.addProperty("hivTestDate");
		description.addProperty("artStartDate");
		description.addProperty("cpStartDate");
		description.addProperty("month0SmearResult");
		description.addProperty("month1SmearResult");
		description.addProperty("month2SmearResult");
		description.addProperty("month3SmearResult");
		description.addProperty("month4SmearResult");
		description.addProperty("month5SmearResult");
		description.addProperty("month6SmearResult");
		description.addProperty("month7SmearResult");
		description.addProperty("month8SmearResult");
		description.addProperty("month9SmearResult");
		description.addProperty("month10SmearResult");
		description.addProperty("month11SmearResult");
		description.addProperty("month12SmearResult");
		description.addProperty("month15SmearResult");
		description.addProperty("month18SmearResult");
		description.addProperty("month21SmearResult");
		description.addProperty("month24SmearResult");
		description.addProperty("month27SmearResult");
		description.addProperty("month30SmearResult");
		description.addProperty("month33SmearResult");
		description.addProperty("month36SmearResult");
		description.addProperty("month0SmearResultDate");
		description.addProperty("month1SmearResultDate");
		description.addProperty("month2SmearResultDate");
		description.addProperty("month3SmearResultDate");
		description.addProperty("month4SmearResultDate");
		description.addProperty("month5SmearResultDate");
		description.addProperty("month6SmearResultDate");
		description.addProperty("month7SmearResultDate");
		description.addProperty("month8SmearResultDate");
		description.addProperty("month9SmearResultDate");
		description.addProperty("month10SmearResultDate");
		description.addProperty("month11SmearResultDate");
		description.addProperty("month12SmearResultDate");
		description.addProperty("month15SmearResultDate");
		description.addProperty("month18SmearResultDate");
		description.addProperty("month21SmearResultDate");
		description.addProperty("month24SmearResultDate");
		description.addProperty("month27SmearResultDate");
		description.addProperty("month30SmearResultDate");
		description.addProperty("month33SmearResultDate");
		description.addProperty("month36SmearResultDate");
		description.addProperty("month0CultureResult");
		description.addProperty("month1CultureResult");
		description.addProperty("month2CultureResult");
		description.addProperty("month3CultureResult");
		description.addProperty("month4CultureResult");
		description.addProperty("month5CultureResult");
		description.addProperty("month6CultureResult");
		description.addProperty("month7CultureResult");
		description.addProperty("month8CultureResult");
		description.addProperty("month9CultureResult");
		description.addProperty("month10CultureResult");
		description.addProperty("month11CultureResult");
		description.addProperty("month12CultureResult");
		description.addProperty("month15CultureResult");
		description.addProperty("month18CultureResult");
		description.addProperty("month21CultureResult");
		description.addProperty("month24CultureResult");
		description.addProperty("month27CultureResult");
		description.addProperty("month30CultureResult");
		description.addProperty("month33CultureResult");
		description.addProperty("month36CultureResult");
		description.addProperty("month0CultureResultDate");
		description.addProperty("month1CultureResultDate");
		description.addProperty("month2CultureResultDate");
		description.addProperty("month3CultureResultDate");
		description.addProperty("month4CultureResultDate");
		description.addProperty("month5CultureResultDate");
		description.addProperty("month6CultureResultDate");
		description.addProperty("month7CultureResultDate");
		description.addProperty("month8CultureResultDate");
		description.addProperty("month9CultureResultDate");
		description.addProperty("month10CultureResultDate");
		description.addProperty("month11CultureResultDate");
		description.addProperty("month12CultureResultDate");
		description.addProperty("month15CultureResultDate");
		description.addProperty("month18CultureResultDate");
		description.addProperty("month21CultureResultDate");
		description.addProperty("month24CultureResultDate");
		description.addProperty("month27CultureResultDate");
		description.addProperty("month30CultureResultDate");
		description.addProperty("month33CultureResultDate");
		description.addProperty("month36CultureResultDate");
		description.addProperty("tb03uTreatmentOutcome");
		description.addProperty("tb03uTreatmentOutcomeDate");
		description.addProperty("diedOfTB");
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
		description.addProperty("relapsed");
		description.addProperty("relapseMonth");
		description.addProperty("notes");
		description.addProperty("dstResults");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		return delegatingResourceDescription;
	}
	
	@Override
	public SimpleTB03uData newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB03uData save(SimpleTB03uData delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB03uData getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleTB03uData delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleTB03uData delegate, RequestContext context) throws ResponseException {
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
		List<TB03uData> patientSet = TB03uController.getTB03uPatientSet(year, quarter, month, locList);
		List<SimpleTB03uData> list = new ArrayList<SimpleTB03uData>();
		for (TB03uData SimpleTB03uData : patientSet) {
			list.add(new SimpleTB03uData(SimpleTB03uData));
		}
		return new NeedsPaging<SimpleTB03uData>(list, context);
	}
}
