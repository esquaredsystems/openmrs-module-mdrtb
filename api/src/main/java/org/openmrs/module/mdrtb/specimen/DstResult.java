package org.openmrs.module.mdrtb.specimen;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestAttribute;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;

public class DstResult implements Comparable<DstResult> {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private LabTestAttribute direct;
	
	private LabTestAttribute typeOfOrganism;
	
	private LabTestAttribute typeOfOrganismNonCoded;
	
	private Set<LabTestAttribute> drugAttributes;
	
	private LabTestAttribute colonies;
	
	private LabTestAttribute concentration;
	
	private LabTestAttribute coloniesInControl;
	
	private LabTestAttribute dstMethod;
	
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
	
	Obs dstResult; // the top-level obs that holds all the data for this result
	
	Concept drug; // a secondary location to store drug-type; used as a workaround for the odd way that the result and the drug are stored in a single obs
	
	Set<Concept> resultSet; // stores all the possible values for the results
	
	public DstResult(LabTest dst) {
		this.resultSet = initResultSet();
	}
	
	public DstResult() {
		this.resultSet = initResultSet();
		// also instantiate an empty obs so that we can use this as a backing object
		dstResult = new Obs();
	}
	
	// set up a dst object, given an existing obs, or an existing dst
	public DstResult(Obs dstResult) {
		this.resultSet = initResultSet();
		
		if (dstResult != null
		        && dstResult.getConcept()
		                .equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_RESULT))) {
			this.dstResult = dstResult;
		} else {
			throw new RuntimeException("Cannot initialize dst: invalid obs used for initialization.");
		}
	}
	
	public Object getDstResult() {
		return this.dstResult;
	}
	
	public String getId() {
		if (this.dstResult.getId() != null) {
			return this.dstResult.getId().toString();
		} else {
			return null;
		}
	}
	
	public Integer getColonies() {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES),
		    dstResult);
		
		if (obs == null || obs.getValueNumeric() == null) {
			return null;
		} else {
			return obs.getValueNumeric().intValue();
		}
	}
	
	public Double getConcentration() {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.CONCENTRATION), dstResult);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueNumeric();
		}
	}
	
	public Concept getDrug() {
		Obs obs = getResultObs();
		
		if (obs == null) {
			// return the temporary drug-type store if it has been defined
			if (this.drug != null) {
				return drug;
			} else {
				return null;
			}
		} else {
			return obs.getValueCoded();
		}
	}
	
	public Concept getResult() {
		Obs obs = getResultObs();
		
		if (obs == null) {
			return null;
		} else {
			return obs.getConcept();
		}
	}
	
	public void setColonies(Integer colonies) {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES),
		    dstResult);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && colonies == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if (obs != null && colonies == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(dstResult.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES),
			        dstResult.getObsDatetime(), dstResult.getLocation());
			obs.setEncounter(dstResult.getEncounter());
			dstResult.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(colonies.doubleValue());
	}
	
	public void setConcentration(Double concentration) {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.CONCENTRATION), dstResult);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && concentration == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if (obs != null && concentration == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(dstResult.getPerson(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.CONCENTRATION), dstResult.getObsDatetime(), dstResult.getLocation());
			obs.setEncounter(dstResult.getEncounter());
			dstResult.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(concentration);
	}
	
	public void setResult(Concept result) {
		// if the result hasn't changed, there is nothing to do
		if (getResult() == result) {
			return;
		}
		
		// TODO: is this what we really want to do in this case?
		// if the result has been set to null, that means we need to void the entire obsgroup and remove it from it's parent
		if (result == null) {
			this.dstResult.setVoided(true);
			this.dstResult.setVoidReason("voided by mdr-tb module ui");
			for (Obs obs : dstResult.getGroupMembers()) {
				obs.setVoided(true);
				obs.setVoidReason("voided by mdr-tb module ui");
			}
			return;
		}
		
		// initialize the obs if needed
		if (getResult() == null) {
			Obs obs = new Obs(dstResult.getPerson(), result, dstResult.getObsDatetime(), dstResult.getLocation());
			obs.setEncounter(dstResult.getEncounter());
			
			// set the drug if it has already been defined
			if (this.drug != null) {
				obs.setValueCoded(drug);
				this.drug = null; // get rid of the temporary store
			}
			
			dstResult.addGroupMember(obs);
		}
		// otherwise, just set the concept
		else {
			getResultObs().setConcept(result);
		}
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
	 * Utility method for copying a dst result
	 */
	public void copyMembersFrom(DstResult source) {
		this.setColonies(source.getColonies());
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
	
	public int compareTo(DstResult dstResult1) {
		if (dstResult1 == null || dstResult1.getConcentration() == null) {
			return 1;
		} else if (this.getConcentration() == null) {
			return -1;
		} else {
			return this.getConcentration().compareTo(dstResult1.getConcentration());
		}
	}
	
	/**
	 * Protected methods
	 */
	
	/**
	 * Used by DstImpl to "remove" a result from a DST
	 */
	protected void voidResult() {
		for (Obs obs : this.dstResult.getGroupMembers()) {
			obs.setVoided(true);
			obs.setVoidReason("voided by DstResult class");
		}
		
		this.dstResult.setVoided(true);
		this.dstResult.setVoidReason("voided by DstResult class");
	}
	
	/**
	 * Utility methods
	 */
	
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
}
