package org.openmrs.module.mdrtb;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

public class MdrtbRestAPITest {
	
	public static final String BASE_URI = "http://localhost:8080/openmrs/ws/rest/v1"; // OpenMRS server must be running
	public static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
	public static final String USERNAME = "admin";
	public static final String PASSWORD = "Admin1234";
	
	@Test
	public void testGetRole() {
		RestAssured.baseURI = BASE_URI;
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", "2020");
		params.put("quarter", "1");
		
		String field = "results[0].simpleForm8Table1Data.uuid";
		
		// Endpoint
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get("/mdrtb/form8report")
		        .then().assertThat().statusCode(200).body(field, matchesPattern(UUID_REGEX)); // Your JSON field and expected value
	}
}
