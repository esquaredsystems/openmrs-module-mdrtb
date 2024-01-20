/**
 * 
 */
package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.web.controller.reporting.MissingTb03uController;
import org.openmrs.module.mdrtb.web.dto.SimpleTB03uMissingData;
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

/**
 * 
 */
@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb03umissingreport", supportedClass = SimpleTB03uMissingData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class TB03uMissingDataResourceController extends DelegatingCrudResource<SimpleTB03uMissingData> implements Searchable {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("errorCount");
		description.addProperty("errorPercentage");
		description.addProperty("totalCases");
		description.addProperty("dqItems");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		return delegatingResourceDescription;
	}
	
	@Override
	public SimpleTB03uMissingData newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB03uMissingData save(SimpleTB03uMissingData delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB03uMissingData getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleTB03uMissingData delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleTB03uMissingData delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String yearStr = context.getRequest().getParameter("year");
		String quarterStr = context.getRequest().getParameter("quarter");
		String monthStr = context.getRequest().getParameter("month");
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
		
		Map<String, Object> map = MissingTb03uController.getMissingTB03uPatientMap(year, quarter, month, locList);
		List<SimpleTB03uMissingData> list = new ArrayList<>();
		if (map.size() > 0) {
			list.add(new SimpleTB03uMissingData(map));
		}
		return new NeedsPaging<>(list, context);
	}
}
