/**
 * 
 */
package org.openmrs.module.mdrtb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;

/**
 * @author owais.hussain@esquaredsystems.com
 */
public class MdrtbServiceLocationTest extends MdrtbTestBase {
	
	MdrtbService service;
	
	LocalDate startDate = new LocalDate(2022, 8, 1);
	
	LocalDate endDate = new LocalDate(2022, 8, 31);
	
	LocalDate now = new LocalDate();
	
	Region dushanbe;// = new Region("Dushanbe", 3);
	
	Region khatlon;// = new Region("Khatlon Region", 4);
	
	Region republic;// = new Region("Republican Subordination", 5);
	
	District fayzobad;// = new District("Fayzobod District", 6);
	
	District nurobad;// = new District("Nurobod District", 7);
	
	District ferdowsi;// = new District("Ferdowsi District", 13);
	
	District mansur;// = new District("Shah Mansur District", 14);
	
	Facility fayzobodLab;// = new Facility("Fayzobod Central Testing Lab", 10);
	
	Facility dushanbeGeneralHospital;// = new Facility("Dushanbe General Hospital", 201);
	
	@Before
	public void runBeforeEachTest() throws Exception {
		super.initTestData();
		service = Context.getService(MdrtbService.class);
	}
	
	@Test
	public void testMdrtbService() {
		assertNotNull(service);
	}
	
	@Test
	public final void testGetRegion() {
		assertEquals("Region mismatch", dushanbe, service.getRegion(dushanbe.getId()));
	}
	
	@Test
	public final void testGetRegions() {
		List<Region> list = service.getRegions();
		assertTrue(list.contains(dushanbe));
		assertTrue(list.contains(republic));
		assertTrue(list.contains(khatlon));
	}
	
	@Test
	public final void testGetDistrict() {
		assertEquals("District mismatch", fayzobad, service.getDistrict(6));
		assertEquals("District mismatch", fayzobad, service.getDistrict("Fayzobod District"));
	}
	
	@Test
	public final void testGetDistricts() {
		List<District> list = service.getDistricts();
		assertTrue(list.contains(fayzobad));
		assertTrue(list.contains(nurobad));
	}
	
	@Test
	public final void testGetDistrictsByParent() {
		List<District> list = service.getDistrictsByParent(republic.getId());
		assertTrue(list.contains(fayzobad));
	}
	
	@Test
	public final void testGetFacility() {
		assertEquals("Facility mismatch", fayzobodLab, service.getFacility(10));
	}
	
	@Test
	public final void testGetFacilities() {
		List<Facility> list = service.getFacilitiesByParent(fayzobad.getId());
		assertTrue(list.contains(fayzobodLab));
	}
	
	@Test
	public final void testGetLocation() {
		Location actual = service.getLocation(republic.getId(), fayzobad.getId(), fayzobodLab.getId());
		Location expected = Context.getLocationService().getLocation(200);
		assertEquals("Location mismatch", expected, actual);
	}
	
	@Test
	@Ignore
	public final void testGetLocationListForDushanbe() {
	}
	
	@Test
	@Ignore
	public final void testGetLocationsFromDistrict() {
		service.getLocationsFromDistrict(fayzobad);
	}
	
	@Test
	@Ignore
	public final void testGetLocationsFromFacility() {
		List<Location> list = service.getLocationsFromFacility(dushanbeGeneralHospital);
		Location expected = Context.getLocationService().getLocation(dushanbeGeneralHospital.getId());
		assertTrue(list.contains(expected));
	}
	
	@Test
	@Ignore
	public final void testGetLocationsFromRegion() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetRegDistricts() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetRegDistrictsInt() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetRegFacilities() {
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public final void testGetRegFacilitiesInt() {
		fail("Not yet implemented");
	}
}
