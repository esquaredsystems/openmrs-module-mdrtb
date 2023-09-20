package org.openmrs.module.mdrtb.reporting.custom;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.commonlabtest.LabTest;
import org.openmrs.module.commonlabtest.LabTestType;
import org.openmrs.module.mdrtb.CommonLabUtil;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.DSTForm;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.CultureImpl;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.SmearImpl;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.custom.HAIN;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2Impl;
import org.openmrs.module.mdrtb.specimen.custom.HAINImpl;
import org.openmrs.module.mdrtb.specimen.custom.Xpert;
import org.openmrs.module.mdrtb.specimen.custom.XpertImpl;

public class TB03Util {
	
	public static Culture getDiagnosticCulture(TB03Form tf) {
		Culture c = null;
		for (CultureForm cf : tf.getCultures()) {//, startDateCollected, endDateCollected)) {
			if (cf.getMonthOfTreatment() != null && cf.getMonthOfTreatment() == 0) {
				LabTestType labTestType = CommonLabUtil.getService().getMdrtbTestType();
				LabTest culture = CommonLabUtil.getService().getMdrtbLabTestOrder(cf.getEncounter(), labTestType);
				c = new CultureImpl(culture);
				// c = new CultureImpl(cf.getEncounter());
				break;
			}
		}
		return c;
	}
	
	public static CultureForm getDiagnosticCultureForm(TB03Form tf) {
		CultureForm c = null;
		for (CultureForm cf : tf.getCultures()) {//, startDateCollected, endDateCollected)) {
			if (cf.getMonthOfTreatment() != null && cf.getMonthOfTreatment() == 0) {
				c = cf;
				break;
			}
		}
		return c;
	}
	
	public static Xpert getFirstXpert(TB03Form tf) {
		Xpert c = null;
		List<XpertForm> xperts = tf.getXperts();
		if (xperts != null && xperts.size() > 0) {
			LabTestType labTestType = CommonLabUtil.getService().getMdrtbTestType();
			LabTest xpert = CommonLabUtil.getService().getMdrtbLabTestOrder(xperts.get(0).getEncounter(), labTestType);
			c = new XpertImpl(xpert);
			// c = new XpertImpl(xperts.get(0).getEncounter());
		}
		return c;
	}
	
	public static XpertForm getFirstXpertForm(TB03Form tf) {
		XpertForm c = null;
		List<XpertForm> xperts = tf.getXperts();
		if (xperts != null && xperts.size() > 0) {
			c = xperts.get(0);
		}
		return c;
	}
	
	public static HAIN getFirstHAIN(TB03Form tf) {
		HAIN c = null;
		List<HAINForm> hains = tf.getHains();
		if (hains != null && hains.size() > 0) {
			LabTestType labTestType = CommonLabUtil.getService().getMdrtbTestType();
			LabTest hain = CommonLabUtil.getService().getMdrtbLabTestOrder(hains.get(0).getEncounter(), labTestType);
			c = new HAINImpl(hain);
			//c = new HAINImpl(hains.get(0).getEncounter());
		}
		return c;
	}
	
	public static HAINForm getFirstHAINForm(TB03Form tf) {
		HAINForm c = null;
		List<HAINForm> hains = tf.getHains();
		if (hains != null && hains.size() > 0) {
			c = hains.get(0);// new HAINImpl(hains.get(0).getEncounter());
		}
		return c;
	}
	
	public static HAIN2 getFirstHAIN2(TB03Form tf) {
		HAIN2 c = null;
		List<HAIN2Form> hains = tf.getHain2s();
		if (hains != null && hains.size() > 0) {
			LabTestType labTestType = CommonLabUtil.getService().getMdrtbTestType();
			LabTest hain = CommonLabUtil.getService().getMdrtbLabTestOrder(hains.get(0).getEncounter(), labTestType);
			c = new HAIN2Impl(hain);
			// c = new HAIN2Impl(hains.get(0).getEncounter());
		}
		return c;
	}
	
	public static HAIN2Form getFirstHAIN2Form(TB03Form tf) {
		HAIN2Form c = null;
		List<HAIN2Form> hains = tf.getHain2s();
		//, startDateCollected, endDateCollected)) {
		if (hains != null && hains.size() > 0) {
			c = hains.get(0);// new HAINImpl(hains.get(0).getEncounter());
		}
		return c;
	}
	
	public static Smear getDiagnosticSmear(TB03Form form) {
		Smear c = null;
		for (SmearForm sf : form.getSmears()) {
			if (sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
				LabTestType labTestType = CommonLabUtil.getService().getMdrtbTestType();
				LabTest smear = CommonLabUtil.getService().getMdrtbLabTestOrder(sf.getEncounter(), labTestType);
				c = new SmearImpl(smear);
				//c = new SmearImpl(sf.getEncounter());
				c.setResult(sf.getSmearResult());
				c.setResultDate(sf.getEncounterDatetime());
				break;
			}
		}
		return c;
	}
	
