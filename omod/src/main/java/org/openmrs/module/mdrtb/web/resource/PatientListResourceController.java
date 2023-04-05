package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.web.controller.reporting.PatientListContoller;
import org.openmrs.module.mdrtb.web.dto.SimpleDataObject;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/patientlist", supportedClass = SimpleDataObject.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class PatientListResourceController extends DelegatingCrudResource<SimpleDataObject> implements Searchable {
	
	private static final String ALL_CASES_ENROLLED = "allenrolled";
	
	private static final String DOTS_CAES_BY_REGISTRATION_GROUP = "dotsbyregistrationgroup";
	
	private static final String DOTS_CASES_BY_ANATOMICAL_SITE = "dotsbyanatomicalsite";
	
	private static final String DOTS_CASES_BY_DRUG_RESISTANCE = "dotsbydrugresistance";
	
	private static final String DOTS_CASES_BY_REGISTRATION_GROUP_AND_BACTERIOLOGICAL_STATUS = "dotsbyregistrationgroupandbacteriologicalstatus";
	
	private static final String MDR_XDR_PATIENTS = "mdrxdrpatients";
	
	private static final String MDR_XDR_PATIENTS_WITH_NO_TREATMENT = "mdrxdrwithnotreatment";
	
	private static final String MDR_SUCCESSFUL_TREATMENT_OUTCOME = "mdrsuccessfultreatmentoutcome";
	
	private static final String WOMEN_OF_CHILD_BEARING_AGE = "womenofchildbearingage";
	
	private static final String MEN_OF_CONSCRIPT_AGE = "menofconscriptage";
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("stringData");
		description.addProperty("integerData");
		description.addProperty("floatData");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		return delegatingResourceDescription;
	}
	
	@Override
	public SimpleDataObject newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleDataObject save(SimpleDataObject delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleDataObject getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void delete(SimpleDataObject delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleDataObject delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String yearStr = context.getRequest().getParameter("year");
		String quarterStr = context.getRequest().getParameter("quarter");
		String monthStr = context.getRequest().getParameter("month");
		String locationUuid = context.getRequest().getParameter("location");
		String listName = context.getRequest().getParameter("listname");
		// If conditions don't meet
		if (listName == null || yearStr == null || locationUuid == null) {
			return new EmptySearchResult();
		}
		// Get location by UUID
		Location parent = Context.getLocationService().getLocationByUuid(locationUuid);
		// Get all child locations
		List<Location> locList = Context.getService(MdrtbService.class).getLocationsInHierarchy(parent);
		Integer year = Integer.parseInt(yearStr);
		Integer quarter = quarterStr == null ? null : Integer.parseInt(quarterStr);
		Integer month = monthStr == null ? null : Integer.parseInt(monthStr);
		List<SimpleDataObject> tableData = new ArrayList<SimpleDataObject>();
		String htmlTable = null;
		switch (listName.toLowerCase()) {
			case ALL_CASES_ENROLLED:
				htmlTable = PatientListContoller.getAllCasesEnrolledTable(locList, year, quarter, month);
				break;
			case DOTS_CAES_BY_REGISTRATION_GROUP:
				htmlTable = PatientListContoller.getDotsCasesByRegistrationGroupTable(locList, year, quarter, month);
				break;
			case DOTS_CASES_BY_ANATOMICAL_SITE:
				htmlTable = PatientListContoller.getDotsCasesByAnatomicalSiteTable(locList, year, quarter, month);
				break;
			case DOTS_CASES_BY_DRUG_RESISTANCE:
				htmlTable = PatientListContoller.getDotsCasesByDrugResistanceTable(locList, year, quarter, month);
				break;
			case DOTS_CASES_BY_REGISTRATION_GROUP_AND_BACTERIOLOGICAL_STATUS:
				htmlTable = PatientListContoller.getDotsPulmonaryCasesByRegisrationGroupAndBacteriologicalStatusTable(
				    locList, year, quarter, month);
				break;
			case MDR_XDR_PATIENTS:
				htmlTable = PatientListContoller.getMdrXdrPatientsTable(locList, year, quarter, month);
				break;
			case MDR_XDR_PATIENTS_WITH_NO_TREATMENT:
				htmlTable = PatientListContoller.getMdrXdrPatientsWithNoTreatmentTable(locList, year, quarter, month);
				break;
			case MDR_SUCCESSFUL_TREATMENT_OUTCOME:
				htmlTable = PatientListContoller.getMdrSuccessfulTreatmentOutcomeTable(locList, year, quarter, month);
				break;
			case WOMEN_OF_CHILD_BEARING_AGE:
				htmlTable = PatientListContoller.getWomenOfChildbearingAgeTable(locList, year, quarter, month);
				break;
			case MEN_OF_CONSCRIPT_AGE:
				htmlTable = PatientListContoller.getMenOfConscriptAgeTable(locList, year, quarter, month);
				break;
		}
		tableData.add(new SimpleDataObject(htmlTable, null, null));
		return new NeedsPaging<SimpleDataObject>(tableData, context);
	}
}
