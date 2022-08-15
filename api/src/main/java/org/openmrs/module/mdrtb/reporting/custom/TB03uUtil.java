package org.openmrs.module.mdrtb.reporting.custom;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.DSTForm;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.custom.HAIN;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2Impl;
import org.openmrs.module.mdrtb.specimen.custom.HAINImpl;
import org.openmrs.module.mdrtb.specimen.custom.Xpert;
import org.openmrs.module.mdrtb.specimen.custom.XpertImpl;

public class TB03uUtil {

	
	/*public static Culture getDiagnosticCulture(Patient patient) {
		Culture c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getMonthOfTreatment()!=null && specimen.getMonthOfTreatment()==0) {
				if(specimen.getCultures().size()>0) {
					c = specimen.getCultures().get(0);
					break;
				}
					
			}
		
		}
		
		return c;
	}
	
	public static Xpert getFirstXpert(Patient patient) {
		Xpert c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getXperts()!=null && specimen.getXperts().size() > 0) {
					c = specimen.getXperts().get(0);
					break;
				}
		}
		
		return c;
	}
	
	public static HAIN getFirstHAIN(Patient patient) {
		HAIN c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getHAINs()!=null && specimen.getHAINs().size() > 0) {
				c = specimen.getHAINs().get(0);
				break;
			}
		
		}
		
		return c;
	}
	
	public static Smear getDiagnosticSmear(Patient patient) {
		Smear c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getMonthOfTreatment()!=null && specimen.getMonthOfTreatment()==0) {
				if(specimen.getSmears().size()>0) {
					c = specimen.getSmears().get(0);
					break;
				}
					
			}
		
		}
		
		return c;
	}
	
	public static Smear getFollowupSmear(Patient patient, Integer month) {
		Smear c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getMonthOfTreatment()!=null && specimen.getMonthOfTreatment()==month.intValue()) {
				if(specimen.getSmears().size()>0) {
					c = specimen.getSmears().get(0);
					break;
				}
					
			}
		
		}
		
		return c;
	}
	
	public static Culture getFollowupCulture(Patient patient, Integer month) {
		Culture c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getMonthOfTreatment()!=null && specimen.getMonthOfTreatment()==month.intValue()) {
				if(specimen.getCultures().size()>0) {
					c = specimen.getCultures().get(0);
					break;
				}
					
			}
		
		}
		
		return c;
	}
	
	public static Dst getDiagnosticDST(Patient patient) {
		Dst d = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			
				if(specimen.getDsts().size()>0) {
					d = specimen.getDsts().get(0);
					break;
				}
					
		}
		
		return d;
	}*/
	/*
	public static Culture getDiagnosticCulture(TB03uForm tf) {
		Culture c = null;
		
		for (CultureForm cf : tf.getCultures()) {//, startDateCollected, endDateCollected)) {
			if(cf.getMonthOfTreatment()!=null && cf.getMonthOfTreatment()==0) {
				
					c = new CultureImpl(cf.getEncounter());
					break;
				}
					
			}
		return c;
		
	}*/

	
	/*public static Xpert getFirstXpert(TB03uForm tf) {
		Xpert c = null;
		List<XpertForm> xperts = tf.getXperts();
		 {//, startDateCollected, endDateCollected)) {
			if(xperts!=null && xperts.size() > 0) {
					c = new XpertImpl(xperts.get(0).getEncounter());
					
				}
		}
		
		return c;
	}
	
	public static HAIN getFirstHAIN(TB03uForm tf) {
		HAIN c = null;
		
		List<HAINForm> hains = tf.getHains();
		//, startDateCollected, endDateCollected)) {
			if(hains!=null && hains.size() > 0) {
					c = new HAINImpl(hains.get(0).getEncounter());
					
				}
	
		
		return c;
	}*/
	
//	
	
	/*public static Smear getDiagnosticSmear(TB03uForm form) {
		Smear c = null;
		
		for (SmearForm sf : form.getSmears()) {
			if(sf.getMonthOfTreatment()!=null && sf.getMonthOfTreatment()==0) {
					c = new SmearImpl(sf.getEncounter());
					break;
				}
					
		}
		
		return c;
		
	}*/
	
	public static SmearForm getFollowupSmearForm(TB03uForm form, Integer month) {
		SmearForm c = null;
		
		for (SmearForm sf : form.getSmears()) {//, startDateCollected, endDateCollected)) {
			if(sf.getMonthOfTreatment()!=null && sf.getMonthOfTreatment()==month.intValue()) {
					c = sf;//new SmearImpl(sf.getEncounter());
					break;
				}
					
		}
		return c;
		
	}
	
	/*public static Culture getFollowupCulture(TB03uForm form, Integer month) {
		Culture c = null;
		
		for (CultureForm cf : form.getCultures()) {//, startDateCollected, endDateCollected)) {
			if(cf.getMonthOfTreatment()!=null && cf.getMonthOfTreatment()==month.intValue()) {
					c = new CultureImpl(cf.getEncounter());
					break;
				}
					
		}
		return c;
		
	}*/
	
