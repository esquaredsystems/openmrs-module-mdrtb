package org.openmrs.module.mdrtb;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

public class MdrtbRestAPITest {
	
	public static String UUID_REGEX;
	
	public static String USERNAME;
	
	public static String PASSWORD;
	
	static {
		RestAssured.baseURI = "http://localhost:8080/openmrs/ws/rest/v1"; // OpenMRS server must be running
		USERNAME = "admin";
		PASSWORD = "Admin1234";
		UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
	}
	
	/** Endpoints **/
	@Test
	public void testForm8ReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/form8report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testForm89ReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/form89report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB03ReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb03report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB03uReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb03ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB07ReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb07report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB07uReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb07ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB08ReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb08report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB08uReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb08ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB03MissingReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb03missingreport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then()
		        .assertThat().statusCode(200);
	}
	
	@Test
	public void testTB03uMissingReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb03umissingreport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then()
		        .assertThat().statusCode(200);
	}
	
	@Test
	public void testPatientListEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/patientlist";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	/** Report Data **/
	@Test
	public void testForm8Report() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/form8report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].simpleForm8Table1Data.uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].simpleForm8Table2Data.uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].simpleForm8Table3Data.uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].simpleForm8Table4Data.uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].simpleForm8Table5aData.uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].simpleTB08Data.newAllDetected", greaterThan(0));
	}
	
	@Test
	public void testForm89Report() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "82be00a0-894b-42aa-812f-428f23e9fd7a");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/form89report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200).body("results[0].ageAtTB03Registration", is(notNullValue()));
	}
	
	@Test
	public void testTB03Report() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "82be00a0-894b-42aa-812f-428f23e9fd7a");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb03report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].patientUuid", matchesPattern(UUID_REGEX))
		        .body("results[0].identifier", is(notNullValue()));
	}
	
	@Test
	public void testTB03uReport() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb03ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].dotsYear", greaterThan(0))
		        .body("results[0].identifierMDR", is(notNullValue()));
	}
	
	@Test
	public void testTB07Report() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "82be00a0-894b-42aa-812f-428f23e9fd7a");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb07report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].totalAll", greaterThanOrEqualTo(0));
	}
	
	@Test
	public void testTB07uReport() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb07ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].totalDetections", greaterThanOrEqualTo(0));
	}
	
	@Test
	public void testTB08Report() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "82be00a0-894b-42aa-812f-428f23e9fd7a");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb08report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].newAllDetected", greaterThanOrEqualTo(0));
	}
	
	@Test
	public void testTB08uReport() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "7a27b5db-f952-4396-989f-2623b2ea735d");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb08ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].newTotalShort", greaterThanOrEqualTo(0));
	}
	
	@Test
	public void testTransferOutReport() {
		Map<String, Object> params = new HashMap<>();
		params.put("q", "bb167f6b-baf4-437a-93c1-376231fa78b4");
		String endpoint = "/mdrtb/transferout";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].encounter", is(notNullValue()));
	}
	
	@Test
	public void testRegimenReport() {
		Map<String, Object> params = new HashMap<>();
		String endpoint = "/mdrtb/transferout/777130b4-d711-49c1-be1e-e87fbecf17d6";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("uuid", matchesPattern(UUID_REGEX))
		        .body("encounter", is(notNullValue()));
	}
}
