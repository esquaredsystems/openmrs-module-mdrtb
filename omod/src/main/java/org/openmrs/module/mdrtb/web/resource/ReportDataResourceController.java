package org.openmrs.module.mdrtb.web.resource;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.formula.functions.T;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.ReportData;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "mdrtb/reportdata", supportedClass = ReportData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class ReportDataResourceController extends DelegatingCrudResource<ReportData> implements Searchable {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	private MdrtbService service = Context.getService(MdrtbService.class);
	
	@Override
	public ReportData newDelegate() {
		return new ReportData();
	}
	
	@Override
	public ReportData save(ReportData delegate) {
		return service.saveReportData(delegate);
	}
	
	@Override
	public ReportData getByUniqueId(String uuid) {
		return service.getReportDataByUuid(uuid);
	}
	
	@Override
	protected void delete(ReportData delegate, String reason, RequestContext context) throws ResponseException {
		service.saveReportData(delegate);
	}
	
	@Override
	public void purge(ReportData delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("display");
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addSelfLink();
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		delegatingResourceDescription.addProperty("name");
		delegatingResourceDescription.addProperty("description");
		return delegatingResourceDescription;
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		//TODO: Implement search for item
		String query = context.getParameter("q");
		List<T> results = null;
		return new NeedsPaging<T>(results, context);
	}
}
