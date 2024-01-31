package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.custom.DQItem;
import org.openmrs.module.mdrtb.web.controller.reporting.DOTSDQController;
import org.openmrs.module.mdrtb.web.controller.reporting.MDRDQController;
import org.openmrs.module.mdrtb.web.dto.SimpleDQItem;
import org.openmrs.module.mdrtb.web.dto.SimpleForm89Data;
import org.openmrs.module.webservices.rest.SimpleObject;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/dataquality", supportedClass = SimpleForm89Data.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class DataQualityResourceController extends DelegatingCrudResource<SimpleForm89Data> implements Searchable {
	
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
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		return delegatingResourceDescription;
	}
	
	@Override
	public SimpleForm89Data newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleForm89Data save(SimpleForm89Data delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleForm89Data getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleForm89Data delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleForm89Data delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String type = context.getRequest().getParameter("type");
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
		SimpleObject report = new SimpleObject();
		// Fetch the respective report
		Map<String, Object> metrics;
		if ("dots".equalsIgnoreCase(type)) {
			metrics = DOTSDQController.getDOTSQualityMetrics(year, quarter, month, month2, locList);
		} else {
			metrics = MDRDQController.getMDRQualityMetrics(year, quarter, month, month2, locList);
		}
		for(Entry<String, Object> entry : metrics.entrySet()) {
			// Check if the object is a List<DQItem> boxed inside it
			if (entry.getValue() instanceof List) {
				List<DQItem> dqItems = (List<DQItem>) metrics.get(entry.getKey());
				List<SimpleDQItem> simpleDQItems = new ArrayList<>();
				for (DQItem item : dqItems) {
					simpleDQItems.add(new SimpleDQItem(item));
				}
				report.add(entry.getKey(), simpleDQItems);
			} else {
				report.add(entry.getKey(), entry.getValue());
			}
		}
		List<SimpleObject> list = new ArrayList<>();
		list.add(report);
		return new NeedsPaging<>(list, context);
	}
}
