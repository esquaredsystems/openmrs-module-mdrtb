package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.custom.TB08uData;
import org.openmrs.module.mdrtb.web.controller.reporting.TB08uController;
import org.openmrs.module.mdrtb.web.dto.SimpleTB08uData;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb08ureport", supportedClass = SimpleTB08uData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class TB08uDataResourceController extends DelegatingCrudResource<SimpleTB08uData> implements Searchable {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("newRegisteredShort");
		description.addProperty("newCuredShort");
		description.addProperty("newCompletedShort");
		description.addProperty("newTxSuccessShort");
		description.addProperty("newDiedTBShort");
		description.addProperty("newDiedNotTBShort");
		description.addProperty("newFailedShort");
		description.addProperty("newDefaultedShort");
		description.addProperty("newNotAssessedShort");
		description.addProperty("newTotalShort");
		description.addProperty("relapse1RegisteredShort");
		description.addProperty("relapse1CuredShort");
		description.addProperty("relapse1CompletedShort");
		description.addProperty("relapse1TxSuccessShort");
		description.addProperty("relapse1DiedTBShort");
		description.addProperty("relapse1DiedNotTBShort");
		description.addProperty("relapse1FailedShort");
		description.addProperty("relapse1DefaultedShort");
		description.addProperty("relapse1NotAssessedShort");
		description.addProperty("relapse1TotalShort");
		description.addProperty("relapse2RegisteredShort");
		description.addProperty("relapse2CuredShort");
		description.addProperty("relapse2CompletedShort");
		description.addProperty("relapse2TxSuccessShort");
		description.addProperty("relapse2DiedTBShort");
		description.addProperty("relapse2DiedNotTBShort");
		description.addProperty("relapse2FailedShort");
		description.addProperty("relapse2DefaultedShort");
		description.addProperty("relapse2NotAssessedShort");
		description.addProperty("relapse2TotalShort");
		description.addProperty("default1RegisteredShort");
		description.addProperty("default1CuredShort");
		description.addProperty("default1CompletedShort");
		description.addProperty("default1TxSuccessShort");
		description.addProperty("default1DiedTBShort");
		description.addProperty("default1DiedNotTBShort");
		description.addProperty("default1FailedShort");
		description.addProperty("default1DefaultedShort");
		description.addProperty("default1NotAssessedShort");
		description.addProperty("default1TotalShort");
		description.addProperty("default2RegisteredShort");
		description.addProperty("default2CuredShort");
		description.addProperty("default2CompletedShort");
		description.addProperty("default2TxSuccessShort");
		description.addProperty("default2DiedTBShort");
		description.addProperty("default2DiedNotTBShort");
		description.addProperty("default2FailedShort");
		description.addProperty("default2DefaultedShort");
		description.addProperty("default2NotAssessedShort");
		description.addProperty("default2TotalShort");
		description.addProperty("failure1RegisteredShort");
		description.addProperty("failure1CuredShort");
		description.addProperty("failure1CompletedShort");
		description.addProperty("failure1TxSuccessShort");
		description.addProperty("failure1DiedTBShort");
		description.addProperty("failure1DiedNotTBShort");
		description.addProperty("failure1FailedShort");
		description.addProperty("failure1DefaultedShort");
		description.addProperty("failure1NotAssessedShort");
		description.addProperty("failure1TotalShort");
		description.addProperty("failure2RegisteredShort");
		description.addProperty("failure2CuredShort");
		description.addProperty("failure2CompletedShort");
		description.addProperty("failure2TxSuccessShort");
		description.addProperty("failure2DiedTBShort");
		description.addProperty("failure2DiedNotTBShort");
		description.addProperty("failure2FailedShort");
		description.addProperty("failure2DefaultedShort");
		description.addProperty("failure2NotAssessedShort");
		description.addProperty("failure2TotalShort");
		description.addProperty("otherRegisteredShort");
		description.addProperty("otherCuredShort");
		description.addProperty("otherCompletedShort");
		description.addProperty("otherTxSuccessShort");
		description.addProperty("otherDiedTBShort");
		description.addProperty("otherDiedNotTBShort");
		description.addProperty("otherFailedShort");
		description.addProperty("otherDefaultedShort");
		description.addProperty("otherNotAssessedShort");
		description.addProperty("otherTotalShort");
		description.addProperty("totalRegisteredShort");
		description.addProperty("totalCuredShort");
		description.addProperty("totalCompletedShort");
		description.addProperty("totalTxSuccessShort");
		description.addProperty("totalDiedTBShort");
		description.addProperty("totalDiedNotTBShort");
		description.addProperty("totalFailedShort");
		description.addProperty("totalDefaultedShort");
		description.addProperty("totalNotAssessedShort");
		description.addProperty("totalTotalShort");
		description.addProperty("newRegisteredIndiv");
		description.addProperty("newCuredIndiv");
		description.addProperty("newCompletedIndiv");
		description.addProperty("newTxSuccessIndiv");
		description.addProperty("newDiedTBIndiv");
		description.addProperty("newDiedNotTBIndiv");
		description.addProperty("newFailedIndiv");
		description.addProperty("newDefaultedIndiv");
		description.addProperty("newNotAssessedIndiv");
		description.addProperty("newTotalIndiv");
		description.addProperty("relapse1RegisteredIndiv");
		description.addProperty("relapse1CuredIndiv");
		description.addProperty("relapse1CompletedIndiv");
		description.addProperty("relapse1TxSuccessIndiv");
		description.addProperty("relapse1DiedTBIndiv");
		description.addProperty("relapse1DiedNotTBIndiv");
		description.addProperty("relapse1FailedIndiv");
		description.addProperty("relapse1DefaultedIndiv");
		description.addProperty("relapse1NotAssessedIndiv");
		description.addProperty("relapse1TotalIndiv");
		description.addProperty("relapse2RegisteredIndiv");
		description.addProperty("relapse2CuredIndiv");
		description.addProperty("relapse2CompletedIndiv");
		description.addProperty("relapse2TxSuccessIndiv");
		description.addProperty("relapse2DiedTBIndiv");
		description.addProperty("relapse2DiedNotTBIndiv");
		description.addProperty("relapse2FailedIndiv");
		description.addProperty("relapse2DefaultedIndiv");
		description.addProperty("relapse2NotAssessedIndiv");
		description.addProperty("relapse2TotalIndiv");
		description.addProperty("default1RegisteredIndiv");
		description.addProperty("default1CuredIndiv");
		description.addProperty("default1CompletedIndiv");
		description.addProperty("default1TxSuccessIndiv");
		description.addProperty("default1DiedTBIndiv");
		description.addProperty("default1DiedNotTBIndiv");
		description.addProperty("default1FailedIndiv");
		description.addProperty("default1DefaultedIndiv");
		description.addProperty("default1NotAssessedIndiv");
		description.addProperty("default1TotalIndiv");
		description.addProperty("default2RegisteredIndiv");
		description.addProperty("default2CuredIndiv");
		description.addProperty("default2CompletedIndiv");
		description.addProperty("default2TxSuccessIndiv");
		description.addProperty("default2DiedTBIndiv");
		description.addProperty("default2DiedNotTBIndiv");
		description.addProperty("default2FailedIndiv");
		description.addProperty("default2DefaultedIndiv");
		description.addProperty("default2NotAssessedIndiv");
		description.addProperty("default2TotalIndiv");
		description.addProperty("failure1RegisteredIndiv");
		description.addProperty("failure1CuredIndiv");
		description.addProperty("failure1CompletedIndiv");
		description.addProperty("failure1TxSuccessIndiv");
		description.addProperty("failure1DiedTBIndiv");
		description.addProperty("failure1DiedNotTBIndiv");
		description.addProperty("failure1FailedIndiv");
		description.addProperty("failure1DefaultedIndiv");
		description.addProperty("failure1NotAssessedIndiv");
		description.addProperty("failure1TotalIndiv");
		description.addProperty("failure2RegisteredIndiv");
		description.addProperty("failure2CuredIndiv");
		description.addProperty("failure2CompletedIndiv");
		description.addProperty("failure2TxSuccessIndiv");
		description.addProperty("failure2DiedTBIndiv");
		description.addProperty("failure2DiedNotTBIndiv");
		description.addProperty("failure2FailedIndiv");
		description.addProperty("failure2DefaultedIndiv");
		description.addProperty("failure2NotAssessedIndiv");
		description.addProperty("failure2TotalIndiv");
		description.addProperty("otherRegisteredIndiv");
		description.addProperty("otherCuredIndiv");
		description.addProperty("otherCompletedIndiv");
		description.addProperty("otherTxSuccessIndiv");
		description.addProperty("otherDiedTBIndiv");
		description.addProperty("otherDiedNotTBIndiv");
		description.addProperty("otherFailedIndiv");
		description.addProperty("otherDefaultedIndiv");
		description.addProperty("otherNotAssessedIndiv");
		description.addProperty("otherTotalIndiv");
		description.addProperty("totalRegisteredIndiv");
		description.addProperty("totalCuredIndiv");
		description.addProperty("totalCompletedIndiv");
		description.addProperty("totalTxSuccessIndiv");
		description.addProperty("totalDiedTBIndiv");
		description.addProperty("totalDiedNotTBIndiv");
		description.addProperty("totalFailedIndiv");
		description.addProperty("totalDefaultedIndiv");
		description.addProperty("totalNotAssessedIndiv");
		description.addProperty("totalTotalIndiv");
		description.addProperty("newRegisteredStandard");
		description.addProperty("newCuredStandard");
		description.addProperty("newCompletedStandard");
		description.addProperty("newTxSuccessStandard");
		description.addProperty("newDiedTBStandard");
		description.addProperty("newDiedNotTBStandard");
		description.addProperty("newFailedStandard");
		description.addProperty("newDefaultedStandard");
		description.addProperty("newNotAssessedStandard");
		description.addProperty("newTotalStandard");
		description.addProperty("relapse1RegisteredStandard");
		description.addProperty("relapse1CuredStandard");
		description.addProperty("relapse1CompletedStandard");
		description.addProperty("relapse1TxSuccessStandard");
		description.addProperty("relapse1DiedTBStandard");
		description.addProperty("relapse1DiedNotTBStandard");
		description.addProperty("relapse1FailedStandard");
		description.addProperty("relapse1DefaultedStandard");
		description.addProperty("relapse1NotAssessedStandard");
		description.addProperty("relapse1TotalStandard");
		description.addProperty("relapse2RegisteredStandard");
		description.addProperty("relapse2CuredStandard");
		description.addProperty("relapse2CompletedStandard");
		description.addProperty("relapse2TxSuccessStandard");
		description.addProperty("relapse2DiedTBStandard");
		description.addProperty("relapse2DiedNotTBStandard");
		description.addProperty("relapse2FailedStandard");
		description.addProperty("relapse2DefaultedStandard");
		description.addProperty("relapse2NotAssessedStandard");
		description.addProperty("relapse2TotalStandard");
		description.addProperty("default1RegisteredStandard");
		description.addProperty("default1CuredStandard");
		description.addProperty("default1CompletedStandard");
		description.addProperty("default1TxSuccessStandard");
		description.addProperty("default1DiedTBStandard");
		description.addProperty("default1DiedNotTBStandard");
		description.addProperty("default1FailedStandard");
		description.addProperty("default1DefaultedStandard");
		description.addProperty("default1NotAssessedStandard");
		description.addProperty("default1TotalStandard");
		description.addProperty("default2RegisteredStandard");
		description.addProperty("default2CuredStandard");
		description.addProperty("default2CompletedStandard");
		description.addProperty("default2TxSuccessStandard");
		description.addProperty("default2DiedTBStandard");
		description.addProperty("default2DiedNotTBStandard");
		description.addProperty("default2FailedStandard");
		description.addProperty("default2DefaultedStandard");
		description.addProperty("default2NotAssessedStandard");
		description.addProperty("default2TotalStandard");
		description.addProperty("failure1RegisteredStandard");
		description.addProperty("failure1CuredStandard");
		description.addProperty("failure1CompletedStandard");
		description.addProperty("failure1TxSuccessStandard");
		description.addProperty("failure1DiedTBStandard");
		description.addProperty("failure1DiedNotTBStandard");
		description.addProperty("failure1FailedStandard");
		description.addProperty("failure1DefaultedStandard");
		description.addProperty("failure1NotAssessedStandard");
		description.addProperty("failure1TotalStandard");
		description.addProperty("failure2RegisteredStandard");
		description.addProperty("failure2CuredStandard");
		description.addProperty("failure2CompletedStandard");
		description.addProperty("failure2TxSuccessStandard");
		description.addProperty("failure2DiedTBStandard");
		description.addProperty("failure2DiedNotTBStandard");
		description.addProperty("failure2FailedStandard");
		description.addProperty("failure2DefaultedStandard");
		description.addProperty("failure2NotAssessedStandard");
		description.addProperty("failure2TotalStandard");
		description.addProperty("otherRegisteredStandard");
		description.addProperty("otherCuredStandard");
		description.addProperty("otherCompletedStandard");
		description.addProperty("otherTxSuccessStandard");
		description.addProperty("otherDiedTBStandard");
		description.addProperty("otherDiedNotTBStandard");
		description.addProperty("otherFailedStandard");
		description.addProperty("otherDefaultedStandard");
		description.addProperty("otherNotAssessedStandard");
		description.addProperty("otherTotalStandard");
		description.addProperty("totalRegisteredStandard");
		description.addProperty("totalCuredStandard");
		description.addProperty("totalCompletedStandard");
		description.addProperty("totalTxSuccessStandard");
		description.addProperty("totalDiedTBStandard");
		description.addProperty("totalDiedNotTBStandard");
		description.addProperty("totalFailedStandard");
		description.addProperty("totalDefaultedStandard");
		description.addProperty("totalNotAssessedStandard");
		description.addProperty("totalTotalStandard");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		return delegatingResourceDescription;
	}
	
	@Override
	public SimpleTB08uData newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB08uData save(SimpleTB08uData delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB08uData getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleTB08uData delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleTB08uData delegate, RequestContext context) throws ResponseException {
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
		TB08uData tb08uData = TB08uController.getTB08uPatientSet(locList, year, quarter, month);
		List<SimpleTB08uData> list = new ArrayList<>();
		list.add(new SimpleTB08uData(tb08uData));
		return new NeedsPaging<>(list, context);
	}
}
