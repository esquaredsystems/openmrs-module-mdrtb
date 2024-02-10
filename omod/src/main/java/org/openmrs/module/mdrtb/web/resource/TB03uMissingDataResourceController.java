/**
 * 
 */
package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.module.mdrtb.web.controller.reporting.MissingTb03uController;
import org.openmrs.module.mdrtb.web.dto.SimpleTB03uMissingData;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;

/**
 * 
 */
@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb03umissingreport", supportedClass = SimpleTB03uMissingData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class TB03uMissingDataResourceController extends BaseReportResource<SimpleTB03uMissingData> {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("errorCount");
		description.addProperty("errorPercentage");
		description.addProperty("totalCases");
		description.addProperty("dqItems");
		return description;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected PageableResult doSearch(RequestContext context) {
		final Map<String, Object> params = processParams(context);
		if (params == null) {
			return new EmptySearchResult();
		}
		List<Location> locList = (List<Location>) params.get("locations");
		Integer year = (Integer) params.get("year");
		Integer quarter = (Integer) params.get("quarter");
		Integer month = (Integer) params.get("month");
		Integer month2 = (Integer) params.get("month2");
		Map<String, Object> map = MissingTb03uController.getMissingTB03uPatientMap(year, quarter, month, month2, locList);
		List<SimpleTB03uMissingData> list = new ArrayList<>();
		if (map.size() > 0) {
			list.add(new SimpleTB03uMissingData(map));
		}
		return new NeedsPaging<>(list, context);
	}
}
