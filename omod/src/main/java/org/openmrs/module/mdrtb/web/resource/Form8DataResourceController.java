package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table1Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table2Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table3Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table4Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table5aData;
import org.openmrs.module.mdrtb.reporting.custom.TB08Data;
import org.openmrs.module.mdrtb.web.controller.reporting.Form8Controller;
import org.openmrs.module.mdrtb.web.dto.SimpleForm8Data;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/form8report", supportedClass = SimpleForm8Data.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class Form8DataResourceController extends DelegatingCrudResource<SimpleForm8Data> implements Searchable {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("simpleForm8Table1Data");
		description.addProperty("simpleForm8Table2Data");
		description.addProperty("simpleForm8Table3Data");
		description.addProperty("simpleForm8Table4Data");
		description.addProperty("simpleForm8Table5aData");
		description.addProperty("simpleTB08Data");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		return delegatingResourceDescription;
	}
	
	@Override
	public SimpleForm8Data newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleForm8Data save(SimpleForm8Data delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleForm8Data getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleForm8Data delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleForm8Data delegate, RequestContext context) throws ResponseException {
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
		Map<String, Object> tableMap = Form8Controller.getForm8TableMap(locList, year, quarter, month, month2);
		Form8Table1Data form8Table1Data = (Form8Table1Data) tableMap.getOrDefault("table1", null);
		Form8Table2Data form8Table2Data = (Form8Table2Data) tableMap.getOrDefault("table2", null);
		Form8Table3Data form8Table3Data = (Form8Table3Data) tableMap.getOrDefault("table3", null);
		Form8Table4Data form8Table4Data = (Form8Table4Data) tableMap.getOrDefault("table4", null);
		Form8Table5aData form8Table5aData = (Form8Table5aData) tableMap.getOrDefault("table5a", null);
		TB08Data tb08TableData = (TB08Data) tableMap.getOrDefault("table6", null);
		SimpleForm8Data simpleForm8Data = new SimpleForm8Data(form8Table1Data, form8Table2Data, form8Table3Data,
		        form8Table4Data, form8Table5aData, tb08TableData);
		List<SimpleForm8Data> list = new ArrayList<>();
		list.add(simpleForm8Data);
		return new NeedsPaging<>(list, context);
	}
}
