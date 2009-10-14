package org.openmrs.module.mdrtb.allergy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;

public class MdrtbAllergyUtils {

    protected final Log log = LogFactory.getLog(getClass());
        
    public static Map<Patient, List<MdrtbAllergyStringObj>> getPatientAllergies(List<Patient> pList, MdrtbFactory mu){
        
    
        Map<Patient, List<MdrtbAllergyStringObj>> ret = new HashMap<Patient, List<MdrtbAllergyStringObj>>();
        List<Concept> cList = new ArrayList<Concept>();
        cList.add(mu.getConceptAdverseEffectConstruct());
        List<Person> persList = new ArrayList<Person>();
        for (Patient p: pList)
            persList.add(p);
        List<Obs> oList = Context.getObsService().getObservations(persList, null, cList, null, null, null, null, null, null, null, null, false);
        Concept effect = mu.getConceptAdverseEffect();
        Concept effectNC = mu.getConceptAdverseEffectNonCoded();
        Concept medication = mu.getConceptAdverseEffectMedication();
        Concept medicationNC = mu.getConceptAdverseEffectMedicationNonCoded();
        Concept actionTaken = mu.getConceptAdverseEffectActionTaken();
        Concept adverseEffectDate = mu.getConceptAdverseEffectDate();
        
        for (Patient p : pList){
           ArrayList<MdrtbAllergyStringObj> allergyList = new ArrayList<MdrtbAllergyStringObj>(); 
           for (Obs oParent :  oList){     
               if (oParent.getPersonId().equals(p.getPatientId())){
                   MdrtbAllergyStringObj maso = new MdrtbAllergyStringObj();
                   for (Obs o : oParent.getGroupMembers()){
                       if (o.getConcept().getConceptId().equals(effectNC.getConceptId())){
                           maso.setEffect(o.getValueText());
                       } else if (o.getConcept().getConceptId().equals(effect.getConceptId()) && o.getValueCoded() != null){
                           maso.setEffect(o.getValueCoded().getBestShortName(Context.getLocale()).getName());
                       } else if (o.getConcept().getConceptId().equals(medicationNC.getConceptId())){
                           maso.setMedication(o.getValueText());
                       } else if (o.getConcept().getConceptId().equals(medication.getConceptId()) && o.getValueCoded() != null){
                           maso.setMedication(o.getValueCoded().getBestShortName(Context.getLocale()).getName());
                       } else if (o.getConcept().getConceptId().equals(actionTaken.getConceptId())){
                           maso.setSupportingTreatment(o.getValueText());
                       } else if (o.getConcept().getConceptId().equals(adverseEffectDate.getConceptId()) && o.getValueDatetime() != null){
                           maso.setDate(o.getValueDatetime());
                       } else if (o.getConcept().getConceptId().equals(adverseEffectDate.getConceptId()) && o.getValueDatetime() == null){
                           maso.setDate(o.getObsDatetime());
                       }
                   }
                   allergyList.add(maso);
               }
           }
           
           Collections.sort(allergyList, new Comparator<MdrtbAllergyStringObj>() {
               public int compare(MdrtbAllergyStringObj left, MdrtbAllergyStringObj right) {
                   return (left.getDate()).compareTo(right.getDate());
               }
               
           });
           
           ret.put(p, allergyList);
        }
        
        return ret;
    }
      
}