package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.module.mdrtb.reporting.custom.TB07Table1Data;
import org.openmrs.module.mdrtb.web.controller.reporting.TB07ReportController;
import org.openmrs.module.mdrtb.web.dto.SimpleTB07Table1Data;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb07report", supportedClass = SimpleTB07Table1Data.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class TB07DataResourceController extends BaseReportResource<SimpleTB07Table1Data> {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("newMalePulmonaryBC04");
		description.addProperty("newFemalePulmonaryBC04");
		description.addProperty("newMalePulmonaryBC0514");
		description.addProperty("newFemalePulmonaryBC0514");
		description.addProperty("newMalePulmonaryBC1517");
		description.addProperty("newFemalePulmonaryBC1517");
		description.addProperty("newMalePulmonaryBC1819");
		description.addProperty("newFemalePulmonaryBC1819");
		description.addProperty("newMalePulmonaryBC2024");
		description.addProperty("newFemalePulmonaryBC2024");
		description.addProperty("newMalePulmonaryBC2534");
		description.addProperty("newFemalePulmonaryBC2534");
		description.addProperty("newMalePulmonaryBC3544");
		description.addProperty("newFemalePulmonaryBC3544");
		description.addProperty("newMalePulmonaryBC4554");
		description.addProperty("newFemalePulmonaryBC4554");
		description.addProperty("newMalePulmonaryBC5564");
		description.addProperty("newFemalePulmonaryBC5564");
		description.addProperty("newMalePulmonaryBC65");
		description.addProperty("newFemalePulmonaryBC65");
		description.addProperty("newMalePulmonaryBC");
		description.addProperty("newFemalePulmonaryBC");
		description.addProperty("newPulmonaryBC");
		description.addProperty("newMalePulmonaryBCHIV04");
		description.addProperty("newFemalePulmonaryBCHIV04");
		description.addProperty("newMalePulmonaryBCHIV0514");
		description.addProperty("newFemalePulmonaryBCHIV0514");
		description.addProperty("newMalePulmonaryBCHIV1517");
		description.addProperty("newFemalePulmonaryBCHIV1517");
		description.addProperty("newMalePulmonaryBCHIV1819");
		description.addProperty("newFemalePulmonaryBCHIV1819");
		description.addProperty("newMalePulmonaryBCHIV2024");
		description.addProperty("newFemalePulmonaryBCHIV2024");
		description.addProperty("newMalePulmonaryBCHIV2534");
		description.addProperty("newFemalePulmonaryBCHIV2534");
		description.addProperty("newMalePulmonaryBCHIV3544");
		description.addProperty("newFemalePulmonaryBCHIV3544");
		description.addProperty("newMalePulmonaryBCHIV4554");
		description.addProperty("newFemalePulmonaryBCHIV4554");
		description.addProperty("newMalePulmonaryBCHIV5564");
		description.addProperty("newFemalePulmonaryBCHIV5564");
		description.addProperty("newMalePulmonaryBCHIV65");
		description.addProperty("newFemalePulmonaryBCHIV65");
		description.addProperty("newMalePulmonaryBCHIV");
		description.addProperty("newFemalePulmonaryBCHIV");
		description.addProperty("newPulmonaryBCHIV");
		description.addProperty("newMalePulmonaryCD04");
		description.addProperty("newFemalePulmonaryCD04");
		description.addProperty("newMalePulmonaryCD0514");
		description.addProperty("newFemalePulmonaryCD0514");
		description.addProperty("newMalePulmonaryCD1517");
		description.addProperty("newFemalePulmonaryCD1517");
		description.addProperty("newMalePulmonaryCD1819");
		description.addProperty("newFemalePulmonaryCD1819");
		description.addProperty("newMalePulmonaryCD2024");
		description.addProperty("newFemalePulmonaryCD2024");
		description.addProperty("newMalePulmonaryCD2534");
		description.addProperty("newFemalePulmonaryCD2534");
		description.addProperty("newMalePulmonaryCD3544");
		description.addProperty("newFemalePulmonaryCD3544");
		description.addProperty("newMalePulmonaryCD4554");
		description.addProperty("newFemalePulmonaryCD4554");
		description.addProperty("newMalePulmonaryCD5564");
		description.addProperty("newFemalePulmonaryCD5564");
		description.addProperty("newMalePulmonaryCD65");
		description.addProperty("newFemalePulmonaryCD65");
		description.addProperty("newMalePulmonaryCD");
		description.addProperty("newFemalePulmonaryCD");
		description.addProperty("newPulmonaryCD");
		description.addProperty("newMalePulmonaryCDHIV04");
		description.addProperty("newFemalePulmonaryCDHIV04");
		description.addProperty("newMalePulmonaryCDHIV0514");
		description.addProperty("newFemalePulmonaryCDHIV0514");
		description.addProperty("newMalePulmonaryCDHIV1517");
		description.addProperty("newFemalePulmonaryCDHIV1517");
		description.addProperty("newMalePulmonaryCDHIV1819");
		description.addProperty("newFemalePulmonaryCDHIV1819");
		description.addProperty("newMalePulmonaryCDHIV2024");
		description.addProperty("newFemalePulmonaryCDHIV2024");
		description.addProperty("newMalePulmonaryCDHIV2534");
		description.addProperty("newFemalePulmonaryCDHIV2534");
		description.addProperty("newMalePulmonaryCDHIV3544");
		description.addProperty("newFemalePulmonaryCDHIV3544");
		description.addProperty("newMalePulmonaryCDHIV4554");
		description.addProperty("newFemalePulmonaryCDHIV4554");
		description.addProperty("newMalePulmonaryCDHIV5564");
		description.addProperty("newFemalePulmonaryCDHIV5564");
		description.addProperty("newMalePulmonaryCDHIV65");
		description.addProperty("newFemalePulmonaryCDHIV65");
		description.addProperty("newMalePulmonaryCDHIV");
		description.addProperty("newFemalePulmonaryCDHIV");
		description.addProperty("newPulmonaryCDHIV");
		description.addProperty("newMaleExtrapulmonary04");
		description.addProperty("newFemaleExtrapulmonary04");
		description.addProperty("newMaleExtrapulmonary0514");
		description.addProperty("newFemaleExtrapulmonary0514");
		description.addProperty("newMaleExtrapulmonary1517");
		description.addProperty("newFemaleExtrapulmonary1517");
		description.addProperty("newMaleExtrapulmonary1819");
		description.addProperty("newFemaleExtrapulmonary1819");
		description.addProperty("newMaleExtrapulmonary2024");
		description.addProperty("newFemaleExtrapulmonary2024");
		description.addProperty("newMaleExtrapulmonary2534");
		description.addProperty("newFemaleExtrapulmonary2534");
		description.addProperty("newMaleExtrapulmonary3544");
		description.addProperty("newFemaleExtrapulmonary3544");
		description.addProperty("newMaleExtrapulmonary4554");
		description.addProperty("newFemaleExtrapulmonary4554");
		description.addProperty("newMaleExtrapulmonary5564");
		description.addProperty("newFemaleExtrapulmonary5564");
		description.addProperty("newMaleExtrapulmonary65");
		description.addProperty("newFemaleExtrapulmonary65");
		description.addProperty("newMaleExtrapulmonary");
		description.addProperty("newFemaleExtrapulmonary");
		description.addProperty("newExtrapulmonary");
		description.addProperty("newMaleExtrapulmonaryHIV04");
		description.addProperty("newFemaleExtrapulmonaryHIV04");
		description.addProperty("newMaleExtrapulmonaryHIV0514");
		description.addProperty("newFemaleExtrapulmonaryHIV0514");
		description.addProperty("newMaleExtrapulmonaryHIV1517");
		description.addProperty("newFemaleExtrapulmonaryHIV1517");
		description.addProperty("newMaleExtrapulmonaryHIV1819");
		description.addProperty("newFemaleExtrapulmonaryHIV1819");
		description.addProperty("newMaleExtrapulmonaryHIV2024");
		description.addProperty("newFemaleExtrapulmonaryHIV2024");
		description.addProperty("newMaleExtrapulmonaryHIV2534");
		description.addProperty("newFemaleExtrapulmonaryHIV2534");
		description.addProperty("newMaleExtrapulmonaryHIV3544");
		description.addProperty("newFemaleExtrapulmonaryHIV3544");
		description.addProperty("newMaleExtrapulmonaryHIV4554");
		description.addProperty("newFemaleExtrapulmonaryHIV4554");
		description.addProperty("newMaleExtrapulmonaryHIV5564");
		description.addProperty("newFemaleExtrapulmonaryHIV5564");
		description.addProperty("newMaleExtrapulmonaryHIV65");
		description.addProperty("newFemaleExtrapulmonaryHIV65");
		description.addProperty("newMaleExtrapulmonaryHIV");
		description.addProperty("newFemaleExtrapulmonaryHIV");
		description.addProperty("newExtrapulmonaryHIV");
		description.addProperty("newMale04");
		description.addProperty("newFemale04");
		description.addProperty("newMale0514");
		description.addProperty("newFemale0514");
		description.addProperty("newMale1517");
		description.addProperty("newFemale1517");
		description.addProperty("newMale1819");
		description.addProperty("newFemale1819");
		description.addProperty("newMale2024");
		description.addProperty("newFemale2024");
		description.addProperty("newMale2534");
		description.addProperty("newFemale2534");
		description.addProperty("newMale3544");
		description.addProperty("newFemale3544");
		description.addProperty("newMale4554");
		description.addProperty("newFemale4554");
		description.addProperty("newMale5564");
		description.addProperty("newFemale5564");
		description.addProperty("newMale65");
		description.addProperty("newFemale65");
		description.addProperty("newMale");
		description.addProperty("newFemale");
		description.addProperty("newAll");
		description.addProperty("newMaleHIV04");
		description.addProperty("newFemaleHIV04");
		description.addProperty("newMaleHIV0514");
		description.addProperty("newFemaleHIV0514");
		description.addProperty("newMaleHIV1517");
		description.addProperty("newFemaleHIV1517");
		description.addProperty("newMaleHIV1819");
		description.addProperty("newFemaleHIV1819");
		description.addProperty("newMaleHIV2024");
		description.addProperty("newFemaleHIV2024");
		description.addProperty("newMaleHIV2534");
		description.addProperty("newFemaleHIV2534");
		description.addProperty("newMaleHIV3544");
		description.addProperty("newFemaleHIV3544");
		description.addProperty("newMaleHIV4554");
		description.addProperty("newFemaleHIV4554");
		description.addProperty("newMaleHIV5564");
		description.addProperty("newFemaleHIV5564");
		description.addProperty("newMaleHIV65");
		description.addProperty("newFemaleHIV65");
		description.addProperty("newMaleHIV");
		description.addProperty("newFemaleHIV");
		description.addProperty("newAllHIV");
		description.addProperty("relapseMalePulmonaryBC04");
		description.addProperty("relapseFemalePulmonaryBC04");
		description.addProperty("relapseMalePulmonaryBC0514");
		description.addProperty("relapseFemalePulmonaryBC0514");
		description.addProperty("relapseMalePulmonaryBC1517");
		description.addProperty("relapseFemalePulmonaryBC1517");
		description.addProperty("relapseMalePulmonaryBC1819");
		description.addProperty("relapseFemalePulmonaryBC1819");
		description.addProperty("relapseMalePulmonaryBC2024");
		description.addProperty("relapseFemalePulmonaryBC2024");
		description.addProperty("relapseMalePulmonaryBC2534");
		description.addProperty("relapseFemalePulmonaryBC2534");
		description.addProperty("relapseMalePulmonaryBC3544");
		description.addProperty("relapseFemalePulmonaryBC3544");
		description.addProperty("relapseMalePulmonaryBC4554");
		description.addProperty("relapseFemalePulmonaryBC4554");
		description.addProperty("relapseMalePulmonaryBC5564");
		description.addProperty("relapseFemalePulmonaryBC5564");
		description.addProperty("relapseMalePulmonaryBC65");
		description.addProperty("relapseFemalePulmonaryBC65");
		description.addProperty("relapseMalePulmonaryBC");
		description.addProperty("relapseFemalePulmonaryBC");
		description.addProperty("relapsePulmonaryBC");
		description.addProperty("relapseMalePulmonaryBCHIV04");
		description.addProperty("relapseFemalePulmonaryBCHIV04");
		description.addProperty("relapseMalePulmonaryBCHIV0514");
		description.addProperty("relapseFemalePulmonaryBCHIV0514");
		description.addProperty("relapseMalePulmonaryBCHIV1517");
		description.addProperty("relapseFemalePulmonaryBCHIV1517");
		description.addProperty("relapseMalePulmonaryBCHIV1819");
		description.addProperty("relapseFemalePulmonaryBCHIV1819");
		description.addProperty("relapseMalePulmonaryBCHIV2024");
		description.addProperty("relapseFemalePulmonaryBCHIV2024");
		description.addProperty("relapseMalePulmonaryBCHIV2534");
		description.addProperty("relapseFemalePulmonaryBCHIV2534");
		description.addProperty("relapseMalePulmonaryBCHIV3544");
		description.addProperty("relapseFemalePulmonaryBCHIV3544");
		description.addProperty("relapseMalePulmonaryBCHIV4554");
		description.addProperty("relapseFemalePulmonaryBCHIV4554");
		description.addProperty("relapseMalePulmonaryBCHIV5564");
		description.addProperty("relapseFemalePulmonaryBCHIV5564");
		description.addProperty("relapseMalePulmonaryBCHIV65");
		description.addProperty("relapseFemalePulmonaryBCHIV65");
		description.addProperty("relapseMalePulmonaryBCHIV");
		description.addProperty("relapseFemalePulmonaryBCHIV");
		description.addProperty("relapsePulmonaryBCHIV");
		description.addProperty("relapseMalePulmonaryCD04");
		description.addProperty("relapseFemalePulmonaryCD04");
		description.addProperty("relapseMalePulmonaryCD0514");
		description.addProperty("relapseFemalePulmonaryCD0514");
		description.addProperty("relapseMalePulmonaryCD1517");
		description.addProperty("relapseFemalePulmonaryCD1517");
		description.addProperty("relapseMalePulmonaryCD1819");
		description.addProperty("relapseFemalePulmonaryCD1819");
		description.addProperty("relapseMalePulmonaryCD2024");
		description.addProperty("relapseFemalePulmonaryCD2024");
		description.addProperty("relapseMalePulmonaryCD2534");
		description.addProperty("relapseFemalePulmonaryCD2534");
		description.addProperty("relapseMalePulmonaryCD3544");
		description.addProperty("relapseFemalePulmonaryCD3544");
		description.addProperty("relapseMalePulmonaryCD4554");
		description.addProperty("relapseFemalePulmonaryCD4554");
		description.addProperty("relapseMalePulmonaryCD5564");
		description.addProperty("relapseFemalePulmonaryCD5564");
		description.addProperty("relapseMalePulmonaryCD65");
		description.addProperty("relapseFemalePulmonaryCD65");
		description.addProperty("relapseMalePulmonaryCD");
		description.addProperty("relapseFemalePulmonaryCD");
		description.addProperty("relapsePulmonaryCD");
		description.addProperty("relapseMalePulmonaryCDHIV04");
		description.addProperty("relapseFemalePulmonaryCDHIV04");
		description.addProperty("relapseMalePulmonaryCDHIV0514");
		description.addProperty("relapseFemalePulmonaryCDHIV0514");
		description.addProperty("relapseMalePulmonaryCDHIV1517");
		description.addProperty("relapseFemalePulmonaryCDHIV1517");
		description.addProperty("relapseMalePulmonaryCDHIV1819");
		description.addProperty("relapseFemalePulmonaryCDHIV1819");
		description.addProperty("relapseMalePulmonaryCDHIV2024");
		description.addProperty("relapseFemalePulmonaryCDHIV2024");
		description.addProperty("relapseMalePulmonaryCDHIV2534");
		description.addProperty("relapseFemalePulmonaryCDHIV2534");
		description.addProperty("relapseMalePulmonaryCDHIV3544");
		description.addProperty("relapseFemalePulmonaryCDHIV3544");
		description.addProperty("relapseMalePulmonaryCDHIV4554");
		description.addProperty("relapseFemalePulmonaryCDHIV4554");
		description.addProperty("relapseMalePulmonaryCDHIV5564");
		description.addProperty("relapseFemalePulmonaryCDHIV5564");
		description.addProperty("relapseMalePulmonaryCDHIV65");
		description.addProperty("relapseFemalePulmonaryCDHIV65");
		description.addProperty("relapseMalePulmonaryCDHIV");
		description.addProperty("relapseFemalePulmonaryCDHIV");
		description.addProperty("relapsePulmonaryCDHIV");
		description.addProperty("relapseMaleExtrapulmonary04");
		description.addProperty("relapseFemaleExtrapulmonary04");
		description.addProperty("relapseMaleExtrapulmonary0514");
		description.addProperty("relapseFemaleExtrapulmonary0514");
		description.addProperty("relapseMaleExtrapulmonary1517");
		description.addProperty("relapseFemaleExtrapulmonary1517");
		description.addProperty("relapseMaleExtrapulmonary1819");
		description.addProperty("relapseFemaleExtrapulmonary1819");
		description.addProperty("relapseMaleExtrapulmonary2024");
		description.addProperty("relapseFemaleExtrapulmonary2024");
		description.addProperty("relapseMaleExtrapulmonary2534");
		description.addProperty("relapseFemaleExtrapulmonary2534");
		description.addProperty("relapseMaleExtrapulmonary3544");
		description.addProperty("relapseFemaleExtrapulmonary3544");
		description.addProperty("relapseMaleExtrapulmonary4554");
		description.addProperty("relapseFemaleExtrapulmonary4554");
		description.addProperty("relapseMaleExtrapulmonary5564");
		description.addProperty("relapseFemaleExtrapulmonary5564");
		description.addProperty("relapseMaleExtrapulmonary65");
		description.addProperty("relapseFemaleExtrapulmonary65");
		description.addProperty("relapseMaleExtrapulmonary");
		description.addProperty("relapseFemaleExtrapulmonary");
		description.addProperty("relapseExtrapulmonary");
		description.addProperty("relapseMaleExtrapulmonaryHIV04");
		description.addProperty("relapseFemaleExtrapulmonaryHIV04");
		description.addProperty("relapseMaleExtrapulmonaryHIV0514");
		description.addProperty("relapseFemaleExtrapulmonaryHIV0514");
		description.addProperty("relapseMaleExtrapulmonaryHIV1517");
		description.addProperty("relapseFemaleExtrapulmonaryHIV1517");
		description.addProperty("relapseMaleExtrapulmonaryHIV1819");
		description.addProperty("relapseFemaleExtrapulmonaryHIV1819");
		description.addProperty("relapseMaleExtrapulmonaryHIV2024");
		description.addProperty("relapseFemaleExtrapulmonaryHIV2024");
		description.addProperty("relapseMaleExtrapulmonaryHIV2534");
		description.addProperty("relapseFemaleExtrapulmonaryHIV2534");
		description.addProperty("relapseMaleExtrapulmonaryHIV3544");
		description.addProperty("relapseFemaleExtrapulmonaryHIV3544");
		description.addProperty("relapseMaleExtrapulmonaryHIV4554");
		description.addProperty("relapseFemaleExtrapulmonaryHIV4554");
		description.addProperty("relapseMaleExtrapulmonaryHIV5564");
		description.addProperty("relapseFemaleExtrapulmonaryHIV5564");
		description.addProperty("relapseMaleExtrapulmonaryHIV65");
		description.addProperty("relapseFemaleExtrapulmonaryHIV65");
		description.addProperty("relapseMaleExtrapulmonaryHIV");
		description.addProperty("relapseFemaleExtrapulmonaryHIV");
		description.addProperty("relapseExtrapulmonaryHIV");
		description.addProperty("relapseMale04");
		description.addProperty("relapseFemale04");
		description.addProperty("relapseMale0514");
		description.addProperty("relapseFemale0514");
		description.addProperty("relapseMale1517");
		description.addProperty("relapseFemale1517");
		description.addProperty("relapseMale1819");
		description.addProperty("relapseFemale1819");
		description.addProperty("relapseMale2024");
		description.addProperty("relapseFemale2024");
		description.addProperty("relapseMale2534");
		description.addProperty("relapseFemale2534");
		description.addProperty("relapseMale3544");
		description.addProperty("relapseFemale3544");
		description.addProperty("relapseMale4554");
		description.addProperty("relapseFemale4554");
		description.addProperty("relapseMale5564");
		description.addProperty("relapseFemale5564");
		description.addProperty("relapseMale65");
		description.addProperty("relapseFemale65");
		description.addProperty("relapseMale");
		description.addProperty("relapseFemale");
		description.addProperty("relapseAll");
		description.addProperty("relapseMaleHIV04");
		description.addProperty("relapseFemaleHIV04");
		description.addProperty("relapseMaleHIV0514");
		description.addProperty("relapseFemaleHIV0514");
		description.addProperty("relapseMaleHIV1517");
		description.addProperty("relapseFemaleHIV1517");
		description.addProperty("relapseMaleHIV1819");
		description.addProperty("relapseFemaleHIV1819");
		description.addProperty("relapseMaleHIV2024");
		description.addProperty("relapseFemaleHIV2024");
		description.addProperty("relapseMaleHIV2534");
		description.addProperty("relapseFemaleHIV2534");
		description.addProperty("relapseMaleHIV3544");
		description.addProperty("relapseFemaleHIV3544");
		description.addProperty("relapseMaleHIV4554");
		description.addProperty("relapseFemaleHIV4554");
		description.addProperty("relapseMaleHIV5564");
		description.addProperty("relapseFemaleHIV5564");
		description.addProperty("relapseMaleHIV65");
		description.addProperty("relapseFemaleHIV65");
		description.addProperty("relapseMaleHIV");
		description.addProperty("relapseFemaleHIV");
		description.addProperty("relapseAllHIV");
		description.addProperty("hivPositive");
		description.addProperty("hivTested");
		description.addProperty("artStarted");
		description.addProperty("pctStarted");
		description.addProperty("failureMalePulmonaryBC");
		description.addProperty("failureFemalePulmonaryBC");
		description.addProperty("failurePulmonaryBC");
		description.addProperty("failureMalePulmonaryBCHIV");
		description.addProperty("failureFemalePulmonaryBCHIV");
		description.addProperty("failurePulmonaryBCHIV");
		description.addProperty("failureMalePulmonaryCD");
		description.addProperty("failureFemalePulmonaryCD");
		description.addProperty("failurePulmonaryCD");
		description.addProperty("failureMalePulmonaryCDHIV");
		description.addProperty("failureFemalePulmonaryCDHIV");
		description.addProperty("failurePulmonaryCDHIV");
		description.addProperty("failureMaleExtrapulmonary");
		description.addProperty("failureFemaleExtrapulmonary");
		description.addProperty("failureExtrapulmonary");
		description.addProperty("failureMaleExtrapulmonaryHIV");
		description.addProperty("failureFemaleExtrapulmonaryHIV");
		description.addProperty("failureExtrapulmonaryHIV");
		description.addProperty("failureMale");
		description.addProperty("failureFemale");
		description.addProperty("failureAll");
		description.addProperty("failureMaleHIV");
		description.addProperty("failureFemaleHIV");
		description.addProperty("failureAllHIV");
		description.addProperty("defaultMalePulmonaryBC");
		description.addProperty("defaultFemalePulmonaryBC");
		description.addProperty("defaultPulmonaryBC");
		description.addProperty("defaultMalePulmonaryBCHIV");
		description.addProperty("defaultFemalePulmonaryBCHIV");
		description.addProperty("defaultPulmonaryBCHIV");
		description.addProperty("defaultMalePulmonaryCD");
		description.addProperty("defaultFemalePulmonaryCD");
		description.addProperty("defaultPulmonaryCD");
		description.addProperty("defaultMalePulmonaryCDHIV");
		description.addProperty("defaultFemalePulmonaryCDHIV");
		description.addProperty("defaultPulmonaryCDHIV");
		description.addProperty("defaultMaleExtrapulmonary");
		description.addProperty("defaultFemaleExtrapulmonary");
		description.addProperty("defaultExtrapulmonary");
		description.addProperty("defaultMaleExtrapulmonaryHIV");
		description.addProperty("defaultFemaleExtrapulmonaryHIV");
		description.addProperty("defaultExtrapulmonaryHIV");
		description.addProperty("defaultMale");
		description.addProperty("defaultFemale");
		description.addProperty("defaultAll");
		description.addProperty("defaultMaleHIV");
		description.addProperty("defaultFemaleHIV");
		description.addProperty("defaultAllHIV");
		description.addProperty("otherMalePulmonaryBC");
		description.addProperty("otherFemalePulmonaryBC");
		description.addProperty("otherPulmonaryBC");
		description.addProperty("otherMalePulmonaryBCHIV");
		description.addProperty("otherFemalePulmonaryBCHIV");
		description.addProperty("otherPulmonaryBCHIV");
		description.addProperty("otherMalePulmonaryCD");
		description.addProperty("otherFemalePulmonaryCD");
		description.addProperty("otherPulmonaryCD");
		description.addProperty("otherMalePulmonaryCDHIV");
		description.addProperty("otherFemalePulmonaryCDHIV");
		description.addProperty("otherPulmonaryCDHIV");
		description.addProperty("otherMaleExtrapulmonary");
		description.addProperty("otherFemaleExtrapulmonary");
		description.addProperty("otherExtrapulmonary");
		description.addProperty("otherMaleExtrapulmonaryHIV");
		description.addProperty("otherFemaleExtrapulmonaryHIV");
		description.addProperty("otherExtrapulmonaryHIV");
		description.addProperty("otherMale");
		description.addProperty("otherFemale");
		description.addProperty("otherAll");
		description.addProperty("otherMaleHIV");
		description.addProperty("otherFemaleHIV");
		description.addProperty("otherAllHIV");
		description.addProperty("retreatmentMalePulmonaryBC");
		description.addProperty("retreatmentFemalePulmonaryBC");
		description.addProperty("retreatmentPulmonaryBC");
		description.addProperty("retreatmentMalePulmonaryBCHIV");
		description.addProperty("retreatmentFemalePulmonaryBCHIV");
		description.addProperty("retreatmentPulmonaryBCHIV");
		description.addProperty("retreatmentMalePulmonaryCD");
		description.addProperty("retreatmentFemalePulmonaryCD");
		description.addProperty("retreatmentPulmonaryCD");
		description.addProperty("retreatmentMalePulmonaryCDHIV");
		description.addProperty("retreatmentFemalePulmonaryCDHIV");
		description.addProperty("retreatmentPulmonaryCDHIV");
		description.addProperty("retreatmentMaleExtrapulmonary");
		description.addProperty("retreatmentFemaleExtrapulmonary");
		description.addProperty("retreatmentExtrapulmonary");
		description.addProperty("retreatmentMaleExtrapulmonaryHIV");
		description.addProperty("retreatmentFemaleExtrapulmonaryHIV");
		description.addProperty("retreatmentExtrapulmonaryHIV");
		description.addProperty("retreatmentMale");
		description.addProperty("retreatmentFemale");
		description.addProperty("retreatmentAll");
		description.addProperty("retreatmentMaleHIV");
		description.addProperty("retreatmentFemaleHIV");
		description.addProperty("retreatmentAllHIV");
		description.addProperty("totalMalePulmonaryBC");
		description.addProperty("totalFemalePulmonaryBC");
		description.addProperty("totalPulmonaryBC");
		description.addProperty("totalMalePulmonaryBCHIV");
		description.addProperty("totalFemalePulmonaryBCHIV");
		description.addProperty("totalPulmonaryBCHIV");
		description.addProperty("totalMalePulmonaryCD");
		description.addProperty("totalFemalePulmonaryCD");
		description.addProperty("totalPulmonaryCD");
		description.addProperty("totalMalePulmonaryCDHIV");
		description.addProperty("totalFemalePulmonaryCDHIV");
		description.addProperty("totalPulmonaryCDHIV");
		description.addProperty("totalMaleExtrapulmonary");
		description.addProperty("totalFemaleExtrapulmonary");
		description.addProperty("totalExtrapulmonary");
		description.addProperty("totalMaleExtrapulmonaryHIV");
		description.addProperty("totalFemaleExtrapulmonaryHIV");
		description.addProperty("totalExtrapulmonaryHIV");
		description.addProperty("totalMale");
		description.addProperty("totalFemale");
		description.addProperty("totalAll");
		description.addProperty("totalMaleHIV");
		description.addProperty("totalFemaleHIV");
		description.addProperty("totalAllHIV");
		description.addProperty("womenOfChildBearingAge");
		description.addProperty("pregnant");
		description.addProperty("contacts");
		description.addProperty("migrants");
		description.addProperty("phcWorkers");
		description.addProperty("tbServicesWorkers");
		description.addProperty("died");
		description.addProperty("diedChildren");
		description.addProperty("diedWomenOfChildBearingAge");
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
		TB07Table1Data TB07DataTable1 = TB07ReportController.getTB07PatientSet(year, quarter, month, month2, locList);
		List<SimpleTB07Table1Data> list = new ArrayList<>();
		list.add(new SimpleTB07Table1Data(TB07DataTable1));
		return new NeedsPaging<>(list, context);
	}
}