	public static SmearForm getDiagnosticSmearForm(TB03Form form) {
		SmearForm c = null;
		for (SmearForm sf : form.getSmears()) {
			if (sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
				c = sf;
				break;
			}
		}
		return c;
	}
	
	public static Smear getFollowupSmear(TB03Form form, Integer month) {
		Smear c = null;
		for (SmearForm sf : form.getSmears()) {
			if (sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == month.intValue()) {
				LabTestType labTestType = CommonLabUtil.getService().getMdrtbTestType();
				LabTest smear = CommonLabUtil.getService().getMdrtbLabTestOrder(sf.getEncounter(), labTestType);
				c = new SmearImpl(smear);
				// c = new SmearImpl(sf.getEncounter());
				break;
			}
		}
		return c;
	}
	
	public static SmearForm getFollowupSmearForm(TB03Form form, Integer month) {
		SmearForm c = null;
		for (SmearForm sf : form.getSmears()) {
			if (sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == month.intValue()) {
				c = sf;
				break;
			}
		}
		return c;
	}
	
	public static Dst getDiagnosticDST(TB03Form tf) {
		Dst d = null;
		List<DSTForm> dsts = tf.getDsts();
		if (dsts != null && dsts.size() > 0) {
			LabTest dst = CommonLabUtil.getService().getDstLabTestOrder(dsts.get(0).getEncounter());
			d = new DstImpl(dst);
			// d = new DstImpl(dsts.get(0).getEncounter());
		}
		return d;
	}
	
	//find out which date and before or after
	//also to use sample collection date of test or other date?
	public static Specimen getClosestTestTo(List<Specimen> tests, Date date) {
		
		Specimen min = tests.get(0);
		long minDiff = 0;
		long diffInTime = 0;
		GregorianCalendar dateCal = new GregorianCalendar();
		dateCal.setTime(date);
		dateCal.set(Calendar.HOUR, 0);
		dateCal.set(Calendar.MINUTE, 0);
		dateCal.set(Calendar.SECOND, 0);
		dateCal.set(Calendar.MILLISECOND, 1);
		
		GregorianCalendar testCal = new GregorianCalendar();
		testCal.setTime(min.getDateCollected());
		testCal.set(Calendar.HOUR, 0);
		testCal.set(Calendar.MINUTE, 0);
		testCal.set(Calendar.SECOND, 0);
		testCal.set(Calendar.MILLISECOND, 1);
		
		diffInTime = dateCal.getTimeInMillis() - testCal.getTimeInMillis();
		
		minDiff = Math.abs(diffInTime);
		
		for (int i = 1; i < tests.size(); i++) {
			testCal.setTime(tests.get(i).getDateCollected());
			testCal.set(Calendar.HOUR, 0);
			testCal.set(Calendar.MINUTE, 0);
			testCal.set(Calendar.SECOND, 0);
			testCal.set(Calendar.MILLISECOND, 1);
			
			diffInTime = dateCal.getTimeInMillis() - testCal.getTimeInMillis();
			
			diffInTime = Math.abs(diffInTime);
			
			if (diffInTime < minDiff) {
				minDiff = diffInTime;
				min = tests.get(i);
			}
			
		}
		
		return min;
		
	}
	
	public static String getRegistrationNumber(TB03Form form) {
		if (form == null) {
			return Context.getMessageSourceService().getMessage("mdrtb.unassigned");
			
		}
		String val = "";
		PatientIdentifier pi = null;
		Integer ppid = null;
		Concept ppidConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		Obs idObs = MdrtbUtil.getObsFromEncounter(ppidConcept, form.getEncounter());
		if (idObs == null) {
			val = null;
		} else {
			ppid = idObs.getValueNumeric().intValue();
			PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(ppid);
			
			if (pp != null) {
				pi = Context.getService(MdrtbService.class).getPatientProgramIdentifier(pp);
				if (pi == null) {
					val = null;
				} else {
					val = pi.getIdentifier();
				}
			} else {
				val = null;
			}
		}
		if (val == null || val.length() == 0) {
			val = Context.getMessageSourceService().getMessage("mdrtb.unassigned");
		}
		return val;
	}
	
	public static String getRegistrationNumber(TB03uForm form) {
		String val = "";
		PatientIdentifier pi = null;
		Integer ppid = null;
		Concept ppidConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		Obs idObs = MdrtbUtil.getObsFromEncounter(ppidConcept, form.getEncounter());
		if (idObs == null) {
			val = null;
		}
		
		else {
			ppid = idObs.getValueNumeric().intValue();
			PatientProgram pp = Context.getProgramWorkflowService().getPatientProgram(ppid);
			if (pp != null) {
				pi = Context.getService(MdrtbService.class).getPatientProgramIdentifier(pp);
				if (pi == null) {
					val = null;
				} else {
					val = pi.getIdentifier();
				}
			} else {
				val = null;
			}
		}
		if (val == null || val.length() == 0) {
			val = Context.getMessageSourceService().getMessage("mdrtb.unassigned");
		}
		return val;
	}
}
