package org.openmrs.module.mdrtb.specimen.custom;

import org.openmrs.Concept;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestAttribute;
import org.openmrs.module.mdrtb.CommonLabUtil;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.specimen.TestImpl;

/**
 * An implementaton of a MdrtbSmear. This wraps an ObsGroup and provides access to smear data within
 * the obsgroup.
 */
public class HAIN2Impl extends TestImpl implements HAIN2 {
	
	public HAIN2Impl() {
		// test = new Obs(null, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN2_CONSTRUCT), null, null);
	}
	
	// set up a xpert object, given an existing obs
	/*
	public HAIN2Impl(Obs hain) {
		if (hain == null
		        || !(hain.getConcept().equals(Context.getService(MdrtbService.class).getConcept(
		            MdrtbConcepts.HAIN2_CONSTRUCT)))) {
			throw new RuntimeException("Cannot initialize xpert: invalid obs used for initialization.");
		}
		test = hain;
	}
	*/
	
	// create a new smear object, given an existing patient
	/*
	public HAIN2Impl(Encounter encounter) {
		if (encounter == null) {
			throw new RuntimeException("Cannot create hain: encounter can not be null.");
		}
		// note that we are setting the location null--tests don't immediately inherit the location of the parent encounter
		test = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.HAIN2_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	*/
	
	public HAIN2Impl(LabTest hain2) {
		if (hain2 == null) {
			throw new RuntimeException("Cannot initialize test: Parameter is null.");
		}
		test = hain2;
	}
	
	@Override
	public String getTestType() {
		return "hain";
	}
	
	public String getComments() {
		return test.getResultComments();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
		return (obs == null) ? null : obs.getComment();
		*/
	}
	
	public void setComments(String comments) {
		test.setResultComments(comments);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
		if (obs == null && StringUtils.isBlank(comments)) {
			return;
		}
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT),
			        test.getObsDatetime(), test.getOrder().getEncounter().getLocation());
			obs.setEncounter(test.getOrder().getEncounter());
			test.addGroupMember(obs);
		}
		obs.setComment(comments);
		*/
	}
	
	public Concept getResult() {
		LabTestAttribute attribute = CommonLabUtil.getService().getHain2AttributeByTestAndName(test,
		    MdrtbConcepts.MTB_RESULT);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setResult(Concept mtbResult) {
		LabTestAttribute attribute = CommonLabUtil.getService().getHain2AttributeByTestAndName(test,
		    MdrtbConcepts.MTB_RESULT);
		attribute.setValue(mtbResult);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && mtbResult == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (mtbResult == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT),
			        test.getObsDatetime(), test.getOrder().getEncounter().getLocation());
			obs.setEncounter(test.getOrder().getEncounter());
			test.addGroupMember(obs);
		}
		// now save the value
		obs.setValueCoded(mtbResult);
		*/
	}
	
	public Concept getInhResistance() {
		LabTestAttribute attribute = CommonLabUtil.getService().getHain2AttributeByTestAndName(test,
		    MdrtbConcepts.ISONIAZID_RESULT);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ISONIAZID_RESULT), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setInhResistance(Concept inhResistance) {
		LabTestAttribute attribute = CommonLabUtil.getService().getHain2AttributeByTestAndName(test,
		    MdrtbConcepts.ISONIAZID_RESULT);
		attribute.setValue(inhResistance);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ISONIAZID_RESULT), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && inhResistance == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (inhResistance == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.ISONIAZID_RESULT), test.getObsDatetime(), test.getOrder().getEncounter().getLocation());
			obs.setEncounter(test.getOrder().getEncounter());
			test.addGroupMember(obs);
		}
		// now save the value
		obs.setValueCoded(inhResistance);
		*/
	}
	
	public Concept getRifResistance() {
		LabTestAttribute attribute = CommonLabUtil.getService().getHain2AttributeByTestAndName(test,
		    MdrtbConcepts.RIFAMPICIN_RESULT);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN_RESULT), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setRifResistance(Concept rifResistance) {
		LabTestAttribute attribute = CommonLabUtil.getService().getHain2AttributeByTestAndName(test,
		    MdrtbConcepts.RIFAMPICIN_RESULT);
		attribute.setValue(rifResistance);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN_RESULT), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && rifResistance == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (rifResistance == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.RIFAMPICIN_RESULT), test.getObsDatetime(), test.getOrder().getEncounter().getLocation());
			obs.setEncounter(test.getOrder().getEncounter());
			test.addGroupMember(obs);
		}
		// now save the value
		obs.setValueCoded(rifResistance);
		*/
	}
	
	public Concept getMtbBurden() {
		LabTestAttribute attribute = CommonLabUtil.getService().getHain2AttributeByTestAndName(test,
		    MdrtbConcepts.XPERT_MTB_BURDEN);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_MTB_BURDEN), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setMtbBurden(Concept mtbBurden) {
		LabTestAttribute attribute = CommonLabUtil.getService().getHain2AttributeByTestAndName(test,
		    MdrtbConcepts.XPERT_MTB_BURDEN);
		attribute.setValue(mtbBurden);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_MTB_BURDEN), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && mtbBurden == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (mtbBurden == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class)
			        .getConcept(MdrtbConcepts.XPERT_MTB_BURDEN), test.getObsDatetime(), test.getOrder().getEncounter().getLocation());
			obs.setEncounter(test.getOrder().getEncounter());
			test.addGroupMember(obs);
		}
		// now save the value
		obs.setValueCoded(mtbBurden);
		*/
	}
	
	public Concept getMethod() {
		LabTestAttribute attribute = CommonLabUtil.getService().getHain2AttributeByTestAndName(test,
		    MdrtbConcepts.HAIN2_CONSTRUCT);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setMethod(Concept method) {
		LabTestAttribute attribute = CommonLabUtil.getService().getHain2AttributeByTestAndName(test,
		    MdrtbConcepts.HAIN2_CONSTRUCT);
		attribute.setValue(method);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN2_CONSTRUCT), test);
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
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HAIN_CONSTRUCT),
			        test.getObsDatetime(), test.getOrder().getEncounter().getLocation());
			obs.setEncounter(test.getOrder().getEncounter());
			test.addGroupMember(obs);
		}
		// now save the value
		obs.setValueCoded(method);
		*/
	}
}
