package org.openmrs.module.mdrtb.specimen;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestAttribute;
import org.openmrs.module.commonlabtest.LabTestAttributeType;
import org.openmrs.module.commonlabtest.api.CommonLabTestService;
import org.openmrs.module.mdrtb.CommonLabUtil;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.MdrtbService;

/**
 * An implementation of a MdrtbDst. This wraps an ObsGroup and provides access to dst data within
 * the obsgroup.
 */
public class DstImpl extends TestImpl implements Dst {
	
	Map<Integer, List<DstResult>> resultsMap = null;
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public DstImpl() {
		// test = new Obs(null, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT), null, null);
	}
	
	// set up a dst object, given an existing obs
	/*
	public DstImpl(Obs dst) {
		if (dst == null
		        || !(dst.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT)))) {
			throw new RuntimeException("Cannot initialize dst: invalid obs used for initialization.");
		}
		test = dst;
	}
	*/
	
	// create a new culture object, given an existing patient
	/*
	public DstImpl(Encounter encounter) {
		if (encounter == null) {
			throw new RuntimeException("Cannot create culture: encounter can not be null.");
		}
		Concept concept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT);
		Obs obs = MdrtbUtil.getObsFromEncounter(concept, encounter);
		test = obs;
		if (obs == null) {
			test = new Obs(encounter.getPatient(), concept, encounter.getEncounterDatetime(), encounter.getLocation());
			test.setEncounter(encounter);
		}
	}
	*/
	
	public DstImpl(LabTest dst) {
		if (dst == null) {
			throw new RuntimeException("Cannot initialize test: Parameter is null.");
		}
		test = dst;
	}
	
	@Override
	public String getTestType() {
		return "dst";
	}
	
	// TODO: Urgent! Figure out what to do with this after upgrade to common lab. How to add a new DST result set?
	public DstResult addResult() {
		return new DstResult();
		/*
		// create a new obs for the result, set to the proper values
		Obs resultObs = new Obs(this.test.getPerson(), Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.DST_RESULT), this.test.getObsDatetime(), this.test.getLocation());
		resultObs.setEncounter(this.test.getEncounter());
		// add the result to this obs group
		this.test.addGroupMember(resultObs);
		// now create and return a new DstResult
		return new DstResult(resultObs);
		*/
	}
	
	public void setResults() {
		/*
		DIRECT/INDIRECT
		TYPE OF ORGANISM
		TYPE OF ORGANISM NON-CODED
		OFLOXACIN RESISTANCE
		MOXIFLOXACIN RESISTANCE
		LEVOFLOXACIN RESISTANCE
		PROTHIONAMIDE RESISTANCE
		LINEZOLID RESISTANCE
		CLOFAZAMINE RESISTANCE
		BEDAQUILINE RESISTANCE
		DELAMANID RESISTANCE
		P-AMINOSALICY RESISTANCE
		CAPREOMYCIN RESISTANCE
		KANAMYCIN RESISTANCE
		AMIKACIN RESISTANCE
		COLONIES
		CONCENTRATION
		COLONIES IN CONTROL
		TUBERCULOSIS DRUG SENSITIVITY TEST METHOD
		*/
	}
	
