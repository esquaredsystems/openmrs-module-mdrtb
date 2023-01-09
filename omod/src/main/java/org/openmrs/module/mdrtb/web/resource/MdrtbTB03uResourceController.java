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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb03u", supportedClass = SimpleTB03u.class, supportedOpenmrsVersions = { "2.4.*" })
public class MdrtbTB03uResourceController extends DelegatingCrudResource<SimpleTB03u> {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	private MdrtbService service = Context.getService(MdrtbService.class);
	
	@Override
	public SimpleTB03u getByUniqueId(String s) {
		//TODO: 
		return null;
	}
	
	@Override
	public SimpleTB03u newDelegate() {
		return new SimpleTB03u();
	}
	
	@Override
	public SimpleTB03u save(SimpleTB03u simpleTB03u) {
		//TODO:
		return null;
	}
	
	@Override
	public void purge(SimpleTB03u simpleTB03u, RequestContext requestContext) throws ResponseException {
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
	protected void delete(SimpleTB03u delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
