/**
 * 
 */
package org.openmrs.module.mdrtb.web.resource;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestSample;
import org.openmrs.module.commonlabtest.LabTestSampleStatus;
import org.openmrs.module.commonlabtest.api.CommonLabTestService;
import org.openmrs.module.mdrtb.CommonLabUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * 
 */
@Resource(name = RestConstants.VERSION_1 + "/mdrtb/lab", supportedClass = LabTest.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class LabIntegrationResource extends DataDelegatingCrudResource<LabTest> implements Searchable {
	
	private CommonLabTestService commonLabService = CommonLabUtil.getService().getCommonLabService();
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("display");
		description.addProperty("order");
		description.addProperty("labTestType");
		description.addProperty("labReferenceNumber");
		description.addProperty("labTestSamples");
		description.addProperty("attributes", Representation.FULL);
		description.addProperty("auditInfo");
		description.addSelfLink();
		return description;
	}
	
	@Override
	public LabTest newDelegate() {
		return new LabTest();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(LabTest labTest) {
		StringBuilder sb = new StringBuilder();
		sb.append(labTest.getLabTestType().getName());
		sb.append(", ");
		sb.append(labTest.getLabReferenceNumber());
		sb.append(", ");
		sb.append(labTest.getOrder().getPatient().getPersonName().getFullName());
		return sb.toString();
	}
	
	@Override
	public LabTest getByUniqueId(String uuid) {
		LabTest labTest = commonLabService.getLabTestByUuid(uuid);
		labTest.setLabTestSamples(new HashSet<>(commonLabService.getLabTestSamples(labTest, false)));
		labTest.setAttributes(new HashSet<>(commonLabService.getLabTestAttributes(labTest.getTestOrderId())));
		return labTest;
	}
	
	@Override
	public LabTest save(LabTest labTest) throws ResponseException {
		try {
			String uuid = labTest.getOrder().getUuid();
			Order existing = Context.getOrderService().getOrderByUuid(uuid);
			if (existing == null) {
				// The order must exist
				throw new ResourceDoesNotSupportOperationException("No associated Lab Order was found.");
			}
			// Fetch an accepted Sample
			LabTestSample acceptedSample = CommonLabUtil.getService().getMostRecentAcceptedSample(labTest);
			if (acceptedSample == null) {
				// An accepted sample must exist
				throw new ResourceDoesNotSupportOperationException(
				        "No specimen sample was found for this Lab Order with ACCEPTED status.");
			} else {
				acceptedSample.setStatus(LabTestSampleStatus.PROCESSED);
				acceptedSample.setProcessedDate(new Date());
			}
			// Save attributes
			labTest = commonLabService.saveLabTest(labTest, acceptedSample, labTest.getAttributes());
			return labTest;
		}
		catch (Exception e) {
			throw new ResourceDoesNotSupportOperationException("Test Order was not saved", e);
		}
	}
	
	@Override
	protected void delete(LabTest delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(LabTest delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String id = context.getRequest().getParameter("id");
		if (id == null) {
			throw new ResourceDoesNotSupportOperationException("Invaid or missing parameters!");
		}
		// First, fetch orders by Order ID (Lab reference number)
		List<LabTest> orders = commonLabService.getLabTests(id, false);
		if (!orders.isEmpty()) {
			return new NeedsPaging<>(orders, context);
		}
		// Secondly, try to fetch by Sample ID
		List<LabTestSample> samples = commonLabService.getLabTestSamples(id, null, null, false);
		if (!samples.isEmpty()) {
			for (LabTestSample labTestSample : samples) {
				orders.add(labTestSample.getLabTest());
			}
			return new NeedsPaging<>(orders, context);
		}
		// If neither worked, then fetch orders by Patient ID (any Identifier)
		List<Patient> patients = Context.getPatientService().getPatients(null, id, null, true);
		if (!patients.isEmpty()) {
			// Only the first one
			Patient patient = patients.get(0);
			orders = commonLabService.getLabTests(patient, false);
			if (!orders.isEmpty()) {
				return new NeedsPaging<>(orders, context);
			}
		}
		return new NeedsPaging<>(orders, context);
	}
}