	public Integer getColoniesInControl() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES_IN_CONTROL, DstTestType.DST2);
		return (Integer) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES_IN_CONTROL), test);
		return (obs == null || obs.getValueNumeric() == null) ? null : obs.getValueNumeric().intValue();
		*/
	}
	
	public void setColoniesInControl(Integer coloniesInControl) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES_IN_CONTROL, DstTestType.DST2);
		attribute.setValue(coloniesInControl);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES_IN_CONTROL), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && coloniesInControl == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (coloniesInControl == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.COLONIES_IN_CONTROL), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now set the value
		obs.setValueNumeric(coloniesInControl.doubleValue());
		*/
	}
	
	public String getComments() {
		return test.getResultComments();
	}
	
	public void setComments(String comments) {
		test.setResultComments(comments);
	}
	
	public Boolean getDirect() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.DIRECT_INDIRECT, DstTestType.DST2);
		return (Boolean) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIRECT_INDIRECT), test);
		return (obs == null) ? null : obs.getValueAsBoolean();
		*/
	}
	
	public void setDirect(Boolean direct) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.DIRECT_INDIRECT, DstTestType.DST2);
		attribute.setValue(direct);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIRECT_INDIRECT), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && direct == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (direct == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(),
			        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIRECT_INDIRECT), test.getObsDatetime(),
			        test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		obs.setValueNumeric(direct ? 1.0 : 0.0);
		*/
	}
	
	public Concept getMethod() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, MdrtbConcepts.DST_METHOD,
		    DstTestType.DST2);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_METHOD), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setMethod(Concept method) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, MdrtbConcepts.DST_METHOD,
		    DstTestType.DST2);
		attribute.setValue(method);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_METHOD), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && method == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (method == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_METHOD),
			        test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now save the value
		obs.setValueCoded(method);
		*/
	}
	
	public Concept getOrganismType() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM, DstTestType.DST2);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setOrganismType(Concept organismType) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM, DstTestType.DST2);
		attribute.setValue(organismType);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && organismType == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (organismType == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.TYPE_OF_ORGANISM), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now save the value
		obs.setValueCoded(organismType);
		*/
	}
	
	public String getOrganismTypeNonCoded() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED, DstTestType.DST2);
		return (String) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED), test);
		return (obs == null) ? null : obs.getValueText();
		*/
	}
	
	public void setOrganismTypeNonCoded(String organismType) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED, DstTestType.DST2);
		attribute.setValue(organismType);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && organismType == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (organismType == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now save the value
		obs.setValueText(organismType);
		*/
	}
	
	public List<DstResult> getResults() {
		List<DstResult> results = new LinkedList<DstResult>();
		// Get all DST tests for this patient
		Patient patient = test.getOrder().getEncounter().getPatient();
		CommonLabUtil service = CommonLabUtil.getService();
		List<LabTest> dstTests = service.getLabTests(patient);
		for (LabTest labTest : dstTests) {
			// Check if there's DST Result attached with this test
			LabTestAttributeType dstResultType = CommonLabUtil.getService().getLabTestAttributeTypeByName(
			    MdrtbConcepts.DST_RESULT);
			if (labTest.getAttribute(dstResultType) != null) {
				results.add(new DstResult(labTest));
			}
		}
		/*
		// iterate through all the obs groups, create dst results from them, and add them to the list
		if (test.getGroupMembers() != null) {
			for (Obs obs : test.getGroupMembers()) {
				// need to filter for voided obs, since get group members returns voided and non-voided
				if (!obs.getVoided()
				        && obs.getConcept().equals(
				            Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_RESULT))) {
					results.add(new DstResult(obs));
				}
			}
		}
		*/
		return results;
	}
	
	// Note this is created ONCE per instantiation, for performance reasons, so if underlying drugs change, this will be inaccurate
	public Map<Integer, List<DstResult>> getResultsMap() {
		
		if (resultsMap == null) {
			resultsMap = new HashMap<Integer, List<DstResult>>();
			
			// map the results based on a key created by concatenating the string representation of the drug concept id and the
			// string representation of the concentration
			for (DstResult result : getResults()) {
				
				if (result.getDrug() != null) {
					Integer drug = result.getDrug().getId();
					
					// if a result for this drug already exists in the map, attach this result to that list
					if (resultsMap.containsKey(drug)) {
						resultsMap.get(drug).add(result);
						// re-sort, so that the concentrations are in order
						Collections.sort(resultsMap.get(drug));
					}
					// otherwise, create a new entry for this drug
					else {
						List<DstResult> drugResults = new LinkedList<DstResult>();
						drugResults.add(result);
						resultsMap.put(drug, drugResults);
					}
				}
				
				/**
				 * if(result.getConcentration() != null) {
				 * resultsMap.put((result.getDrug().getId()).toString() + "|" +
				 * result.getConcentration().toString(), result); } else {
				 * resultsMap.put((result.getDrug().getId()).toString(), result); }
				 */
			}
		}
		
		return resultsMap;
	}
	
	public void removeResult(DstResult result) {
		((DstResult) result).voidResult();
	}
	
	/**
	 * Utility method for copying a DST
	 */
	public void copyMembersFrom(Test source) {
		super.copyMembersFrom(source);
		this.setColoniesInControl(((Dst) source).getColoniesInControl());
		this.setMethod(((Dst) source).getMethod());
		this.setOrganismType(((Dst) source).getOrganismType());
		this.setOrganismTypeNonCoded(((Dst) source).getOrganismTypeNonCoded());
		this.setDirect(((Dst) source).getDirect());
		
		for (DstResult dstResult : ((Dst) source).getResults()) {
			DstResult newDstResult = this.addResult();
			newDstResult.copyMembersFrom(dstResult);
		}
	}
	
	public String getResultsString() {
		String results = "";
		Map<Integer, List<DstResult>> dstResultsMap = getResultsMap();
		Collection<Concept> drugs = getPossibleDrugTypes();
		for (Concept drug : drugs) {
			if (dstResultsMap.get(drug.getId()) != null) {
				
				for (DstResult result : dstResultsMap.get(drug.getId())) {
					results += result.getDrug().getDisplayString() + ": "
					        + result.getResult().getShortNameInLocale(Context.getLocale()) + "<br/>";
				}
			}
		}
		if (results.length() == 0) {
			results = "N/A";
		}
		return results;
	}
	
	public String getResistantDrugs() {
		String results = "";
		Map<Integer, List<DstResult>> dstResultsMap = getResultsMap();
		Collection<Concept> drugs = getPossibleDrugTypes();
		for (Concept drug : drugs) {
			if (dstResultsMap.get(drug.getId()) != null) {
				for (DstResult result : dstResultsMap.get(drug.getId())) {
					if (Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG).getId()
					        .equals(result.getResult().getId())) {
						ConceptName name = result.getDrug().getShortNameInLocale(Context.getLocale());
						if (name == null) {
							name = result.getDrug().getName(Context.getLocale());
						}
						results += name.getName() + ",";
					}
				}
			}
		}
		if (results.length() == 0) {
			results = "N/A";
		} else {
			results = results.substring(0, results.length() - 1);
		}
		return results;
	}
	
	public String getSensitiveDrugs() {
		String results = "";
		Map<Integer, List<DstResult>> dstResultsMap = getResultsMap();
		Collection<Concept> drugs = getPossibleDrugTypes();
		for (Concept drug : drugs) {
			if (dstResultsMap.get(drug.getId()) != null) {
				
				for (DstResult result : dstResultsMap.get(drug.getId())) {
					if (result.getResult().getId().intValue() == Context.getService(MdrtbService.class)
					        .getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG).getId().intValue()) {
						results += result.getDrug().getName().getName() + ",";
					}
				}
			}
		}
		if (results.length() == 0) {
			results = "N/A";
		} else {
			results = results.substring(0, results.length() - 1);
		}
		return results;
	}
	
	public Collection<Concept> getPossibleDrugTypes() {
		return Context.getService(MdrtbService.class).getMdrtbDrugs();
	}
	
	/**
	 * @deprecated Use getResults() instead
	 */
	@Deprecated
	public Concept getResult() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, MdrtbConcepts.DST_RESULT,
		    DstTestType.DST2);
		return (Concept) attribute.getValue();
	}
	
	/**
	 * @deprecated Use individual set methods instead
	 */
	@Deprecated
	public void setResult(Concept result) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, MdrtbConcepts.DST_METHOD,
		    DstTestType.DST2);
		attribute.setValue(result);
		test.setAttribute(attribute);
	}
}
