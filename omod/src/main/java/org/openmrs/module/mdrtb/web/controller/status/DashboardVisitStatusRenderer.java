package org.openmrs.module.mdrtb.web.controller.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.status.StatusItem;
import org.openmrs.module.mdrtb.status.VisitStatus;
import org.openmrs.module.mdrtb.status.VisitStatusRenderer;

public class DashboardVisitStatusRenderer implements VisitStatusRenderer {
	
	public void renderVisit(StatusItem visit, VisitStatus status) {
		
		DateFormat df = new SimpleDateFormat(MdrtbConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
		
		Encounter encounter = (Encounter) visit.getValue();
		
		String[] params = { df.format(encounter.getEncounterDatetime()), encounter.getLocation().getDisplayString() };
		
		visit.setDisplayString(Context.getMessageSourceService().getMessage("mdrtb.visitStatus.visit", params, "{0} at {1}",
		    Context.getLocale()));
		
		// now determine where to link to
		// if there is a form linked to this encounter, assume it is an HTML Form Entry form
		// (note, however, that we exclude specimen collection encounters--they can't have forms linked to them)
		if (encounter.getForm() != null
		        && !encounter.getEncounterType().equals(Context.getEncounterService().getEncounterType(
		            Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")))) {
			visit.setLink("/module/htmlformentry/htmlFormEntry.form?personId=" + encounter.getPatientId() + "&formId="
			        + encounter.getForm().getId() + "&encounterId=" + encounter.getId() + "&mode=VIEW");
		}
		// otherwise, create the link based on the encounter type of the visit
		else {
			
			EncounterType type = encounter.getEncounterType();
			
			/*
			if (type.equals(Context.getEncounterService().getEncounterType(
			    Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type")))) {
				visit.setLink("/module/mdrtb/form/intake.form?patientId="
				        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
				        + status.getPatientProgram().getId() + "&encounterId=" + encounter.getId());
			} else if (type.equals(Context.getEncounterService().getEncounterType(
			    Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type")))) {
				visit.setLink("/module/mdrtb/form/followup.form?patientId="
				        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
				        + status.getPatientProgram().getId() + "&encounterId=" + encounter.getId());
			} else if (type.equals(Context.getEncounterService().getEncounterType(
			    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")))) {
				visit.setLink("/module/mdrtb/specimen/specimen.form?specimenId=" + encounter.getId() + "&patientProgramId="
				        + status.getPatientProgram().getId());
			} else {
				throw new MdrtbAPIException("Invalid encounter type passed to Dashboard visit status renderer.");
			}
			*/
			if (type.equals(Context.getEncounterService().getEncounterType(
			    Context.getAdministrationService().getGlobalProperty("mdrtb.mdrtbIntake_encounter_type")))) {
				
				visit.setLink("/module/mdrtb/form/tb03u.form?patientId="
				        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
				        + status.getPatientProgram().getId() + "&encounterId=" + encounter.getId());
			} else if (type.equals(Context.getEncounterService().getEncounterType("TB03u - XDR"))) {
				visit.setLink("/module/mdrtb/form/tb03u-xdr.form?patientId="
				        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
				        + status.getPatientProgram().getId() + "&encounterId=" + encounter.getId());
			} else if (type.equals(Context.getEncounterService().getEncounterType("Resistance During Treatment"))) {
				visit.setLink("/module/mdrtb/form/resistanceDuringTx.form?patientId="
				        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
				        + status.getPatientProgram().getId() + "&encounterId=" + encounter.getId());
			} else if (type.equals(Context.getEncounterService().getEncounterType(Context.getAdministrationService()
			        .getGlobalProperty("mdrtb.specimen_collection_encounter_type")))) {} else if (type.equals(
			            Context.getEncounterService().getEncounterType(
			                Context.getAdministrationService().getGlobalProperty("mdrtb.transfer_out_encounter_type")))) {
				visit.setLink("/module/mdrtb/form/transferOut.form?patientId="
				        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
				        + status.getPatientProgram().getId() + "&encounterId=" + encounter.getId());
			} else if (type.equals(Context.getEncounterService().getEncounterType(
			    Context.getAdministrationService().getGlobalProperty("mdrtb.transfer_in_encounter_type")))) {
				visit.setLink("/module/mdrtb/form/transferIn.form?patientId="
				        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
				        + status.getPatientProgram().getId() + "&encounterId=" + encounter.getId());
			} else {
				throw new MdrtbAPIException("Invalid encounter type passed to Dashboard visit status renderer.");
			}
		}
	}
	
	public void renderNewIntakeVisit(StatusItem newIntakeVisit, VisitStatus status) {
		
		// we've changed this so that we link to the select form page instead of determining what form to use here
		// newIntakeVisit.setLink("/module/mdrtb/form/select.form?formType=intake&patientId=" + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId=" + status.getPatientProgram().getId());
		newIntakeVisit.setLink("/module/mdrtb/form/tb03u.form?encounterId=-1&formType=intake&patientId="
		        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
		        + status.getPatientProgram().getId());
	}
	
	public void renderNewTbIntakeVisit(StatusItem newIntakeVisit, VisitStatus status) {
		
		// we've changed this so that we link to the select form page instead of
		// determining what form to use here
		newIntakeVisit.setLink("/module/mdrtb/form/tb03.form?formType=intake&encounterId=-1&patientId="
		        + status.getPatientTbProgram().getPatient().getPatientId() + "&patientProgramId="
		        + status.getPatientTbProgram().getId());
	}
	
	public void renderNewFollowUpVisit(StatusItem newFollowUpVisit, VisitStatus status) {
		
		// we've changed this so that we link to the select form page instead of determining what form to use here
		//newFollowUpVisit.setLink("/module/mdrtb/form/select.form?formType=followUp&patientId=" + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId=" + status.getPatientProgram().getId());
		newFollowUpVisit.setLink("/module/mdrtb/form/tb03u-xdr.form?formType=followUp&encounterId=-1&patientId="
		        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
		        + status.getPatientProgram().getId());
	}
	
	public void renderNewTbFollowUpVisit(StatusItem newFollowUpVisit, VisitStatus status) {
		
		// we've changed this so that we link to the select form page instead of
		// determining what form to use here
		newFollowUpVisit.setLink("/module/mdrtb/form/form89.form?formType=followUp&encounterId=-1&patientId="
		        + status.getPatientTbProgram().getPatient().getPatientId() + "&patientProgramId="
		        + status.getPatientTbProgram().getId());
	}
	
	public void renderNewTransferOutVisit(StatusItem newTransferOutVisit, VisitStatus status) {
		
		// we've changed this so that we link to the select form page instead of
		// determining what form to use here
		newTransferOutVisit.setLink("/module/mdrtb/form/transferOut.form?formType=transferOut&encounterId=-1&patientId="
		        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
		        + status.getPatientProgram().getId());
	}
	
	public void renderNewTransferInVisit(StatusItem newTransferInVisit, VisitStatus status) {
		
		// we've changed this so that we link to the select form page instead of
		// determining what form to use here
		newTransferInVisit.setLink("/module/mdrtb/form/transferIn.form?formType=transferIn&encounterId=-1&patientId="
		        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
		        + status.getPatientProgram().getId());
	}
	
	public void renderNewDrdtVisit(StatusItem newDrdtVisit, VisitStatus status) {
		
		// we've changed this so that we link to the select form page instead of
		// determining what form to use here
		newDrdtVisit.setLink("/module/mdrtb/form/resistanceDuringTx.form?formType=drdt&encounterId=-1&patientId="
		        + status.getPatientProgram().getPatient().getPatientId() + "&patientProgramId="
		        + status.getPatientProgram().getId());
		
	}
	
	public void renderNewTbTransferOutVisit(StatusItem newTransferOutVisit, VisitStatus status) {
		
		// we've changed this so that we link to the select form page instead of
		// determining what form to use here
		newTransferOutVisit.setLink("/module/mdrtb/form/transferOut.form?formType=transferOut&encounterId=-1&patientId="
		        + status.getPatientTbProgram().getPatient().getPatientId() + "&patientProgramId="
		        + status.getPatientTbProgram().getId());
	}
	
	public void renderNewTbTransferInVisit(StatusItem newTransferInVisit, VisitStatus status) {
		
		// we've changed this so that we link to the select form page instead of
		// determining what form to use here
		newTransferInVisit.setLink("/module/mdrtb/form/transferIn.form?formType=transferIn&encounterId=-1&patientId="
		        + status.getPatientTbProgram().getPatient().getPatientId() + "&patientProgramId="
		        + status.getPatientTbProgram().getId());
		
	}
	
	public void renderTbVisit(StatusItem visit, VisitStatus status) {
		
		DateFormat df = new SimpleDateFormat(MdrtbConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
		
		Encounter encounter = (Encounter) visit.getValue();
		
		String[] params = { df.format(encounter.getEncounterDatetime()), encounter.getLocation().getDisplayString() };
		
		visit.setDisplayString(Context.getMessageSourceService().getMessage("mdrtb.visitStatus.visit", params, "{0} at {1}",
		    Context.getLocale()));
		
		// now determine where to link to
		// if there is a form linked to this encounter, assume it is an HTML Form Entry
		// form
		// (note, however, that we exclude specimen collection encounters--they can't
		// have forms linked to them)
		EncounterType type = encounter.getEncounterType();
		
		if (type.equals(Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type")))) {
			
			visit.setLink(
			    "/module/mdrtb/form/tb03.form?patientId=" + status.getPatientTbProgram().getPatient().getPatientId()
			            + "&patientProgramId=" + status.getPatientTbProgram().getId() + "&encounterId=" + encounter.getId());
		}
		
		else if (type.equals(Context.getEncounterService()
		        .getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type")))) {
			visit.setLink(
			    "/module/mdrtb/form/form89.form?patientId=" + status.getPatientTbProgram().getPatient().getPatientId()
			            + "&patientProgramId=" + status.getPatientTbProgram().getId() + "&encounterId=" + encounter.getId());
		} else if (type.equals(Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")))) {
			/*
			 * visit.setLink("/module/mdrtb/specimen/specimen.form?specimenId=" +
			 * encounter.getId() + "&patientProgramId=" +
			 * status.getPatientTbProgram().getId());
			 */
		}
		
		else if (type.equals(Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.transfer_out_encounter_type")))) {
			visit.setLink(
			    "/module/mdrtb/form/transferOut.form?patientId=" + status.getPatientTbProgram().getPatient().getPatientId()
			            + "&patientProgramId=" + status.getPatientTbProgram().getId() + "&encounterId=" + encounter.getId());
		}
		
		else if (type.equals(Context.getEncounterService().getEncounterType(
		    Context.getAdministrationService().getGlobalProperty("mdrtb.transfer_in_encounter_type")))) {
			visit.setLink(
			    "/module/mdrtb/form/transferIn.form?patientId=" + status.getPatientTbProgram().getPatient().getPatientId()
			            + "&patientProgramId=" + status.getPatientTbProgram().getId() + "&encounterId=" + encounter.getId());
		}
		
		else {
			throw new MdrtbAPIException("Invalid encounter type passed to Dashboard visit status renderer.");
		}
		
	}
}
