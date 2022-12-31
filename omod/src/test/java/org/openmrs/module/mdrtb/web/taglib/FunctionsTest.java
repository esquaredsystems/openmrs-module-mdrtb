package org.openmrs.module.mdrtb.web.taglib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class FunctionsTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PACKAGE_PATH = "org/openmrs/module/mdrtb/include/mdrtbtest-dataset.xml";
	
	@Before
	public void initTestData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet(XML_DATASET_PACKAGE_PATH);
		authenticate();
	}
	
	@Test
	public void getConceptFunction_shouldFindMatchingConceptById() {
		Concept c = Functions.getConcept("1441");
		assertEquals(new Integer(1441), c.getId());
	}
	
	@Test
	public void getConceptFunction_shouldFindMatchingConceptByUuid() {
		Concept c = Functions.getConcept("31b4bce4-0370-102d-b0e3-001ec94a0cc1");
		assertEquals(new Integer(1441), c.getId());
	}
	
	@Test
	public void getConceptFunction_shouldFindMatchingConceptByName() {
		Concept c = Functions.getConcept("RESISTANT");
		assertEquals(new Integer(1441), c.getId());
	}
	
	@Test
	public void getConceptFunction_shouldFindMatchingConceptByMdrtbMapping() {
		Concept c = Functions.getConcept("RESISTANT TO TUBERCULOSIS DRUG");
		assertEquals(new Integer(1441), c.getId());
	}
	
	@Test
	public void getConceptFunction_shouldReturnNullIfNoMatch() {
		Concept c = Functions.getConcept("Something that doesn't exist");
		assertNull(c);
	}
	
	@Test
	public void getDrugFunction_shouldFindMatchingDrugById() {
		Drug d = Functions.getDrug("2");
		assertEquals(new Integer(2), d.getId());
	}
	
	@Test
	public void getDrugFunction_shouldFindMatchingDrugName() {
		Drug d = Functions.getDrug("Triomune-30");
		assertEquals(new Integer(2), d.getId());
	}
	
	@Test
	public void getDrugFunction_shouldReturnNullIfNoMatch() {
		Drug d = Functions.getDrug("Something that doesn't exist");
		assertNull(d);
	}
	
}
