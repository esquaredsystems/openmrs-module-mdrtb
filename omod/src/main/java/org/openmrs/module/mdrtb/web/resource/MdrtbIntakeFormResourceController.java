package org.openmrs.module.mdrtb.web.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1
		+ "/mdrtb/intake", supportedClass = SimpleTB03.class, supportedOpenmrsVersions = {"2.4.*"})
public class MdrtbIntakeFormResourceController extends DelegatingCrudResource<SimpleTB03> {

	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());

	private MdrtbService service = Context.getService(MdrtbService.class);

	@Override
	public SimpleTB03 getByUniqueId(String s) {
		//TODO: 
		return null;
	}

	@Override
	public SimpleTB03 newDelegate() {
		return new SimpleTB03();
	}

	@Override
	public SimpleTB03 save(SimpleTB03 simpleTb03) {
		//TODO:
		return null;
	}

	@Override
	public void purge(SimpleTB03 labTestType, RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("display");
		if (representation instanceof DefaultRepresentation) {
			description.addProperty("name");
			description.addProperty("description");
			return description;
		} else if (representation instanceof FullRepresentation) {
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("auditInfo");
			return description;
		} else if (representation instanceof RefRepresentation) {
			description.addProperty("name");
			return description;
		}
		return description;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		delegatingResourceDescription.addProperty("name");
		return delegatingResourceDescription;
	}

	@Override
	protected void delete(SimpleTB03 delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
