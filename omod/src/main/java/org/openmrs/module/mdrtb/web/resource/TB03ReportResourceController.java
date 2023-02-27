package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.api.MdrtbFormServiceImpl;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.RegimenForm;
import org.openmrs.module.mdrtb.web.controller.reporting.TB03ExportController;
import org.openmrs.module.mdrtb.web.dto.SimpleRegimenForm;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/regimen", supportedClass = SimpleRegimenForm.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class TB03ReportResourceController extends DataDelegatingCrudResource<SimpleRegimenForm> {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		return description;
	}
	
	@Override
	public SimpleRegimenForm newDelegate() {
		return new SimpleRegimenForm();
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("q");
		List<SimpleRegimenForm> simpleRegimenForms = new ArrayList<SimpleRegimenForm>();
		TB03ExportController.getPatientSet(year, quarter, month, locList);
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();
			List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
			for (Encounter encounter : encs) {
				if (encounter.getEncounterType().equals(MdrtbConstants.ET_PV_REGIMEN)) {
					RegimenForm regimenForm = new RegimenForm(encounter);
					simpleRegimenForms.add(new SimpleRegimenForm(regimenForm));
				}
			}
			return new NeedsPaging<SimpleRegimenForm>(simpleRegimenForms, context);
		}
		return null;
	}
	
	@Override
	public SimpleRegimenForm getByUniqueId(String uuid) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleRegimenForm save(SimpleRegimenForm delegate) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleRegimenForm delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleRegimenForm delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
