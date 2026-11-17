package org.openmrs.module.mdrtb.reporting.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbTestBase;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.EndOfTreatmentForm;

/**
 * Tests for {@link EndOfTreatmentData#fillEndOfTreatmentColumns(EndOfTreatmentForm)}: the End of
 * Treatment columns of one End of Treatment report row. The form is not saved, and has no patient
 * program, so it has no follow-up lab tests.
 */
public class EndOfTreatmentDataColumnsTest extends MdrtbTestBase {
	
	private EndOfTreatmentForm form;
	
	private MdrtbService service;
	
	private SimpleDateFormat sdf;
	
	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
		service = Context.getService(MdrtbService.class);
		sdf = Context.getDateFormat();
		Encounter encounter = new Encounter();
		encounter.setPatient(hermione);
		encounter.setLocation(hogwarts);
		encounter.setEncounterDatetime(date("2023-09-01"));
		form = new EndOfTreatmentForm(encounter);
	}
	
	private static Date date(String yyyyMMdd) throws Exception {
		return new SimpleDateFormat("yyyy-MM-dd").parse(yyyyMMdd);
	}
	
	private Concept concept(String uuid) {
		Concept concept = service.getConcept(uuid);
		assertNotNull("concept missing from the test datasets: " + uuid, concept);
		return concept;
	}
	
	private String name(String conceptUuid) {
		return concept(conceptUuid).getName(Context.getLocale()).getName();
	}
	
	@Test
	public void fill_shouldLeaveColumnsEmptyWhenThereIsNoForm() {
		EndOfTreatmentData row = new EndOfTreatmentData();
		row.fillEndOfTreatmentColumns(null);
		assertNull(row.getEndOfTreatmentForm());
		assertNull(row.getDispensaryGroup());
		assertTrue(row.getFollowupXrays().isEmpty());
		assertTrue(row.getFollowupLabTests().isEmpty());
		assertNull(row.getTransferToGroup2Date());
		assertNull(row.getTransferToGroup3Date());
		assertNull(row.getTransferToGroup3Reason());
		assertNull(row.getRemovalFromObservationDate());
		assertNull(row.getRemovalFromObservationReason());
	}
	
	@Test
	public void fill_shouldLeaveColumnsEmptyWhenTheFormIsEmpty() {
		EndOfTreatmentData row = new EndOfTreatmentData();
		row.fillEndOfTreatmentColumns(form);
		assertSame(form, row.getEndOfTreatmentForm());
		assertNull(row.getDispensaryGroup());
		assertTrue(row.getFollowupXrays().isEmpty());
		assertTrue(row.getFollowupLabTests().isEmpty());
		assertNull(row.getTransferToGroup2Date());
		assertNull(row.getTransferToGroup3Date());
		assertNull(row.getTransferToGroup3Reason());
		assertNull(row.getRemovalFromObservationDate());
		assertNull(row.getRemovalFromObservationReason());
	}
	
	@Test
	public void fill_shouldCopyEveryColumnOfTheForm() throws Exception {
		form.setDispensaryGroup(concept(MdrtbConcepts.DISPENSARY_GROUP_IB));
		form.setTransferToGroup2Date(date("2023-10-01"));
		form.setTransferToGroup3Date(date("2024-04-01"));
		form.setTransferToGroup3Reason(concept(MdrtbConcepts.TB_HIV));
		form.setRemovalFromObservationDate(date("2025-04-01"));
		form.setRemovalFromObservationReason("Moved abroad");
		
		EndOfTreatmentData row = new EndOfTreatmentData();
		row.fillEndOfTreatmentColumns(form);
		
		assertEquals(name(MdrtbConcepts.DISPENSARY_GROUP_IB), row.getDispensaryGroup());
		assertEquals(sdf.format(date("2023-10-01")), row.getTransferToGroup2Date());
		assertEquals(sdf.format(date("2024-04-01")), row.getTransferToGroup3Date());
		assertEquals(name(MdrtbConcepts.TB_HIV), row.getTransferToGroup3Reason());
		assertEquals(sdf.format(date("2025-04-01")), row.getRemovalFromObservationDate());
		assertEquals("Moved abroad", row.getRemovalFromObservationReason());
	}
	
	@Test
	public void fill_shouldListAllXraysOrderedByMonth() throws Exception {
		form.setFollowupXray(12, date("2024-08-01"), concept(MdrtbConcepts.XRAY_ABNORMAL));
		form.setFollowupXray(6, date("2024-02-01"), concept(MdrtbConcepts.XRAY_NORMAL));
		form.setFollowupXray(18, date("2025-02-01"), null);
		
		EndOfTreatmentData row = new EndOfTreatmentData();
		row.fillEndOfTreatmentColumns(form);
		
		List<Map<String, Object>> xrays = row.getFollowupXrays();
		assertEquals(3, xrays.size());
		assertEquals(6, xrays.get(0).get("monthOfTreatment"));
		assertEquals(sdf.format(date("2024-02-01")), xrays.get(0).get("date"));
		assertEquals(name(MdrtbConcepts.XRAY_NORMAL), xrays.get(0).get("result"));
		assertEquals(12, xrays.get(1).get("monthOfTreatment"));
		assertEquals(sdf.format(date("2024-08-01")), xrays.get(1).get("date"));
		assertEquals(name(MdrtbConcepts.XRAY_ABNORMAL), xrays.get(1).get("result"));
		assertEquals(18, xrays.get(2).get("monthOfTreatment"));
		assertEquals(sdf.format(date("2025-02-01")), xrays.get(2).get("date"));
		assertNull(xrays.get(2).get("result"));
	}
	
	@Test
	public void fill_shouldListNoLabTestsWhenTheFormHasNoPatientProgram() {
		EndOfTreatmentData row = new EndOfTreatmentData();
		row.fillEndOfTreatmentColumns(form);
		assertTrue(row.getFollowupLabTests().isEmpty());
	}
}