	public static CultureForm getFollowupCultureForm(TB03uForm form, Integer month) {
		CultureForm c = null;
		
		for (CultureForm cf : form.getCultures()) {//, startDateCollected, endDateCollected)) {
			if(cf.getMonthOfTreatment()!=null && cf.getMonthOfTreatment()==month.intValue()) {
					c = cf;//new CultureImpl(cf.getEncounter());
					break;
				}
					
		}
		return c;
		
	}
		
	
	/*public static Smear getFollowupSmear(TB03uForm form, Integer month) {
		Smear c = null;
		
		for (SmearForm sf : form.getSmears()) {//, startDateCollected, endDateCollected)) {
			if(sf.getMonthOfTreatment()!=null && sf.getMonthOfTreatment()==month.intValue()) {
					c = new SmearImpl(sf.getEncounter());
					break;
				}
					
		}
		return c;
		
	}
	
	public static Culture getFollowupCulture(TB03uForm form, Integer month) {
		Culture c = null;
		
		for (CultureForm cf : form.getCultures()) {//, startDateCollected, endDateCollected)) {
			if(cf.getMonthOfTreatment()!=null && cf.getMonthOfTreatment()==month.intValue()) {
					c = new CultureImpl(cf.getEncounter());
					break;
				}
					
		}
		return c;
		
	}
		*/
	
	public static Dst getDiagnosticDST(TB03uForm tf) {
		Dst d = null;
		
		List<DSTForm> dsts = tf.getDsts();
		
		if(dsts!=null && dsts.size()>0) {
			d = new DstImpl(dsts.get(0).getEncounter());
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
		dateCal.set(Calendar.HOUR,0);
		dateCal.set(Calendar.MINUTE, 0);
		dateCal.set(Calendar.SECOND, 0);
		dateCal.set(Calendar.MILLISECOND, 1);
		
		GregorianCalendar testCal = new GregorianCalendar();
		testCal.setTime(min.getDateCollected());
		testCal.set(Calendar.HOUR,0);
		testCal.set(Calendar.MINUTE, 0);
		testCal.set(Calendar.SECOND, 0);
		testCal.set(Calendar.MILLISECOND, 1);
		
		diffInTime = dateCal.getTimeInMillis()-testCal.getTimeInMillis();
		
		minDiff = Math.abs(diffInTime);
		
		for(int i=1; i<tests.size(); i++) {
			testCal.setTime(tests.get(i).getDateCollected());
			testCal.set(Calendar.HOUR,0);
			testCal.set(Calendar.MINUTE, 0);
			testCal.set(Calendar.SECOND, 0);
			testCal.set(Calendar.MILLISECOND, 1);
			
			diffInTime = dateCal.getTimeInMillis()-testCal.getTimeInMillis();
			
			diffInTime = Math.abs(diffInTime);
			
			if(diffInTime < minDiff) {
				minDiff = diffInTime;
				min = tests.get(i);
			}
			
			
		}
		
		return min;
		
	}
	
	public static Xpert getFirstXpert(TB03uForm tf) {
		Xpert c = null;
		List<XpertForm> xperts = tf.getXperts();
		 {//, startDateCollected, endDateCollected)) {
			if(xperts!=null && xperts.size() > 0) {
					c = new XpertImpl(xperts.get(0).getEncounter());
					
				}
		}
		
		return c;
	}
	
	public static XpertForm getFirstXpertForm(TB03uForm tf) {
		XpertForm c = null;
		List<XpertForm> xperts = tf.getXperts();
		 {//, startDateCollected, endDateCollected)) {
			if(xperts!=null && xperts.size() > 0) {
					c = xperts.get(0);
					
				}
		}
		
		return c;
	}
	
	public static HAIN getFirstHAIN(TB03uForm tf) {
		HAIN c = null;
		
		List<HAINForm> hains = tf.getHains();
		//, startDateCollected, endDateCollected)) {
			if(hains!=null && hains.size() > 0) {
					c = new HAINImpl(hains.get(0).getEncounter());
					
				}
	
		
		return c;
	}
	
	public static HAINForm getFirstHAINForm(TB03uForm tf) {
		HAINForm c = null;
		
		List<HAINForm> hains = tf.getHains();
		//, startDateCollected, endDateCollected)) {
			if(hains!=null && hains.size() > 0) {
					c = hains.get(0);// new HAINImpl(hains.get(0).getEncounter());
					
				}
	
		
		return c;
	}
	
	public static HAIN2 getFirstHAIN2(TB03uForm tf) {
		HAIN2 c = null;
		
		List<HAIN2Form> hains = tf.getHain2s();
		//, startDateCollected, endDateCollected)) {
			if(hains!=null && hains.size() > 0) {
					c = new HAIN2Impl(hains.get(0).getEncounter());
					
				}
	
		
		return c;
	}
	
	public static HAIN2Form getFirstHAIN2Form(TB03uForm tf) {
		HAIN2Form c = null;
		
		List<HAIN2Form> hains = tf.getHain2s();
		//, startDateCollected, endDateCollected)) {
			if(hains!=null && hains.size() > 0) {
					c = hains.get(0);// new HAINImpl(hains.get(0).getEncounter());
					
				}
	
		
		return c;
	}
	
	
	
}
