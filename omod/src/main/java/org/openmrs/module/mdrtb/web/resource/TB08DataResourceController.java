package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.reporting.custom.TB08Data;
import org.openmrs.module.mdrtb.web.controller.reporting.TB08ReportController;
import org.openmrs.module.mdrtb.web.dto.SimpleTB08Data;
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

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/tb08report", supportedClass = SimpleTB08Data.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*" })
public class TB08DataResourceController extends DelegatingCrudResource<SimpleTB08Data> implements Searchable {
	
	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("newPulmonaryBCDetected");
		description.addProperty("newPulmonaryBCDetected04");
		description.addProperty("newPulmonaryBCDetected0514");
		description.addProperty("newPulmonaryBCDetected1517");
		description.addProperty("newPulmonaryCDDetected");
		description.addProperty("newPulmonaryCDDetected04");
		description.addProperty("newPulmonaryCDDetected0514");
		description.addProperty("newPulmonaryCDDetected1517");
		description.addProperty("newExtrapulmonaryDetected");
		description.addProperty("newExtrapulmonaryDetected04");
		description.addProperty("newExtrapulmonaryDetected0514");
		description.addProperty("newExtrapulmonaryDetected1517");
		description.addProperty("newAllDetected");
		description.addProperty("newAllDetected04");
		description.addProperty("newAllDetected0514");
		description.addProperty("newAllDetected1517");
		description.addProperty("newPulmonaryBCEligible");
		description.addProperty("newPulmonaryBCEligible04");
		description.addProperty("newPulmonaryBCEligible0514");
		description.addProperty("newPulmonaryBCEligible1517");
		description.addProperty("newPulmonaryCDEligible");
		description.addProperty("newPulmonaryCDEligible04");
		description.addProperty("newPulmonaryCDEligible0514");
		description.addProperty("newPulmonaryCDEligible1517");
		description.addProperty("newExtrapulmonaryEligible");
		description.addProperty("newExtrapulmonaryEligible04");
		description.addProperty("newExtrapulmonaryEligible0514");
		description.addProperty("newExtrapulmonaryEligible1517");
		description.addProperty("newAllEligible");
		description.addProperty("newAllEligible04");
		description.addProperty("newAllEligible0514");
		description.addProperty("newAllEligible1517");
		description.addProperty("newPulmonaryBCCured");
		description.addProperty("newPulmonaryBCCured04");
		description.addProperty("newPulmonaryBCCured0514");
		description.addProperty("newPulmonaryBCCured1517");
		description.addProperty("newPulmonaryCDCured");
		description.addProperty("newPulmonaryCDCured04");
		description.addProperty("newPulmonaryCDCured0514");
		description.addProperty("newPulmonaryCDCured1517");
		description.addProperty("newExtrapulmonaryCured");
		description.addProperty("newExtrapulmonaryCured04");
		description.addProperty("newExtrapulmonaryCured0514");
		description.addProperty("newExtrapulmonaryCured1517");
		description.addProperty("newAllCured");
		description.addProperty("newAllCured04");
		description.addProperty("newAllCured0514");
		description.addProperty("newAllCured1517");
		description.addProperty("newPulmonaryBCCompleted");
		description.addProperty("newPulmonaryBCCompleted04");
		description.addProperty("newPulmonaryBCCompleted0514");
		description.addProperty("newPulmonaryBCCompleted1517");
		description.addProperty("newPulmonaryCDCompleted");
		description.addProperty("newPulmonaryCDCompleted04");
		description.addProperty("newPulmonaryCDCompleted0514");
		description.addProperty("newPulmonaryCDCompleted1517");
		description.addProperty("newExtrapulmonaryCompleted");
		description.addProperty("newExtrapulmonaryCompleted04");
		description.addProperty("newExtrapulmonaryCompleted0514");
		description.addProperty("newExtrapulmonaryCompleted1517");
		description.addProperty("newAllCompleted");
		description.addProperty("newAllCompleted04");
		description.addProperty("newAllCompleted0514");
		description.addProperty("newAllCompleted1517");
		description.addProperty("newPulmonaryBCDiedTB");
		description.addProperty("newPulmonaryBCDiedTB04");
		description.addProperty("newPulmonaryBCDiedTB0514");
		description.addProperty("newPulmonaryBCDiedTB1517");
		description.addProperty("newPulmonaryCDDiedTB");
		description.addProperty("newPulmonaryCDDiedTB04");
		description.addProperty("newPulmonaryCDDiedTB0514");
		description.addProperty("newPulmonaryCDDiedTB1517");
		description.addProperty("newExtrapulmonaryDiedTB");
		description.addProperty("newExtrapulmonaryDiedTB04");
		description.addProperty("newExtrapulmonaryDiedTB0514");
		description.addProperty("newExtrapulmonaryDiedTB1517");
		description.addProperty("newAllDiedTB");
		description.addProperty("newAllDiedTB04");
		description.addProperty("newAllDiedTB0514");
		description.addProperty("newAllDiedTB1517");
		description.addProperty("newPulmonaryBCDiedNotTB");
		description.addProperty("newPulmonaryBCDiedNotTB04");
		description.addProperty("newPulmonaryBCDiedNotTB0514");
		description.addProperty("newPulmonaryBCDiedNotTB1517");
		description.addProperty("newPulmonaryCDDiedNotTB");
		description.addProperty("newPulmonaryCDDiedNotTB04");
		description.addProperty("newPulmonaryCDDiedNotTB0514");
		description.addProperty("newPulmonaryCDDiedNotTB1517");
		description.addProperty("newExtrapulmonaryDiedNotTB");
		description.addProperty("newExtrapulmonaryDiedNotTB04");
		description.addProperty("newExtrapulmonaryDiedNotTB0514");
		description.addProperty("newExtrapulmonaryDiedNotTB1517");
		description.addProperty("newAllDiedNotTB");
		description.addProperty("newAllDiedNotTB04");
		description.addProperty("newAllDiedNotTB0514");
		description.addProperty("newAllDiedNotTB1517");
		description.addProperty("newPulmonaryBCFailed");
		description.addProperty("newPulmonaryBCFailed04");
		description.addProperty("newPulmonaryBCFailed0514");
		description.addProperty("newPulmonaryBCFailed1517");
		description.addProperty("newPulmonaryCDFailed");
		description.addProperty("newPulmonaryCDFailed04");
		description.addProperty("newPulmonaryCDFailed0514");
		description.addProperty("newPulmonaryCDFailed1517");
		description.addProperty("newExtrapulmonaryFailed");
		description.addProperty("newExtrapulmonaryFailed04");
		description.addProperty("newExtrapulmonaryFailed0514");
		description.addProperty("newExtrapulmonaryFailed1517");
		description.addProperty("newAllFailed");
		description.addProperty("newAllFailed04");
		description.addProperty("newAllFailed0514");
		description.addProperty("newAllFailed1517");
		description.addProperty("newPulmonaryBCDefaulted");
		description.addProperty("newPulmonaryBCDefaulted04");
		description.addProperty("newPulmonaryBCDefaulted0514");
		description.addProperty("newPulmonaryBCDefaulted1517");
		description.addProperty("newPulmonaryCDDefaulted");
		description.addProperty("newPulmonaryCDDefaulted04");
		description.addProperty("newPulmonaryCDDefaulted0514");
		description.addProperty("newPulmonaryCDDefaulted1517");
		description.addProperty("newExtrapulmonaryDefaulted");
		description.addProperty("newExtrapulmonaryDefaulted04");
		description.addProperty("newExtrapulmonaryDefaulted0514");
		description.addProperty("newExtrapulmonaryDefaulted1517");
		description.addProperty("newAllDefaulted");
		description.addProperty("newAllDefaulted04");
		description.addProperty("newAllDefaulted0514");
		description.addProperty("newAllDefaulted1517");
		description.addProperty("newPulmonaryBCTransferOut");
		description.addProperty("newPulmonaryBCTransferOut04");
		description.addProperty("newPulmonaryBCTransferOut0514");
		description.addProperty("newPulmonaryBCTransferOut1517");
		description.addProperty("newPulmonaryCDTransferOut");
		description.addProperty("newPulmonaryCDTransferOut04");
		description.addProperty("newPulmonaryCDTransferOut0514");
		description.addProperty("newPulmonaryCDTransferOut1517");
		description.addProperty("newExtrapulmonaryTransferOut");
		description.addProperty("newExtrapulmonaryTransferOut04");
		description.addProperty("newExtrapulmonaryTransferOut0514");
		description.addProperty("newExtrapulmonaryTransferOut1517");
		description.addProperty("newAllTransferOut");
		description.addProperty("newAllTransferOut04");
		description.addProperty("newAllTransferOut0514");
		description.addProperty("newAllTransferOut1517");
		description.addProperty("newPulmonaryBCCanceled");
		description.addProperty("newPulmonaryBCCanceled04");
		description.addProperty("newPulmonaryBCCanceled0514");
		description.addProperty("newPulmonaryBCCanceled1517");
		description.addProperty("newPulmonaryCDCanceled");
		description.addProperty("newPulmonaryCDCanceled04");
		description.addProperty("newPulmonaryCDCanceled0514");
		description.addProperty("newPulmonaryCDCanceled1517");
		description.addProperty("newExtrapulmonaryCanceled");
		description.addProperty("newExtrapulmonaryCanceled04");
		description.addProperty("newExtrapulmonaryCanceled0514");
		description.addProperty("newExtrapulmonaryCanceled1517");
		description.addProperty("newAllCanceled");
		description.addProperty("newAllCanceled04");
		description.addProperty("newAllCanceled0514");
		description.addProperty("newAllCanceled1517");
		description.addProperty("newPulmonaryBCSLD");
		description.addProperty("newPulmonaryBCSLD04");
		description.addProperty("newPulmonaryBCSLD0514");
		description.addProperty("newPulmonaryBCSLD1517");
		description.addProperty("newPulmonaryCDSLD");
		description.addProperty("newPulmonaryCDSLD04");
		description.addProperty("newPulmonaryCDSLD0514");
		description.addProperty("newPulmonaryCDSLD1517");
		description.addProperty("newExtrapulmonarySLD");
		description.addProperty("newExtrapulmonarySLD04");
		description.addProperty("newExtrapulmonarySLD0514");
		description.addProperty("newExtrapulmonarySLD1517");
		description.addProperty("newAllSLD");
		description.addProperty("newAllSLD04");
		description.addProperty("newAllSLD0514");
		description.addProperty("newAllSLD1517");
		description.addProperty("relapsePulmonaryBCDetected");
		description.addProperty("relapsePulmonaryBCDetected04");
		description.addProperty("relapsePulmonaryBCDetected0514");
		description.addProperty("relapsePulmonaryBCDetected1517");
		description.addProperty("relapsePulmonaryCDDetected");
		description.addProperty("relapsePulmonaryCDDetected04");
		description.addProperty("relapsePulmonaryCDDetected0514");
		description.addProperty("relapsePulmonaryCDDetected1517");
		description.addProperty("relapseExtrapulmonaryDetected");
		description.addProperty("relapseExtrapulmonaryDetected04");
		description.addProperty("relapseExtrapulmonaryDetected0514");
		description.addProperty("relapseExtrapulmonaryDetected1517");
		description.addProperty("relapseAllDetected");
		description.addProperty("relapseAllDetected04");
		description.addProperty("relapseAllDetected0514");
		description.addProperty("relapseAllDetected1517");
		description.addProperty("relapsePulmonaryBCEligible");
		description.addProperty("relapsePulmonaryBCEligible04");
		description.addProperty("relapsePulmonaryBCEligible0514");
		description.addProperty("relapsePulmonaryBCEligible1517");
		description.addProperty("relapsePulmonaryCDEligible");
		description.addProperty("relapsePulmonaryCDEligible04");
		description.addProperty("relapsePulmonaryCDEligible0514");
		description.addProperty("relapsePulmonaryCDEligible1517");
		description.addProperty("relapseExtrapulmonaryEligible");
		description.addProperty("relapseExtrapulmonaryEligible04");
		description.addProperty("relapseExtrapulmonaryEligible0514");
		description.addProperty("relapseExtrapulmonaryEligible1517");
		description.addProperty("relapseAllEligible");
		description.addProperty("relapseAllEligible04");
		description.addProperty("relapseAllEligible0514");
		description.addProperty("relapseAllEligible1517");
		description.addProperty("relapsePulmonaryBCCured");
		description.addProperty("relapsePulmonaryBCCured04");
		description.addProperty("relapsePulmonaryBCCured0514");
		description.addProperty("relapsePulmonaryBCCured1517");
		description.addProperty("relapsePulmonaryCDCured");
		description.addProperty("relapsePulmonaryCDCured04");
		description.addProperty("relapsePulmonaryCDCured0514");
		description.addProperty("relapsePulmonaryCDCured1517");
		description.addProperty("relapseExtrapulmonaryCured");
		description.addProperty("relapseExtrapulmonaryCured04");
		description.addProperty("relapseExtrapulmonaryCured0514");
		description.addProperty("relapseExtrapulmonaryCured1517");
		description.addProperty("relapseAllCured");
		description.addProperty("relapseAllCured04");
		description.addProperty("relapseAllCured0514");
		description.addProperty("relapseAllCured1517");
		description.addProperty("relapsePulmonaryBCCompleted");
		description.addProperty("relapsePulmonaryBCCompleted04");
		description.addProperty("relapsePulmonaryBCCompleted0514");
		description.addProperty("relapsePulmonaryBCCompleted1517");
		description.addProperty("relapsePulmonaryCDCompleted");
		description.addProperty("relapsePulmonaryCDCompleted04");
		description.addProperty("relapsePulmonaryCDCompleted0514");
		description.addProperty("relapsePulmonaryCDCompleted1517");
		description.addProperty("relapseExtrapulmonaryCompleted");
		description.addProperty("relapseExtrapulmonaryCompleted04");
		description.addProperty("relapseExtrapulmonaryCompleted0514");
		description.addProperty("relapseExtrapulmonaryCompleted1517");
		description.addProperty("relapseAllCompleted");
		description.addProperty("relapseAllCompleted04");
		description.addProperty("relapseAllCompleted0514");
		description.addProperty("relapseAllCompleted1517");
		description.addProperty("relapsePulmonaryBCDiedTB");
		description.addProperty("relapsePulmonaryBCDiedTB04");
		description.addProperty("relapsePulmonaryBCDiedTB0514");
		description.addProperty("relapsePulmonaryBCDiedTB1517");
		description.addProperty("relapsePulmonaryCDDiedTB");
		description.addProperty("relapsePulmonaryCDDiedTB04");
		description.addProperty("relapsePulmonaryCDDiedTB0514");
		description.addProperty("relapsePulmonaryCDDiedTB1517");
		description.addProperty("relapseExtrapulmonaryDiedTB");
		description.addProperty("relapseExtrapulmonaryDiedTB04");
		description.addProperty("relapseExtrapulmonaryDiedTB0514");
		description.addProperty("relapseExtrapulmonaryDiedTB1517");
		description.addProperty("relapseAllDiedTB");
		description.addProperty("relapseAllDiedTB04");
		description.addProperty("relapseAllDiedTB0514");
		description.addProperty("relapseAllDiedTB1517");
		description.addProperty("relapsePulmonaryBCDiedNotTB");
		description.addProperty("relapsePulmonaryBCDiedNotTB04");
		description.addProperty("relapsePulmonaryBCDiedNotTB0514");
		description.addProperty("relapsePulmonaryBCDiedNotTB1517");
		description.addProperty("relapsePulmonaryCDDiedNotTB");
		description.addProperty("relapsePulmonaryCDDiedNotTB04");
		description.addProperty("relapsePulmonaryCDDiedNotTB0514");
		description.addProperty("relapsePulmonaryCDDiedNotTB1517");
		description.addProperty("relapseExtrapulmonaryDiedNotTB");
		description.addProperty("relapseExtrapulmonaryDiedNotTB04");
		description.addProperty("relapseExtrapulmonaryDiedNotTB0514");
		description.addProperty("relapseExtrapulmonaryDiedNotTB1517");
		description.addProperty("relapseAllDiedNotTB");
		description.addProperty("relapseAllDiedNotTB04");
		description.addProperty("relapseAllDiedNotTB0514");
		description.addProperty("relapseAllDiedNotTB1517");
		description.addProperty("relapsePulmonaryBCFailed");
		description.addProperty("relapsePulmonaryBCFailed04");
		description.addProperty("relapsePulmonaryBCFailed0514");
		description.addProperty("relapsePulmonaryBCFailed1517");
		description.addProperty("relapsePulmonaryCDFailed");
		description.addProperty("relapsePulmonaryCDFailed04");
		description.addProperty("relapsePulmonaryCDFailed0514");
		description.addProperty("relapsePulmonaryCDFailed1517");
		description.addProperty("relapseExtrapulmonaryFailed");
		description.addProperty("relapseExtrapulmonaryFailed04");
		description.addProperty("relapseExtrapulmonaryFailed0514");
		description.addProperty("relapseExtrapulmonaryFailed1517");
		description.addProperty("relapseAllFailed");
		description.addProperty("relapseAllFailed04");
		description.addProperty("relapseAllFailed0514");
		description.addProperty("relapseAllFailed1517");
		description.addProperty("relapsePulmonaryBCDefaulted");
		description.addProperty("relapsePulmonaryBCDefaulted04");
		description.addProperty("relapsePulmonaryBCDefaulted0514");
		description.addProperty("relapsePulmonaryBCDefaulted1517");
		description.addProperty("relapsePulmonaryCDDefaulted");
		description.addProperty("relapsePulmonaryCDDefaulted04");
		description.addProperty("relapsePulmonaryCDDefaulted0514");
		description.addProperty("relapsePulmonaryCDDefaulted1517");
		description.addProperty("relapseExtrapulmonaryDefaulted");
		description.addProperty("relapseExtrapulmonaryDefaulted04");
		description.addProperty("relapseExtrapulmonaryDefaulted0514");
		description.addProperty("relapseExtrapulmonaryDefaulted1517");
		description.addProperty("relapseAllDefaulted");
		description.addProperty("relapseAllDefaulted04");
		description.addProperty("relapseAllDefaulted0514");
		description.addProperty("relapseAllDefaulted1517");
		description.addProperty("relapsePulmonaryBCTransferOut");
		description.addProperty("relapsePulmonaryBCTransferOut04");
		description.addProperty("relapsePulmonaryBCTransferOut0514");
		description.addProperty("relapsePulmonaryBCTransferOut1517");
		description.addProperty("relapsePulmonaryCDTransferOut");
		description.addProperty("relapsePulmonaryCDTransferOut04");
		description.addProperty("relapsePulmonaryCDTransferOut0514");
		description.addProperty("relapsePulmonaryCDTransferOut1517");
		description.addProperty("relapseExtrapulmonaryTransferOut");
		description.addProperty("relapseExtrapulmonaryTransferOut04");
		description.addProperty("relapseExtrapulmonaryTransferOut0514");
		description.addProperty("relapseExtrapulmonaryTransferOut1517");
		description.addProperty("relapseAllTransferOut");
		description.addProperty("relapseAllTransferOut04");
		description.addProperty("relapseAllTransferOut0514");
		description.addProperty("relapseAllTransferOut1517");
		description.addProperty("relapsePulmonaryBCCanceled");
		description.addProperty("relapsePulmonaryBCCanceled04");
		description.addProperty("relapsePulmonaryBCCanceled0514");
		description.addProperty("relapsePulmonaryBCCanceled1517");
		description.addProperty("relapsePulmonaryCDCanceled");
		description.addProperty("relapsePulmonaryCDCanceled04");
		description.addProperty("relapsePulmonaryCDCanceled0514");
		description.addProperty("relapsePulmonaryCDCanceled1517");
		description.addProperty("relapseExtrapulmonaryCanceled");
		description.addProperty("relapseExtrapulmonaryCanceled04");
		description.addProperty("relapseExtrapulmonaryCanceled0514");
		description.addProperty("relapseExtrapulmonaryCanceled1517");
		description.addProperty("relapseAllCanceled");
		description.addProperty("relapseAllCanceled04");
		description.addProperty("relapseAllCanceled0514");
		description.addProperty("relapseAllCanceled1517");
		description.addProperty("relapsePulmonaryBCSLD");
		description.addProperty("relapsePulmonaryBCSLD04");
		description.addProperty("relapsePulmonaryBCSLD0514");
		description.addProperty("relapsePulmonaryBCSLD1517");
		description.addProperty("relapsePulmonaryCDSLD");
		description.addProperty("relapsePulmonaryCDSLD04");
		description.addProperty("relapsePulmonaryCDSLD0514");
		description.addProperty("relapsePulmonaryCDSLD1517");
		description.addProperty("relapseExtrapulmonarySLD");
		description.addProperty("relapseExtrapulmonarySLD04");
		description.addProperty("relapseExtrapulmonarySLD0514");
		description.addProperty("relapseExtrapulmonarySLD1517");
		description.addProperty("relapseAllSLD");
		description.addProperty("relapseAllSLD04");
		description.addProperty("relapseAllSLD0514");
		description.addProperty("relapseAllSLD1517");
		description.addProperty("failurePulmonaryBCDetected");
		description.addProperty("failurePulmonaryCDDetected");
		description.addProperty("failureExtrapulmonaryDetected");
		description.addProperty("failureAllDetected");
		description.addProperty("failurePulmonaryBCEligible");
		description.addProperty("failurePulmonaryCDEligible");
		description.addProperty("failureExtrapulmonaryEligible");
		description.addProperty("failureAllEligible");
		description.addProperty("failurePulmonaryBCCured");
		description.addProperty("failurePulmonaryCDCured");
		description.addProperty("failureExtrapulmonaryCured");
		description.addProperty("failureAllCured");
		description.addProperty("failurePulmonaryBCCompleted");
		description.addProperty("failurePulmonaryCDCompleted");
		description.addProperty("failureExtrapulmonaryCompleted");
		description.addProperty("failureAllCompleted");
		description.addProperty("failurePulmonaryBCDiedTB");
		description.addProperty("failurePulmonaryCDDiedTB");
		description.addProperty("failureExtrapulmonaryDiedTB");
		description.addProperty("failureAllDiedTB");
		description.addProperty("failurePulmonaryBCDiedNotTB");
		description.addProperty("failurePulmonaryCDDiedNotTB");
		description.addProperty("failureExtrapulmonaryDiedNotTB");
		description.addProperty("failureAllDiedNotTB");
		description.addProperty("failurePulmonaryBCFailed");
		description.addProperty("failurePulmonaryCDFailed");
		description.addProperty("failureExtrapulmonaryFailed");
		description.addProperty("failureAllFailed");
		description.addProperty("failurePulmonaryBCDefaulted");
		description.addProperty("failurePulmonaryCDDefaulted");
		description.addProperty("failureExtrapulmonaryDefaulted");
		description.addProperty("failureAllDefaulted");
		description.addProperty("failurePulmonaryBCTransferOut");
		description.addProperty("failurePulmonaryCDTransferOut");
		description.addProperty("failureExtrapulmonaryTransferOut");
		description.addProperty("failureAllTransferOut");
		description.addProperty("failurePulmonaryBCCanceled");
		description.addProperty("failurePulmonaryCDCanceled");
		description.addProperty("failureExtrapulmonaryCanceled");
		description.addProperty("failureAllCanceled");
		description.addProperty("failurePulmonaryBCSLD");
		description.addProperty("failurePulmonaryCDSLD");
		description.addProperty("failureExtrapulmonarySLD");
		description.addProperty("failureAllSLD");
		description.addProperty("defaultPulmonaryBCDetected");
		description.addProperty("defaultPulmonaryCDDetected");
		description.addProperty("defaultExtrapulmonaryDetected");
		description.addProperty("defaultAllDetected");
		description.addProperty("defaultPulmonaryBCEligible");
		description.addProperty("defaultPulmonaryCDEligible");
		description.addProperty("defaultExtrapulmonaryEligible");
		description.addProperty("defaultAllEligible");
		description.addProperty("defaultPulmonaryBCCured");
		description.addProperty("defaultPulmonaryCDCured");
		description.addProperty("defaultExtrapulmonaryCured");
		description.addProperty("defaultAllCured");
		description.addProperty("defaultPulmonaryBCCompleted");
		description.addProperty("defaultPulmonaryCDCompleted");
		description.addProperty("defaultExtrapulmonaryCompleted");
		description.addProperty("defaultAllCompleted");
		description.addProperty("defaultPulmonaryBCDiedTB");
		description.addProperty("defaultPulmonaryCDDiedTB");
		description.addProperty("defaultExtrapulmonaryDiedTB");
		description.addProperty("defaultAllDiedTB");
		description.addProperty("defaultPulmonaryBCDiedNotTB");
		description.addProperty("defaultPulmonaryCDDiedNotTB");
		description.addProperty("defaultExtrapulmonaryDiedNotTB");
		description.addProperty("defaultAllDiedNotTB");
		description.addProperty("defaultPulmonaryBCFailed");
		description.addProperty("defaultPulmonaryCDFailed");
		description.addProperty("defaultExtrapulmonaryFailed");
		description.addProperty("defaultAllFailed");
		description.addProperty("defaultPulmonaryBCDefaulted");
		description.addProperty("defaultPulmonaryCDDefaulted");
		description.addProperty("defaultExtrapulmonaryDefaulted");
		description.addProperty("defaultAllDefaulted");
		description.addProperty("defaultPulmonaryBCTransferOut");
		description.addProperty("defaultPulmonaryCDTransferOut");
		description.addProperty("defaultExtrapulmonaryTransferOut");
		description.addProperty("defaultAllTransferOut");
		description.addProperty("defaultPulmonaryBCCanceled");
		description.addProperty("defaultPulmonaryCDCanceled");
		description.addProperty("defaultExtrapulmonaryCanceled");
		description.addProperty("defaultAllCanceled");
		description.addProperty("defaultPulmonaryBCSLD");
		description.addProperty("defaultPulmonaryCDSLD");
		description.addProperty("defaultExtrapulmonarySLD");
		description.addProperty("defaultAllSLD");
		description.addProperty("otherPulmonaryBCDetected");
		description.addProperty("otherPulmonaryCDDetected");
		description.addProperty("otherExtrapulmonaryDetected");
		description.addProperty("otherAllDetected");
		description.addProperty("otherPulmonaryBCEligible");
		description.addProperty("otherPulmonaryCDEligible");
		description.addProperty("otherExtrapulmonaryEligible");
		description.addProperty("otherAllEligible");
		description.addProperty("otherPulmonaryBCCured");
		description.addProperty("otherPulmonaryCDCured");
		description.addProperty("otherExtrapulmonaryCured");
		description.addProperty("otherAllCured");
		description.addProperty("otherPulmonaryBCCompleted");
		description.addProperty("otherPulmonaryCDCompleted");
		description.addProperty("otherExtrapulmonaryCompleted");
		description.addProperty("otherAllCompleted");
		description.addProperty("otherPulmonaryBCDiedTB");
		description.addProperty("otherPulmonaryCDDiedTB");
		description.addProperty("otherExtrapulmonaryDiedTB");
		description.addProperty("otherAllDiedTB");
		description.addProperty("otherPulmonaryBCDiedNotTB");
		description.addProperty("otherPulmonaryCDDiedNotTB");
		description.addProperty("otherExtrapulmonaryDiedNotTB");
		description.addProperty("otherAllDiedNotTB");
		description.addProperty("otherPulmonaryBCFailed");
		description.addProperty("otherPulmonaryCDFailed");
		description.addProperty("otherExtrapulmonaryFailed");
		description.addProperty("otherAllFailed");
		description.addProperty("otherPulmonaryBCDefaulted");
		description.addProperty("otherPulmonaryCDDefaulted");
		description.addProperty("otherExtrapulmonaryDefaulted");
		description.addProperty("otherAllDefaulted");
		description.addProperty("otherPulmonaryBCTransferOut");
		description.addProperty("otherPulmonaryCDTransferOut");
		description.addProperty("otherExtrapulmonaryTransferOut");
		description.addProperty("otherAllTransferOut");
		description.addProperty("otherPulmonaryBCCanceled");
		description.addProperty("otherPulmonaryCDCanceled");
		description.addProperty("otherExtrapulmonaryCanceled");
		description.addProperty("otherAllCanceled");
		description.addProperty("otherPulmonaryBCSLD");
		description.addProperty("otherPulmonaryCDSLD");
		description.addProperty("otherExtrapulmonarySLD");
		description.addProperty("otherAllSLD");
		description.addProperty("rtxPulmonaryBCDetected");
		description.addProperty("rtxPulmonaryCDDetected");
		description.addProperty("rtxExtrapulmonaryDetected");
		description.addProperty("rtxAllDetected");
		description.addProperty("rtxPulmonaryBCEligible");
		description.addProperty("rtxPulmonaryCDEligible");
		description.addProperty("rtxExtrapulmonaryEligible");
		description.addProperty("rtxAllEligible");
		description.addProperty("rtxPulmonaryBCCured");
		description.addProperty("rtxPulmonaryCDCured");
		description.addProperty("rtxExtrapulmonaryCured");
		description.addProperty("rtxAllCured");
		description.addProperty("rtxPulmonaryBCCompleted");
		description.addProperty("rtxPulmonaryCDCompleted");
		description.addProperty("rtxExtrapulmonaryCompleted");
		description.addProperty("rtxAllCompleted");
		description.addProperty("rtxPulmonaryBCDiedTB");
		description.addProperty("rtxPulmonaryCDDiedTB");
		description.addProperty("rtxExtrapulmonaryDiedTB");
		description.addProperty("rtxAllDiedTB");
		description.addProperty("rtxPulmonaryBCDiedNotTB");
		description.addProperty("rtxPulmonaryCDDiedNotTB");
		description.addProperty("rtxExtrapulmonaryDiedNotTB");
		description.addProperty("rtxAllDiedNotTB");
		description.addProperty("rtxPulmonaryBCFailed");
		description.addProperty("rtxPulmonaryCDFailed");
		description.addProperty("rtxExtrapulmonaryFailed");
		description.addProperty("rtxAllFailed");
		description.addProperty("rtxPulmonaryBCDefaulted");
		description.addProperty("rtxPulmonaryCDDefaulted");
		description.addProperty("rtxExtrapulmonaryDefaulted");
		description.addProperty("rtxAllDefaulted");
		description.addProperty("rtxPulmonaryBCTransferOut");
		description.addProperty("rtxPulmonaryCDTransferOut");
		description.addProperty("rtxExtrapulmonaryTransferOut");
		description.addProperty("rtxAllTransferOut");
		description.addProperty("rtxPulmonaryBCCanceled");
		description.addProperty("rtxPulmonaryCDCanceled");
		description.addProperty("rtxExtrapulmonaryCanceled");
		description.addProperty("rtxAllCanceled");
		description.addProperty("rtxPulmonaryBCSLD");
		description.addProperty("rtxPulmonaryCDSLD");
		description.addProperty("rtxExtrapulmonarySLD");
		description.addProperty("rtxAllSLD");
		description.addProperty("allDetected");
		description.addProperty("allEligible");
		description.addProperty("allCured");
		description.addProperty("allCompleted");
		description.addProperty("allDiedTB");
		description.addProperty("allDiedNotTB");
		description.addProperty("allFailed");
		description.addProperty("allDefaulted");
		description.addProperty("allTransferOut");
		description.addProperty("allCanceled");
		description.addProperty("allSLD");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		return delegatingResourceDescription;
	}
	
	@Override
	public SimpleTB08Data newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB08Data save(SimpleTB08Data delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleTB08Data getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleTB08Data delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleTB08Data delegate, RequestContext context) throws ResponseException {
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
		TB08Data tb08Data = TB08ReportController.getTB08PatientSet(year, quarter, month, locList);
		List<SimpleTB08Data> list = new ArrayList<SimpleTB08Data>();
		list.add(new SimpleTB08Data(tb08Data));
		return new NeedsPaging<SimpleTB08Data>(list, context);
	}
}