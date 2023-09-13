package org.openmrs.module.mdrtb.specimen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestAttribute;
import org.openmrs.module.commonlabtest.LabTestAttributeType;
import org.openmrs.module.commonlabtest.LabTestSample;
import org.openmrs.module.commonlabtest.api.CommonLabTestService;
import org.openmrs.module.mdrtb.CommonLabUtil;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.specimen.SpecimenConstants.TestStatus;

/**
 * An implementation of a MdrtbDst. This wraps an ObsGroup and provides access to dst data within
 * the obsgroup.
 */
public class DstImpl extends TestImpl implements Dst {
		
	protected final Log log = LogFactory.getLog(getClass());
	
	private Set<LabTestAttribute> drugAttributes;
	
	private Map<Concept, Concept> resultSet; // stores all the possible values for the drug results

	private List<DstImpl> resultsMap = null;
	
	public DstImpl() {
		this.resultSet = initResultSet();
		// test = new Obs(null, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT), null, null);
		// also instantiate an empty obs so that we can use this as a backing object
		dstResult = new Obs();
	}
	
	public DstImpl(LabTest dst) {
		if (dst == null) {
			throw new RuntimeException("Cannot initialize test: Parameter is null.");
		}
		test = dst;
		this.resultSet = initResultSet();
		// also instantiate an empty obs so that we can use this as a backing object
		dstResult = new Obs();
	}

