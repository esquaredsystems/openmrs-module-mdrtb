package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.custom.TB07uData;
import org.openmrs.module.mdrtb.web.controller.reporting.TB07uController;
import org.openmrs.module.mdrtb.web.dto.SimpleTB07uData;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb07ureport", supportedClass = SimpleTB07uData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class TB07uDataResourceController extends DelegatingCrudResource<SimpleTB07uData> implements Searchable {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("totalDetections");
		description.addProperty("mdrDetections");
		description.addProperty("pdrDetections");
		description.addProperty("preXdrDetections");
		description.addProperty("xdrDetections");
		description.addProperty("newMdr");
		description.addProperty("newMdr04");
		description.addProperty("newMdr0514");
		description.addProperty("newMdr1517");
		description.addProperty("newMdrHiv");
		description.addProperty("newShortMdr");
		description.addProperty("newShortMdr04");
		description.addProperty("newShortMdr0514");
		description.addProperty("newShortMdr1517");
		description.addProperty("newShortMdrHiv");
		description.addProperty("newStandardMdr");
		description.addProperty("newStandardMdr04");
		description.addProperty("newStandardMdr0514");
		description.addProperty("newStandardMdr1517");
		description.addProperty("newStandardMdrHiv");
		description.addProperty("relapse1Mdr");
		description.addProperty("relapse1Mdr04");
		description.addProperty("relapse1Mdr0514");
		description.addProperty("relapse1Mdr1517");
		description.addProperty("relapse1MdrHiv");
		description.addProperty("relapse1ShortMdr");
		description.addProperty("relapse1ShortMdr04");
		description.addProperty("relapse1ShortMdr0514");
		description.addProperty("relapse1ShortMdr1517");
		description.addProperty("relapse1ShortMdrHiv");
		description.addProperty("relapse1StandardMdr");
		description.addProperty("relapse1StandardMdr04");
		description.addProperty("relapse1StandardMdr0514");
		description.addProperty("relapse1StandardMdr1517");
		description.addProperty("relapse1StandardMdrHiv");
		description.addProperty("relapse2Mdr");
		description.addProperty("relapse2Mdr04");
		description.addProperty("relapse2Mdr0514");
		description.addProperty("relapse2Mdr1517");
		description.addProperty("relapse2MdrHiv");
		description.addProperty("relapse2ShortMdr");
		description.addProperty("relapse2ShortMdr04");
		description.addProperty("relapse2ShortMdr0514");
		description.addProperty("relapse2ShortMdr1517");
		description.addProperty("relapse2ShortMdrHiv");
		description.addProperty("relapse2StandardMdr");
		description.addProperty("relapse2StandardMdr04");
		description.addProperty("relapse2StandardMdr0514");
		description.addProperty("relapse2StandardMdr1517");
		description.addProperty("relapse2StandardMdrHiv");
		description.addProperty("default1Mdr");
		description.addProperty("default1Mdr04");
		description.addProperty("default1Mdr0514");
		description.addProperty("default1Mdr1517");
		description.addProperty("default1MdrHiv");
		description.addProperty("default1ShortMdr");
		description.addProperty("default1ShortMdr04");
		description.addProperty("default1ShortMdr0514");
		description.addProperty("default1ShortMdr1517");
		description.addProperty("default1ShortMdrHiv");
		description.addProperty("default1StandardMdr");
		description.addProperty("default1StandardMdr04");
		description.addProperty("default1StandardMdr0514");
		description.addProperty("default1StandardMdr1517");
		description.addProperty("default1StandardMdrHiv");
		description.addProperty("default2Mdr");
		description.addProperty("default2Mdr04");
		description.addProperty("default2Mdr0514");
		description.addProperty("default2Mdr1517");
		description.addProperty("default2MdrHiv");
		description.addProperty("default2ShortMdr");
		description.addProperty("default2ShortMdr04");
		description.addProperty("default2ShortMdr0514");
		description.addProperty("default2ShortMdr1517");
		description.addProperty("default2ShortMdrHiv");
		description.addProperty("default2StandardMdr");
		description.addProperty("default2StandardMdr04");
		description.addProperty("default2StandardMdr0514");
		description.addProperty("default2StandardMdr1517");
		description.addProperty("default2StandardMdrHiv");
		description.addProperty("failure1Mdr");
		description.addProperty("failure1Mdr04");
		description.addProperty("failure1Mdr0514");
		description.addProperty("failure1Mdr1517");
		description.addProperty("failure1MdrHiv");
		description.addProperty("failure1ShortMdr");
		description.addProperty("failure1ShortMdr04");
		description.addProperty("failure1ShortMdr0514");
		description.addProperty("failure1ShortMdr1517");
		description.addProperty("failure1ShortMdrHiv");
		description.addProperty("failure1StandardMdr");
		description.addProperty("failure1StandardMdr04");
		description.addProperty("failure1StandardMdr0514");
		description.addProperty("failure1StandardMdr1517");
		description.addProperty("failure1StandardMdrHiv");
		description.addProperty("failure2Mdr");
		description.addProperty("failure2Mdr04");
		description.addProperty("failure2Mdr0514");
		description.addProperty("failure2Mdr1517");
		description.addProperty("failure2MdrHiv");
		description.addProperty("failure2ShortMdr");
		description.addProperty("failure2ShortMdr04");
		description.addProperty("failure2ShortMdr0514");
		description.addProperty("failure2ShortMdr1517");
		description.addProperty("failure2ShortMdrHiv");
		description.addProperty("failure2StandardMdr");
		description.addProperty("failure2StandardMdr04");
		description.addProperty("failure2StandardMdr0514");
		description.addProperty("failure2StandardMdr1517");
		description.addProperty("failure2StandardMdrHiv");
		description.addProperty("otherMdr");
		description.addProperty("otherMdr04");
		description.addProperty("otherMdr0514");
		description.addProperty("otherMdr1517");
		description.addProperty("otherMdrHiv");
		description.addProperty("otherShortMdr");
		description.addProperty("otherShortMdr04");
		description.addProperty("otherShortMdr0514");
		description.addProperty("otherShortMdr1517");
		description.addProperty("otherShortMdrHiv");
		description.addProperty("otherStandardMdr");
		description.addProperty("otherStandardMdr04");
		description.addProperty("otherStandardMdr0514");
		description.addProperty("otherStandardMdr1517");
		description.addProperty("otherStandardMdrHiv");
		description.addProperty("totalMdr");
		description.addProperty("totalMdr04");
		description.addProperty("totalMdr0514");
		description.addProperty("totalMdr1517");
		description.addProperty("totalMdrHiv");
		description.addProperty("totalShortMdr");
		description.addProperty("totalShortMdr04");
		description.addProperty("totalShortMdr0514");
		description.addProperty("totalShortMdr1517");
		description.addProperty("totalShortMdrHiv");
		description.addProperty("totalStandardMdr");
		description.addProperty("totalStandardMdr04");
		description.addProperty("totalStandardMdr0514");
		description.addProperty("totalStandardMdr1517");
		description.addProperty("totalStandardMdrHiv");
		description.addProperty("newPdr");
		description.addProperty("newPdr04");
		description.addProperty("newPdr0514");
		description.addProperty("newPdr1517");
		description.addProperty("newPdrHiv");
		description.addProperty("relapse1Pdr");
		description.addProperty("relapse1Pdr04");
		description.addProperty("relapse1Pdr0514");
		description.addProperty("relapse1Pdr1517");
		description.addProperty("relapse1PdrHiv");
		description.addProperty("relapse2Pdr");
		description.addProperty("relapse2Pdr04");
		description.addProperty("relapse2Pdr0514");
		description.addProperty("relapse2Pdr1517");
		description.addProperty("relapse2PdrHiv");
		description.addProperty("default1Pdr");
		description.addProperty("default1Pdr04");
		description.addProperty("default1Pdr0514");
		description.addProperty("default1Pdr1517");
		description.addProperty("default1PdrHiv");
		description.addProperty("default2Pdr");
		description.addProperty("default2Pdr04");
		description.addProperty("default2Pdr0514");
		description.addProperty("default2Pdr1517");
		description.addProperty("default2PdrHiv");
		description.addProperty("failure1Pdr");
		description.addProperty("failure1Pdr04");
		description.addProperty("failure1Pdr0514");
		description.addProperty("failure1Pdr1517");
		description.addProperty("failure1PdrHiv");
		description.addProperty("failure2Pdr");
		description.addProperty("failure2Pdr04");
		description.addProperty("failure2Pdr0514");
		description.addProperty("failure2Pdr1517");
		description.addProperty("failure2PdrHiv");
		description.addProperty("otherPdr");
		description.addProperty("otherPdr04");
		description.addProperty("otherPdr0514");
		description.addProperty("otherPdr1517");
		description.addProperty("otherPdrHiv");
		description.addProperty("totalPdr");
		description.addProperty("totalPdr04");
		description.addProperty("totalPdr0514");
		description.addProperty("totalPdr1517");
		description.addProperty("totalPdrHiv");
		description.addProperty("newPreXdr");
		description.addProperty("newPreXdr04");
		description.addProperty("newPreXdr0514");
		description.addProperty("newPreXdr1517");
		description.addProperty("newPreXdrHiv");
		description.addProperty("relapse1PreXdr");
		description.addProperty("relapse1PreXdr04");
		description.addProperty("relapse1PreXdr0514");
		description.addProperty("relapse1PreXdr1517");
		description.addProperty("relapse1PreXdrHiv");
		description.addProperty("relapse2PreXdr");
		description.addProperty("relapse2PreXdr04");
		description.addProperty("relapse2PreXdr0514");
		description.addProperty("relapse2PreXdr1517");
		description.addProperty("relapse2PreXdrHiv");
		description.addProperty("default1PreXdr");
		description.addProperty("default1PreXdr04");
		description.addProperty("default1PreXdr0514");
		description.addProperty("default1PreXdr1517");
		description.addProperty("default1PreXdrHiv");
		description.addProperty("default2PreXdr");
		description.addProperty("default2PreXdr04");
		description.addProperty("default2PreXdr0514");
		description.addProperty("default2PreXdr1517");
		description.addProperty("default2PreXdrHiv");
		description.addProperty("failure1PreXdr");
		description.addProperty("failure1PreXdr04");
		description.addProperty("failure1PreXdr0514");
		description.addProperty("failure1PreXdr1517");
		description.addProperty("failure1PreXdrHiv");
		description.addProperty("failure2PreXdr");
		description.addProperty("failure2PreXdr04");
		description.addProperty("failure2PreXdr0514");
		description.addProperty("failure2PreXdr1517");
		description.addProperty("failure2PreXdrHiv");
		description.addProperty("otherPreXdr");
		description.addProperty("otherPreXdr04");
		description.addProperty("otherPreXdr0514");
		description.addProperty("otherPreXdr1517");
		description.addProperty("otherPreXdrHiv");
		description.addProperty("totalPreXdr");
		description.addProperty("totalPreXdr04");
		description.addProperty("totalPreXdr0514");
		description.addProperty("totalPreXdr1517");
		description.addProperty("totalPreXdrHiv");
		description.addProperty("newXdr");
		description.addProperty("newXdr04");
		description.addProperty("newXdr0514");
		description.addProperty("newXdr1517");
		description.addProperty("newXdrHiv");
		description.addProperty("relapse1Xdr");
		description.addProperty("relapse1Xdr04");
		description.addProperty("relapse1Xdr0514");
		description.addProperty("relapse1Xdr1517");
		description.addProperty("relapse1XdrHiv");
		description.addProperty("relapse2Xdr");
		description.addProperty("relapse2Xdr04");
		description.addProperty("relapse2Xdr0514");
		description.addProperty("relapse2Xdr1517");
		description.addProperty("relapse2XdrHiv");
		description.addProperty("default1Xdr");
		description.addProperty("default1Xdr04");
		description.addProperty("default1Xdr0514");
		description.addProperty("default1Xdr1517");
		description.addProperty("default1XdrHiv");
		description.addProperty("default2Xdr");
		description.addProperty("default2Xdr04");
		description.addProperty("default2Xdr0514");
		description.addProperty("default2Xdr1517");
		description.addProperty("default2XdrHiv");
		description.addProperty("failure1Xdr");
		description.addProperty("failure1Xdr04");
		description.addProperty("failure1Xdr0514");
		description.addProperty("failure1Xdr1517");
		description.addProperty("failure1XdrHiv");
		description.addProperty("failure2Xdr");
		description.addProperty("failure2Xdr04");
		description.addProperty("failure2Xdr0514");
		description.addProperty("failure2Xdr1517");
		description.addProperty("failure2XdrHiv");
		description.addProperty("otherXdr");
		description.addProperty("otherXdr04");
		description.addProperty("otherXdr0514");
		description.addProperty("otherXdr1517");
		description.addProperty("otherXdrHiv");
		description.addProperty("totalXdr");
		description.addProperty("totalXdr04");
		description.addProperty("totalXdr0514");
		description.addProperty("totalXdr1517");
		description.addProperty("totalXdrHiv");
		description.addProperty("newTotal");
		description.addProperty("newTotal04");
		description.addProperty("newTotal0514");
		description.addProperty("newTotal1517");
		description.addProperty("newTotalHiv");
		description.addProperty("relapse1Total");
		description.addProperty("relapse1Total04");
		description.addProperty("relapse1Total0514");
		description.addProperty("relapse1Total1517");
		description.addProperty("relapse1TotalHiv");
		description.addProperty("relapse2Total");
		description.addProperty("relapse2Total04");
		description.addProperty("relapse2Total0514");
		description.addProperty("relapse2Total1517");
		description.addProperty("relapse2TotalHiv");
		description.addProperty("default1Total");
		description.addProperty("default1Total04");
		description.addProperty("default1Total0514");
		description.addProperty("default1Total1517");
		description.addProperty("default1TotalHiv");
		description.addProperty("default2Total");
		description.addProperty("default2Total04");
		description.addProperty("default2Total0514");
		description.addProperty("default2Total1517");
		description.addProperty("default2TotalHiv");
		description.addProperty("failure1Total");
		description.addProperty("failure1Total04");
		description.addProperty("failure1Total0514");
		description.addProperty("failure1Total1517");
		description.addProperty("failure1TotalHiv");
		description.addProperty("failure2Total");
		description.addProperty("failure2Total04");
		description.addProperty("failure2Total0514");
		description.addProperty("failure2Total1517");
		description.addProperty("failure2TotalHiv");
		description.addProperty("otherTotal");
		description.addProperty("otherTotal04");
		description.addProperty("otherTotal0514");
		description.addProperty("otherTotal1517");
		description.addProperty("otherTotalHiv");
		description.addProperty("totalTotal");
		description.addProperty("totalTotal04");
		description.addProperty("totalTotal0514");
		description.addProperty("totalTotal1517");
		description.addProperty("totalTotalHiv");
		description.addProperty("newIndLzdXdrPreXdr");
		description.addProperty("newIndLzdXdrPreXdr04");
		description.addProperty("newIndLzdXdrPreXdr0514");
		description.addProperty("newIndLzdXdrPreXdr1517");
		description.addProperty("newIndLzdXdrPreXdrHiv");
		description.addProperty("newIndBdqXdrPreXdr");
		description.addProperty("newIndBdqXdrPreXdr04");
		description.addProperty("newIndBdqXdrPreXdr0514");
		description.addProperty("newIndBdqXdrPreXdr1517");
		description.addProperty("newIndBdqXdrPreXdrHiv");
		description.addProperty("relapse1IndLzdXdrPreXdr");
		description.addProperty("relapse1IndLzdXdrPreXdr04");
		description.addProperty("relapse1IndLzdXdrPreXdr0514");
		description.addProperty("relapse1IndLzdXdrPreXdr1517");
		description.addProperty("relapse1IndLzdXdrPreXdrHiv");
		description.addProperty("relapse1IndBdqXdrPreXdr");
		description.addProperty("relapse1IndBdqXdrPreXdr04");
		description.addProperty("relapse1IndBdqXdrPreXdr0514");
		description.addProperty("relapse1IndBdqXdrPreXdr1517");
		description.addProperty("relapse1IndBdqXdrPreXdrHiv");
		description.addProperty("relapse2IndLzdXdrPreXdr");
		description.addProperty("relapse2IndLzdXdrPreXdr04");
		description.addProperty("relapse2IndLzdXdrPreXdr0514");
		description.addProperty("relapse2IndLzdXdrPreXdr1517");
		description.addProperty("relapse2IndLzdXdrPreXdrHiv");
		description.addProperty("relapse2IndBdqXdrPreXdr");
		description.addProperty("relapse2IndBdqXdrPreXdr04");
		description.addProperty("relapse2IndBdqXdrPreXdr0514");
		description.addProperty("relapse2IndBdqXdrPreXdr1517");
		description.addProperty("relapse2IndBdqXdrPreXdrHiv");
		description.addProperty("default1IndLzdXdrPreXdr");
		description.addProperty("default1IndLzdXdrPreXdr04");
		description.addProperty("default1IndLzdXdrPreXdr0514");
		description.addProperty("default1IndLzdXdrPreXdr1517");
		description.addProperty("default1IndLzdXdrPreXdrHiv");
		description.addProperty("default1IndBdqXdrPreXdr");
		description.addProperty("default1IndBdqXdrPreXdr04");
		description.addProperty("default1IndBdqXdrPreXdr0514");
		description.addProperty("default1IndBdqXdrPreXdr1517");
		description.addProperty("default1IndBdqXdrPreXdrHiv");
		description.addProperty("default2IndLzdXdrPreXdr");
		description.addProperty("default2IndLzdXdrPreXdr04");
		description.addProperty("default2IndLzdXdrPreXdr0514");
		description.addProperty("default2IndLzdXdrPreXdr1517");
		description.addProperty("default2IndLzdXdrPreXdrHiv");
		description.addProperty("default2IndBdqXdrPreXdr");
		description.addProperty("default2IndBdqXdrPreXdr04");
		description.addProperty("default2IndBdqXdrPreXdr0514");
		description.addProperty("default2IndBdqXdrPreXdr1517");
		description.addProperty("default2IndBdqXdrPreXdrHiv");
		description.addProperty("failure1IndLzdXdrPreXdr");
		description.addProperty("failure1IndLzdXdrPreXdr04");
		description.addProperty("failure1IndLzdXdrPreXdr0514");
		description.addProperty("failure1IndLzdXdrPreXdr1517");
		description.addProperty("failure1IndLzdXdrPreXdrHiv");
		description.addProperty("failure1IndBdqXdrPreXdr");
		description.addProperty("failure1IndBdqXdrPreXdr04");
		description.addProperty("failure1IndBdqXdrPreXdr0514");
		description.addProperty("failure1IndBdqXdrPreXdr1517");
		description.addProperty("failure1IndBdqXdrPreXdrHiv");
		description.addProperty("failure2IndLzdXdrPreXdr");
		description.addProperty("failure2IndLzdXdrPreXdr04");
		description.addProperty("failure2IndLzdXdrPreXdr0514");
		description.addProperty("failure2IndLzdXdrPreXdr1517");
		description.addProperty("failure2IndLzdXdrPreXdrHiv");
		description.addProperty("failure2IndBdqXdrPreXdr");
		description.addProperty("failure2IndBdqXdrPreXdr04");
		description.addProperty("failure2IndBdqXdrPreXdr0514");
		description.addProperty("failure2IndBdqXdrPreXdr1517");
		description.addProperty("failure2IndBdqXdrPreXdrHiv");
		description.addProperty("otherIndLzdXdrPreXdr");
		description.addProperty("otherIndLzdXdrPreXdr04");
		description.addProperty("otherIndLzdXdrPreXdr0514");
		description.addProperty("otherIndLzdXdrPreXdr1517");
		description.addProperty("otherIndLzdXdrPreXdrHiv");
		description.addProperty("otherIndBdqXdrPreXdr");
		description.addProperty("otherIndBdqXdrPreXdr04");
		description.addProperty("otherIndBdqXdrPreXdr0514");
		description.addProperty("otherIndBdqXdrPreXdr1517");
		description.addProperty("otherIndBdqXdrPreXdrHiv");
		description.addProperty("totalIndLzdXdrPreXdr");
		description.addProperty("totalIndLzdXdrPreXdr04");
		description.addProperty("totalIndLzdXdrPreXdr0514");
		description.addProperty("totalIndLzdXdrPreXdr1517");
		description.addProperty("totalIndLzdXdrPreXdrHiv");
		description.addProperty("totalIndBdqXdrPreXdr");
		description.addProperty("totalIndBdqXdrPreXdr04");
		description.addProperty("totalIndBdqXdrPreXdr0514");
		description.addProperty("totalIndBdqXdrPreXdr1517");
		description.addProperty("totalIndBdqXdrPreXdrHiv");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		return delegatingResourceDescription;
	}
	
	@Override
	public SimpleTB07uData newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB07uData save(SimpleTB07uData delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB07uData getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleTB07uData delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleTB07uData delegate, RequestContext context) throws ResponseException {
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
		TB07uData TB07uData = TB07uController.getTB07uPatientSet(locList, year, quarter, month, month2);
		List<SimpleTB07uData> list = new ArrayList<>();
		list.add(new SimpleTB07uData(TB07uData));
		return new NeedsPaging<>(list, context);
	}
}
