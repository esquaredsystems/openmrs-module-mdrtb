package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Concept;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestAttribute;
import org.openmrs.module.mdrtb.CommonLabUtil;
import org.openmrs.module.mdrtb.MdrtbConcepts;

/**
 * An implementaton of a MdrtbCulture. This wraps an ObsGroup and provides access to culture data
 * within the obsgroup.
 */
public class CultureImpl extends TestImpl implements Culture {
	
	private static final String CULTURE = "culture";
	
	public CultureImpl() {
	}
	
	// set up a culture object, given an existing obs
	/*
	public CultureImpl(Obs culture) {
		if (culture == null
		        || !(culture.getConcept().equals(Context.getService(MdrtbService.class).getConcept(
		            MdrtbConcepts.CULTURE_CONSTRUCT)))) {
			throw new RuntimeException("Cannot initialize culture: invalid obs used for initialization.");
		}
		test = culture;
	}
	*/
	
	// create a new culture object, given an existing patient
	/*
	public CultureImpl(Encounter encounter) {
		if (encounter == null) {
			throw new RuntimeException("Cannot create culture: encounter can not be null.");
		}
		test = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.CULTURE_CONSTRUCT), encounter.getEncounterDatetime(), null);
		test = new LabTest(order);
	}
	*/
	
	public CultureImpl(LabTest culture) {
		if (culture == null) {
			throw new RuntimeException("Cannot initialize test: Parameter is null.");
		}
		test = culture;
	}
	
	@Override
	public String getTestType() {
		return CULTURE;
	}
	
	public Integer getColonies() {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES);
		return (Integer) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
			Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES), test);
		return (obs == null) ? null : obs.getValueNumeric();
		*/
	}
	
	public void setColonies(Integer colonies) {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES);
		attribute.setValue(colonies);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES),
		    test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && colonies == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (colonies == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES),
			        test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now set the value
		obs.setValueNumeric(colonies.doubleValue());
		*/
	}
	
	public String getComments() {
		return test.getResultComments();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT), test);
		return (obs == null) ? null : obs.getValueText();
		*/
	}
	
	public void setComments(String comments) {
		test.setResultComments(comments);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT), test);
		// if this obs has not been created, and there is no data to add, do nothing
		if (obs == null && StringUtils.isBlank(comments)) {
			return;
		}
		// we don't need to test for comments == null here like the other obs because
		// the comments are stored on the results obs
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT),
			        test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		obs.setComment(comments);
		*/
	}
	
	public Integer getDaysToPositivity() {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.DAYS_TO_POSITIVITY);
		return (Integer) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DAYS_TO_POSITIVITY), test);
		return (obs == null) ? null : obs.getValueNumeric();
		*/
	}
	
	public void setDaysToPositivity(Integer daysToPositivity) {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.DAYS_TO_POSITIVITY);
		attribute.setValue(daysToPositivity);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DAYS_TO_POSITIVITY), test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && daysToPositivity == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (daysToPositivity == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if needed
		if (obs == null) {
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(
			    MdrtbConcepts.DAYS_TO_POSITIVITY), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now set the value
		obs.setValueNumeric(daysToPositivity.doubleValue());
		*/
	}
	
	public Concept getOrganismType() {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setOrganismType(Concept organismType) {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM);
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
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED);
		return (String) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
			Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED), test);
		return (obs == null) ? null : obs.getValueText();
		*/
	}
	
	public void setOrganismTypeNonCoded(String organismType) {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED);
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
	
	public Concept getMethod() {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.COLONIES);
		return (Concept) attribute.getValue();
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_METHOD), test);
		return (obs == null) ? null : obs.getValueCoded();
		*/
	}
	
	public void setMethod(Concept method) {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.CULTURE_METHOD);
		attribute.setValue(method);
		test.setAttribute(attribute);
		/*
		Obs obs = MdrtbUtil.getObsFromObsGroup(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_METHOD), test);
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
			obs = new Obs(test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_METHOD),
			        test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now save the value
		obs.setValueCoded(method);
		*/
	}
	
	public Concept getResult() {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.CULTURE_RESULT);
		return (Concept) attribute.getValue();
		/*
		Concept cultureResultConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT);
		Obs obs = MdrtbUtil.getObsFromObsGroup(cultureResultConcept, test);
		return (obs == null) ? null : return obs.getValueCoded();
		*/
	}
	
	public void setResult(Concept result) {
		LabTestAttribute attribute = CommonLabUtil.getService().getCultureAttributeByTestAndName(test,
		    MdrtbConcepts.CULTURE_RESULT);
		attribute.setValue(result);
		test.setAttribute(attribute);
		/*
		Concept cultureResultConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT);
		Obs obs = MdrtbUtil.getObsFromObsGroup(cultureResultConcept, test);
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && result == null) {
			return;
		}
		// if we are trying to set the obs to null, simply void the obs
		if (result == null && StringUtils.isBlank(obs.getComment())) { // we also need to make sure that there is no
			                                                           // comment on the obs in this case
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		// initialize the obs if we need to
		if (obs == null) {
			obs = new Obs(test.getPerson(), cultureResultConcept, test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		// now save the data
		obs.setValueCoded(result);
		*/
	}
	
	/**
	 * Utility method for copying a culture
	 */
	public void copyMembersFrom(Test source) {
		super.copyMembersFrom(source);
		this.setColonies(((Culture) source).getColonies());
		this.setMethod(((Culture) source).getMethod());
		this.setOrganismType(((Culture) source).getOrganismType());
		this.setOrganismTypeNonCoded(((Culture) source).getOrganismTypeNonCoded());
		this.setDaysToPositivity(((Culture) source).getDaysToPositivity());
		this.setResult(((Test) source).getResult());
	}
}