	/**
	 * Initializes the resultSet property, which stores all the possible result types for a DST
	 * result
	 */
	private Set<Concept> initResultSet() {
		Set<Concept> resultSet = new HashSet<Concept>();
		resultSet.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG));
		resultSet.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.INTERMEDIATE_TO_TB_DRUG));
		resultSet.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG));
		resultSet.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS));
		resultSet.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONTAMINATED));
		resultSet.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NONE));
		return resultSet;
	}

	public Object getTest() {
		return null;
	}
	
	public String getId() {
		if (test.getId() != null) {
			return test.getId().toString();
		}
		return null;
	}
	
	public TestStatus getStatus() {
		return null;
	}
	
	public String getTestType() {
		return "dst";
	}
	
	public String getSpecimenId() {
		return null;
	}
	
	public Date getDateCollected() {
		return null;
	}
	
	public String getAccessionNumber() {
		return null;
	}
	
	// public void setAccessionNumber(String accessionNumber);
	
	public Date getStartDate() {
		return test.getOrder().getEffectiveStartDate();
	}
	
	public void setStartDate(Date startDate) {
		test.getOrder().setScheduledDate(startDate);
	}
	
	// INVESTIGATION DATE
	public Date getResultDate() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, "INVESTIGATION DATE", 
			DstTestType.DST2);
		// Return the date in respective Specimen sample if the attribute is not available
		if (attribute == null) {
			LabTestSample testSample = CommonLabUtil.getService().getMostRecentAcceptedSample(test);
			return testSample.getProcessedDate();
		}
		return (Date) attribute.getValue();
	}
	
	public void setResultDate(Date resultDate) {		
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, "INVESTIGATION DATE",
		    DstTestType.DST2);
		attribute.setValue(resultDate);
		test.setAttribute(attribute);
		LabTestSample testSample = CommonLabUtil.getService().getMostRecentAcceptedSample(test);
		testSample.setProcessedDate(resultDate);
	}
	
	public Date getDateOrdered() {
		return test.getOrder().getDateCreated();
	}
	
	public void setDateOrdered(Date dateOrdered) {
		test.getOrder().setDateCreated(dateOrdered);
	}
	
	// DATE COLLECTED
	public Date getDateReceived() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, "DATE COLLECTED", 
			DstTestType.DST2);
		return (Date) attribute.getValue();
	}
	
	public void setDateReceived(Date dateReceived) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, "DATE COLLECTED", 
			DstTestType.DST2);
		attribute.setValue(dateReceived);
		test.setAttribute(attribute);
		LabTestSample testSample = CommonLabUtil.getService().getMostRecentAcceptedSample(test);
		testSample.setCollectionDate(dateReceived);
	}
	
	//TODO: Confirm if this is the right one or is it a Test attribute
	public Location getLab() {
		return test.getOrder().getEncounter().getLocation();
	}
	
	//TODO: Confirm if this is the right one or is it a Test attribute
	public void setLab(Location location) {
		test.getOrder().getEncounter().setLocation(location);
	}
	
	public String getComments() {
		return test.getResultComments();
	}
	
	public void setComments(String comments) {
		test.setResultComments(comments);
	}
	
	public String getRealSpecimenId() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, MdrtbConcepts.SPECIMEN_ID,
		    DstTestType.DST2);
		return (String) attribute.getValue();
	}
	
	public Concept getMethod() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, MdrtbConcepts.DST_METHOD,
		    DstTestType.DST2);
		return (Concept) attribute.getValue();
	}
	
	public void setMethod(Concept method) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test, MdrtbConcepts.DST_METHOD,
		    DstTestType.DST2);
		attribute.setValue(method);
		test.setAttribute(attribute);
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
	
	public Boolean getDirect() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.DIRECT_INDIRECT, DstTestType.DST2);
		return (Boolean) attribute.getValue();
	}
	
	public void setDirect(Boolean direct) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.DIRECT_INDIRECT, DstTestType.DST2);
		attribute.setValue(direct);
		test.setAttribute(attribute);
	}
	
	public Concept getOrganismType() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM, DstTestType.DST2);
		return (Concept) attribute.getValue();
	}
	
	public void setOrganismType(Concept organismType) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM, DstTestType.DST2);
		attribute.setValue(organismType);
		test.setAttribute(attribute);
	}
	
	public String getOrganismTypeNonCoded() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED, DstTestType.DST2);
		return (String) attribute.getValue();
	}
	
	public void setOrganismTypeNonCoded(String organismType) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED, DstTestType.DST2);
		attribute.setValue(organismType);
		test.setAttribute(attribute);
	}
	
	public Integer getConcentration() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.CONCENTRATION, DstTestType.DST2);
		return (Integer) attribute.getValue();
	}
	
	public void setConcentration(Integer concentration) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.CONCENTRATION, DstTestType.DST2);
		attribute.setValue(concentration);
		test.setAttribute(attribute);
	}
	
	public Integer getColoniesInControl() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES_IN_CONTROL, DstTestType.DST2);
		return (Integer) attribute.getValue();
	}
	
	public void setColonies(Integer colonies) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES, DstTestType.DST2);
		attribute.setValue(colonies);
		test.setAttribute(attribute);
	}

	public Integer getColonies() {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES, DstTestType.DST2);
		return (Integer) attribute.getValue();
	}
	
	public void setColoniesInControl(Integer coloniesInControl) {
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES_IN_CONTROL, DstTestType.DST2);
		attribute.setValue(coloniesInControl);
		test.setAttribute(attribute);
	}
	
	public List<DstImpl> getResults() {
		List<DstImpl> results = new LinkedList<DstImpl>();
		// Get all DST tests for this patient
		Patient patient = test.getOrder().getEncounter().getPatient();
		CommonLabUtil service = CommonLabUtil.getService();
		List<LabTest> dstTests = service.getLabTests(patient);
		for (LabTest labTest : dstTests) {
			// Check if there's DST Result attached with this test
			LabTestAttributeType dstResultType = CommonLabUtil.getService().getLabTestAttributeTypeByName(
			    MdrtbConcepts.DST_RESULT);
			if (labTest.getAttribute(dstResultType) != null) {
				results.add(new DstImpl(labTest));
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
	/**
	 * This method should return a {@link Map} of <Concept, Concept> for each drug and its result
	 */
	public List<DstImpl> getResultsMap() {
		Collection<Concept> drugs = getPossibleDrugTypes();
		Map<Concept, Concept> drugResultsMap = new HashMap<>(drugResultsMap.size() + 1);
		LabTestAttribute attribute = CommonLabUtil.getService().getDstAttributeByTestAndName(test,
		    MdrtbConcepts.CONCENTRATION, DstTestType.DST2);
		for (Concept drug : drugs) {
			drugResultsMap.put(drug, getDrugResult(drug));
		}
		
		if (resultsMap == null) {
			resultsMap = new ArrayList<DstImpl>();
			// map the results based on a key created by concatenating the string representation of the drug concept id and the
			// string representation of the concentration
			for (DstImpl result : getResults()) {
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
						List<DstImpl> drugResults = new LinkedList<DstImpl>();
						drugResults.add(result);
						resultsMap.put(drug, drugResults);
					}
				}
			}
		}
		return resultsMap;
	}
	
	//FIXME: Urgent! Figure out what to do with this after upgrade to common lab. How to add a new DST result set?
	public DstImpl addResult() {
		return new DstImpl();
	}
	
	public void removeResult(DstImpl result) {
		//FIXME: Remove result should mean removing a drug from results
	}
	
	public String getResultsString() {
		StringBuffer results = new StringBuffer();
		for (LabTestAttribute drugAtt : drugAttributes) {
			results.append(drugAtt.getAttributeType().getName());
			results.append(": ");
			results.append(((Concept)drugAtt.getValue()).getShortNameInLocale(Context.getLocale()));
			results.append("<br/>");
		}
		/*
		Map<Integer, List<DstResult>> dstResultsMap = getResultsMap();
		Collection<Concept> drugs = getPossibleDrugTypes();
		for (Concept drug : drugs) {
			if (dstResultsMap.get(drug.getId()) != null) {
				
				for (DstResult result : dstResultsMap.get(drug.getId())) {
					results += result.getDrug().getDisplayString() + ": "
					        + result.getResult().getShortNameInLocale(Context.getLocale()) + "<br/>";
				}
			}
		}*/
		if (results.length() == 0) {
			return "N/A";
		}
		return results.toString().substring(0, results.length() - 1);
	}
	
	public String getResistantDrugs() {
		StringBuffer results = new StringBuffer();
		Concept resistant = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG);
		// For every drug in drug attributes, attach the ones that are resistant
		for (LabTestAttribute drugAtt : drugAttributes) {
			if (drugAtt.getValue().equals(resistant)) {
				results.append(drugAtt.getAttributeType().getName());
				results.append(",");
			}
		}
		/*
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
		}*/
		if (results.length() == 0) {
			return "N/A";
		}
		return results.toString().substring(0, results.length() - 1);
	}
	
	public String getSensitiveDrugs() {
		StringBuffer results = new StringBuffer();
		Concept resistant = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG);
		// For every drug in drug attributes, attach the ones that are resistant
		for (LabTestAttribute drugAtt : drugAttributes) {
			if (drugAtt.getValue().equals(resistant)) {
				results.append(drugAtt.getAttributeType().getName());
				results.append(",");
			}
		}
		/*
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
		*/
		if (results.length() == 0) {
			return "N/A";
		}
		return results.toString().substring(0, results.length() - 1);
	}
	
	// TODO: Get rid of the below two
	Obs dstResult; // the top-level obs that holds all the data for this result
	Concept drug; // a secondary location to store drug-type; used as a workaround for the odd way that the result and the drug are stored in a single obs
		
	public void setResults() {
	}
	
	public Collection<Concept> getPossibleDrugTypes() {
		return Context.getService(MdrtbService.class).getMdrtbDrugs();
	}
	
	public Concept getDrug() {
		Obs obs = getResultObs();
		if (obs == null) {
			// return the temporary drug-type store if it has been defined
			if (this.drug != null) {
				return drug;
			}
			return null;
		}
		return obs.getValueCoded();
	}
	
	public void setDrug(Concept drug) {
		// if the drug hasn't changed, there is nothing to do
		if (getDrug() == drug) {
			return;
		}
		
		// if the result hasn't been set yet, we have to temporarily store the drug elsewhere
		if (getResult() == null) {
			this.drug = drug;
		} else {
			// otherwise, set the drug
			getResultObs().setValueCoded(drug);
		}
	}
	
	/**
	 * Utility method for copying a DST
	 */
	public void copyMembersFrom(Test source) {
		super.copyMembersFrom(source);
		setColoniesInControl(((Dst) source).getColoniesInControl());
		setMethod(((Dst) source).getMethod());
		setOrganismType(((Dst) source).getOrganismType());
		setOrganismTypeNonCoded(((Dst) source).getOrganismTypeNonCoded());
		setDirect(((Dst) source).getDirect());
		
		for (DstImpl dstResult : ((Dst) source).getResults()) {
			DstImpl newDstResult = this.addResult();
			newDstResult.copyMembersFrom(dstResult);
		}
	}
	
	/**
	 * Utility method for copying a dst result
	 */
	public void copyMembersFrom(DstImpl source) {
		this.setColoniesInControl(source.getColoniesInControl());
		this.setConcentration(source.getConcentration());
		this.setDrug(source.getDrug());
		this.setResult(source.getResult());
	}
	
	/**
	 * Comparable interface method and utility methods. This simply sorts by concentration for now,
	 * so it doesn't really make sense to use except for a set when the drugs are the same, which is
	 * all we use it for now. May want to expand it to sort alphabetically, or by the
	 * "possibleDrugTypesToDisplay" list)
	 */
	public int compareTo(DstImpl dstResult1) {
		if (dstResult1 == null || dstResult1.getConcentration() == null) {
			return 1;
		} else if (this.getConcentration() == null) {
			return -1;
		} else {
			return this.getConcentration().compareTo(dstResult1.getConcentration());
		}
	}

	/**
	 * Returns the obs that contains the test result
	 */
	// TODO: this method won't work correct if the dst result construct has more than one result concept in it (which is invalid, but we probably should test for it?)
	private Obs getResultObs() {
		if (dstResult.getGroupMembers() != null) {
			for (Obs obs : dstResult.getGroupMembers()) {
				if (!obs.getVoided() && resultSet.contains(obs.getConcept())) {
					return obs;
				}
			}
		}
		return null;
	}
	
	/*
	REFERRING FACILITY
	REQUESTING MEDICAL FACILITY
	TUBERCULOSIS SAMPLE SOURCE
	DATE OF REQUEST FOR LABORATORY INVESTIGATION
	LAB SPECIALIST NAME
	REFERRED BY
	TUBERCULOSIS SPECIMEN ID
	MONTH OF TREATMENT
	PATIENT PROGRAM ID
	DIRECT/INDIRECT
	TYPE OF ORGANISM
	TYPE OF ORGANISM NON-CODED
	TUBERCULOSIS DRUG SENSITIVITY TEST METHOD
	*/
	
	// LABORATORY INVESTIGATION NUMBER
	public String getLabInvestigationNumber() {
		return null;
	}
	
	public void setLabInvestigationNumber(String labInvestigationNumber) {
	}
	
	// PURPOSE OF INVESTIGATION
	public Concept getPurposeOfInvestigation() {
		return null;
	}
	
	public void setPurposeOfInvestigation(Concept purposeOfInvestigation) {
	}

	
}
