package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestAttribute;
import org.openmrs.module.mdrtb.CommonLabUtil;
import org.openmrs.module.mdrtb.MdrtbConcepts;

/**
 * An implementaton of a MdrtbSmear. This wraps an ObsGroup and provides access to smear data within
 * the obsgroup.
 */
public class SmearImpl extends TestImpl implements Smear {
	
	public SmearImpl() {
		// test = new Obs(null, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT), null, null);
	}
	
	// set up a smear object, given an existing obs
	/*
	public SmearImpl(Obs smear) {
		if (smear == null
		        || !(smear.getConcept().equals(Context.getService(MdrtbService.class).getConcept(
		            MdrtbConcepts.SMEAR_CONSTRUCT)))) {
			throw new RuntimeException("Cannot initialize smear: invalid obs used for initialization.");
		}
		test = smear;
	}
	*/
	
	// create a new smear object, given an existing patient
	/*
	public SmearImpl(Encounter encounter) {
		if (encounter == null) {
			throw new RuntimeException("Cannot create smear: encounter can not be null.");
		}
		// note that we are setting the location null--tests don't immediately inherit
		// the location of the parent encounter
		test = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.SMEAR_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	*/
	
	public SmearImpl(LabTest smear) {
		if (smear == null) {
			throw new RuntimeException("Cannot initialize test: Parameter is null.");
		}
		test = smear;
	}
	
	@Override
	public String getTestType() {
		return "smear";
	}
	
	public Integer getBacilli() {
		LabTestAttribute attribute = CommonLabUtil.getService().getSmearAttributeByTestAndName(test, MdrtbConcepts.BACILLI);
		return (Integer) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BACILLI), test);
		return (obs == null) ? null : obs.getValueNumeric();
		*/
	}
	
	public void setBacilli(Integer bacilli) {
		LabTestAttribute attribute = CommonLabUtil.getService().getSmearAttributeByTestAndName(test, MdrtbConcepts.BACILLI);
		attribute.setValue(bacilli);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BACILLI), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && bacilli == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (bacilli == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BACILLI),
			        test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now set the value
		obs.setValueNumeric(bacilli.doubleValue());
		*/
	}
	
	public String getComments() {
		return test.getResultComments();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT), test);
		return (obs == null) ? null : obs.getValueText();
		*/
	}
	
	public void setComments(String comments) {
		test.setResultComments(comments);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT), test);
		// if this obs has not been created, and there is no data to add, do nothing
		if (obs == null && StringUtils.isBlank(comments)) {
			return;
		}
		// we don't need to test for comments == null here like the other obs because
		// the comments are stored on the results obs
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT),
			        test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		obs.setComment(comments);
		*/
	}
	
	public Concept getMethod() {
		LabTestAttribute attribute = CommonLabUtil.getService().getSmearAttributeByTestAndName(test,
		    MdrtbConcepts.SMEAR_METHOD);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_METHOD), test);
		obs.setValueCoded(method);
		*/
	}
	
	public void setMethod(Concept method) {
		LabTestAttribute attribute = CommonLabUtil.getService().getSmearAttributeByTestAndName(test,
		    MdrtbConcepts.SMEAR_METHOD);
		attribute.setValue(method);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_METHOD), test);
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
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_METHOD),
			        test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now save the value
		obs.setValueCoded(method);
		*/
	}
	
	public Concept getResult() {
		LabTestAttribute attribute = CommonLabUtil.getService().getSmearAttributeByTestAndName(test,
		    MdrtbConcepts.SMEAR_RESULT);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setResult(Concept result) {
		LabTestAttribute attribute = CommonLabUtil.getService().getSmearAttributeByTestAndName(test,
		    MdrtbConcepts.SMEAR_RESULT);
		attribute.setValue(result);
		test.setAttribute(attribute);
		
		/*
		Concept smearResultConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT);
		Obs obs = MdrtbUtil.getObsFromObsGroup(smearResultConcept, test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && result == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (result == null && StringUtils.isBlank(obs.getComment())) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if we need to
		if (obs == null) {
			obs = new Obs(test.getPerson(), smearResultConcept, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now save the data
		obs.setValueCoded(result);
		*/
	}
	
	/**
	 * Utility method for copying a smear
	 */
	public void copyMembersFrom(Test source) {
		super.copyMembersFrom(source);
		this.setBacilli(((Smear) source).getBacilli());
		this.setMethod(((Smear) source).getMethod());
		this.setResult(((Test) source).getResult());
	}
}
