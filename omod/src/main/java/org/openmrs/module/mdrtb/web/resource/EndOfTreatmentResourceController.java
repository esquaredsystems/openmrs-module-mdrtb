package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.module.mdrtb.reporting.custom.EndOfTreatmentData;
import org.openmrs.module.mdrtb.web.controller.reporting.EndOfTreatmentController;
import org.openmrs.module.mdrtb.web.dto.SimpleEndOfTreatmentData;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;

/**
 * End of Treatment report. Same parameters as the TB03 report (year, quarter, month, month2,
 * location), plus type: "dots" for the TB03 register patients, "mdr" for the TB03u register
 * patients. Example: GET
 * /rest/v1/mdrtb/endoftreatmentreport?type=dots&year=2023&quarter=1&location=...
 */
@Resource(name = RestConstants.VERSION_1 + "/mdrtb/endoftreatmentreport", supportedClass = SimpleEndOfTreatmentData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class EndOfTreatmentResourceController extends BaseReportResource<SimpleEndOfTreatmentData> {
	
	public static final String TYPE_DOTS = "dots";
	
	public static final String TYPE_MDR = "mdr";
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("patientUuid");
		description.addProperty("identifier");
		description.addProperty("personName");
		description.addProperty("gender");
		description.addProperty("personAddress");
		description.addProperty("dateOfBirth");
		description.addProperty("registrationDate");
		description.addProperty("registrationFacility");
		description.addProperty("treatmentOutcome");
		description.addProperty("treatmentOutcomeDate");
		description.addProperty("endOfTreatmentUuid");
		description.addProperty("dispensaryGroup");
		description.addProperty("followupXrays");
		description.addProperty("followupLabTests");
		description.addProperty("transferToGroup2Date");
		description.addProperty("transferToGroup3Date");
		description.addProperty("transferToGroup3Reason");
		description.addProperty("removalFromObservationDate");
		description.addProperty("removalFromObservationReason");
		return description;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected PageableResult doSearch(RequestContext context) {
		// Checked first, so that a wrong type is never answered with an empty (or wrong) register
		String type = context.getRequest().getParameter("type");
		if (!TYPE_DOTS.equalsIgnoreCase(type) && !TYPE_MDR.equalsIgnoreCase(type)) {
			throw new IllegalRequestException("Parameter type must be '" + TYPE_DOTS + "' or '" + TYPE_MDR + "'");
		}
		final Map<String, Object> params = processParams(context);
		if (params == null) {
			return new EmptySearchResult();
		}
		List<Location> locList = (List<Location>) params.get("locations");
		Integer year = (Integer) params.get("year");
		Integer quarter = (Integer) params.get("quarter");
		Integer month = (Integer) params.get("month");
		Integer month2 = (Integer) params.get("month2");
		List<EndOfTreatmentData> patientSet;
		if (TYPE_DOTS.equalsIgnoreCase(type)) {
			patientSet = EndOfTreatmentController.getDotsEndOfTreatmentPatientSet(year, quarter, month, month2, locList);
		} else {
			patientSet = EndOfTreatmentController.getMdrEndOfTreatmentPatientSet(year, quarter, month, month2, locList);
		}
		List<SimpleEndOfTreatmentData> list = new ArrayList<>();
		for (EndOfTreatmentData data : patientSet) {
			list.add(new SimpleEndOfTreatmentData(data));
		}
		return new NeedsPaging<>(list, context);
	}
}
