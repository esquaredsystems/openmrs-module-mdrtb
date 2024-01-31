package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.pv.AdverseEventsRegisterData;
import org.openmrs.module.mdrtb.web.controller.reporting.AdverseEventsRegisterController;
import org.openmrs.module.mdrtb.web.dto.SimpleAdverseEventsRegisterData;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/adverseeventsregister", supportedClass = SimpleAdverseEventsRegisterData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class AdverseEventsDataResourceController extends DelegatingCrudResource<SimpleAdverseEventsRegisterData> implements Searchable {
	
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
		description.addProperty("patientIdentifier");
		description.addProperty("patientName");
		description.addProperty("birthdate");
		description.addProperty("onsetDate");
		description.addProperty("aeDescription");
		description.addProperty("diagnosticInvestigation");
		description.addProperty("serious");
		description.addProperty("ofSpecialInterest");
		description.addProperty("ancillaryDrugs");
		description.addProperty("doseChanged");
		description.addProperty("suspectedDrug");
		description.addProperty("suspectedDrugStartDate");
		description.addProperty("actionOutcome");
		description.addProperty("actionTaken");
		description.addProperty("txRegimen");
		description.addProperty("drugRechallenge");
		description.addProperty("yellowCardDate");
		description.addProperty("comments");
		description.addProperty("adverseEventsForm");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		return new DelegatingResourceDescription();
	}
	
	@Override
	public SimpleAdverseEventsRegisterData newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleAdverseEventsRegisterData save(SimpleAdverseEventsRegisterData delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleAdverseEventsRegisterData getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleAdverseEventsRegisterData delegate, String reason, RequestContext context)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleAdverseEventsRegisterData delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String yearStr = context.getRequest().getParameter("year");
		String quarterStr = context.getRequest().getParameter("quarter");
		String monthStr = context.getRequest().getParameter("month");
		String month2Str = context.getRequest().getParameter("month2");
		String locationUuid = context.getRequest().getParameter("location");
		// If conditions don't meet
		if (yearStr == null) {
			return new EmptySearchResult();
		}
		// Get location by UUID
		Location parent;
		List<Location> locList;
		if (locationUuid != null) {
			parent = Context.getLocationService().getLocationByUuid(locationUuid);
			// Get all child locations
			locList = Context.getService(MdrtbService.class).getLocationsInHierarchy(parent);
		}
		// Get all locations
		else {
			locList = Context.getLocationService().getAllLocations(false);
		}
		Integer year = Integer.parseInt(yearStr);
		Integer quarter = quarterStr == null ? null : Integer.parseInt(quarterStr);
		Integer month = monthStr == null ? null : Integer.parseInt(monthStr);
		Integer month2 = month2Str == null ? null : Integer.parseInt(month2Str);
		List<AdverseEventsRegisterData> aeRegister = AdverseEventsRegisterController.getAdverseEventsRegister(year, quarter, month, month2, locList);
		List<SimpleAdverseEventsRegisterData> list = new ArrayList<>();
		for (AdverseEventsRegisterData aeData : aeRegister) {
			list.add(new SimpleAdverseEventsRegisterData(aeData));
		}
		return new NeedsPaging<>(list, context);
	}
}
