/**
 * 
 */
package org.openmrs.module.mdrtb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.exception.MissingConceptException;

/**
 * @author owais
 */
public class MdrtbConceptsTest extends MdrtbTestBase {
	
	private MdrtbConcepts concepts;
	
	@Before
	public void setUp() throws Exception {
		super.initTestData();
		concepts = new MdrtbConcepts();
	}
	
	@Test
	public final void testGetAllConceptMappings() {
		assertTrue("MdrtbConcepts currently declares mappings as String constants, not String arrays", concepts
		        .getAllConceptMappings().isEmpty());
	}
	
	@Test
	public final void testInitializeEverythingAboutConcept() {
		Concept concept = Context.getConceptService().getConcept(12);
		
		concepts.initializeEverythingAboutConcept(concept);
		
		assertNotNull(concept.getDatatype());
		assertFalse(concept.getNames().isEmpty());
		assertFalse(concept.getAnswers().isEmpty());
		assertNotNull(concept.getAnswers().iterator().next().getAnswerConcept().getNames());
	}
	
	@Test(expected = MissingConceptException.class)
	public final void testLookup() {
		concepts.lookup("NONEXISTENT MDRTB CONCEPT");
	}
	
	@Test
	public final void testResetCache() throws Exception {
		getCache().put(MdrtbConcepts.YES, yesConcept);
		assertTrue(getCache().containsKey(MdrtbConcepts.YES));
		
		concepts.resetCache();
		
		assertTrue(getCache().isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Concept> getCache() throws Exception {
		Field cache = MdrtbConcepts.class.getDeclaredField("cache");
		cache.setAccessible(true);
		return (Map<String, Concept>) cache.get(concepts);
	}
	
}
