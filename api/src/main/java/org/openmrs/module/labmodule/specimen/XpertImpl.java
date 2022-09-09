package org.openmrs.module.labmodule.specimen;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;

/**
 * An implementaton of a MdrtbSmear.  This wraps an ObsGroup and provides access to smear
 * data within the obsgroup.
 */
public class XpertImpl extends TestImpl implements Xpert {
	
	public XpertImpl() {
	}

	// set up a xpert object, given an existing obs
	public XpertImpl(Obs xpert) {
		
		if(xpert == null || !(xpert.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT)))) {
			throw new RuntimeException ("Cannot initialize xpert: invalid obs used for initialization.");
		}
		
		test = xpert;
	}
	
	// create a new smear object, given an existing patient
	public XpertImpl(Encounter encounter) {
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create xpert: encounter can not be null.");
		}
		
		// note that we are setting the location null--tests don't immediately inherit the location of the parent encounter
		test = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "xpert";
	}
	
    
    
    public String getComments() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getComment();
    	}
    }
      
   

    public void setComments(String comments) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test);
    	
    	// if this obs has not been created, and there is no data to add, do nothing
    	if (obs == null && StringUtils.isBlank(comments)) {
    		return;
    	}
    	
    	// we don't need to test for comments == null here like the other obs because
    	// the comments are stored on the results obs
    	
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		obs.setComment(comments);
    }
   

	@Override
	public Concept getResult() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResult(Concept mtbResult) {
		
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test);
    	
   	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && mtbResult == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(mtbResult == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(mtbResult);
	}

	@Override
	public Concept getRifResistance() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setRifResistance(Concept rifResistance) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE), test);
    	
   	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && rifResistance == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(rifResistance == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.RIFAMPICIN_RESISTANCE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(rifResistance);
		
	}

	@Override
	public String getErrorCode() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.ERROR_CODE), test);
    	
    	if (obs == null || obs.getValueText() == null) {
    		return null;
    	}
    	else {
    		return obs.getValueText();
    	}
	}

	@Override
	public void setErrorCode(String code) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.ERROR_CODE), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && code == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(code == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.ERROR_CODE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueText(code);
		
	}
	
	@Override
	public Concept getMtbBurden() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.XPERT_MTB_BURDEN), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setMtbBurden(Concept mtbBurden) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.XPERT_MTB_BURDEN), test);
    	
	   	 // if this obs have not been created, and there is no data to add, do nothing
			if (obs == null && mtbBurden == null) {
				return;
			}
			
			// if we are trying to set the obs to null, simply void the obs
			if(mtbBurden == null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
				return;
			}
	   	
			// initialize the obs if needed
			if (obs == null) {		
				obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.XPERT_MTB_BURDEN), test.getObsDatetime(), test.getLocation());
				obs.setEncounter(test.getEncounter());
				test.addGroupMember(obs);
			}
			
			// now save the value
			obs.setValueCoded(mtbBurden);
	}
	
	@Override
	public String getLabSpecialistName() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.LAB_SPECIALIST_NAME), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueText();
    	}
	}

	@Override
	public void setLabSpecialistName(String labName) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.LAB_SPECIALIST_NAME), test);
    	
	   	 // if this obs have not been created, and there is no data to add, do nothing
			if (obs == null && labName == null) {
				return;
			}
			
			// if we are trying to set the obs to null, simply void the obs
			if(labName == null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
				return;
			}
	   	
			// initialize the obs if needed
			if (obs == null) {		
				obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.LAB_SPECIALIST_NAME), test.getObsDatetime(), test.getLocation());
				obs.setEncounter(test.getEncounter());
				test.addGroupMember(obs);
			}
			
			// now save the value
			obs.setValueText(labName);
	}

	@Override
	public Concept getSentToCulture() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_CULTURE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setSentToCulture(Concept cultureSent) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_CULTURE), test);
    	
	   	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && cultureSent == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(cultureSent == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_CULTURE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(cultureSent);
	}

	@Override
	public Concept getSentToDst() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_DST), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}	
   }

	@Override
	public void setSentToDst(Concept dstSent) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_DST), test);
    	
	   	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dstSent == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(dstSent == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_DST), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(dstSent);
	}

	@Override
	public Date getSentToCultureDate() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_CULTURE_DATE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueDatetime();
    	}
	}

	@Override
	public void setSentToCultureDate(Date cultureSentDate) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_CULTURE_DATE), test);
    	
	   	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && cultureSentDate == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(cultureSentDate == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_CULTURE_DATE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueDatetime(cultureSentDate);
	}

	@Override
	public Date getSentToDstDate() {
	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_DST_DATE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueDatetime();
    	}
	}

	@Override
	public void setSentToDstDate(Date dstSentDate) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_DST_DATE), test);
    	
	   	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dstSentDate == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(dstSentDate == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.SENT_TO_DST_DATE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueDatetime(dstSentDate);
	}

	/*public Integer getDistrictId() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DISTRICT), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return Integer.parseInt(obs.getValueText());
    	}
	}

	public void setDistrictId(Integer districtId) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DISTRICT), test);
    	
	   	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && districtId == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(districtId == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DISTRICT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueText(String.valueOf(districtId));
		
	}

	public Integer getOblastId() {
	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.OBLAST), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return Integer.parseInt(obs.getValueText());
    	}
	}

	public void setOblastId(Integer oblastId) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.OBLAST), test);
    	
	   	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && oblastId == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(oblastId == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.OBLAST), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueText(String.valueOf(oblastId));
	}	*/
}

