package com.bloyot.recordhomeworkapi;

import com.bloyot.recordhomeworkcommon.Gender;
import com.bloyot.recordhomeworkcommon.Record;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RecordHomeworkApiApplicationTests {
	private static final Gson GSON = new Gson();

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Auto wire the bean here so that we can set the records value for testing purposes. In practice, with an actual
	 * database, you would either mock the database service to return the responses needed for the test, or use an in memory
	 * database initialized with the mock data for you test.
	 */
	@Autowired
	private RecordController recordController;

	@BeforeEach
	public void setup() {
		// initialize some test data
		// have to create a new array list wrapper because Arrays.asList creates an immutable list
		List<Record> testRecords = new ArrayList<>(Arrays.asList(
			new Record("Ada", "Weaver", Gender.FEMALE, "red", LocalDate.of(2037,12,15)),
			new Record("Kobe", "Bass", Gender.MALE, "green", LocalDate.of(1949,10,11)),
			new Record("Riya", "Murray", Gender.FEMALE, "orange", LocalDate.of(1945,9,26))
		));
		ReflectionTestUtils.setField(recordController, "records", testRecords);
	}

	@Test
	void getRecordsByGender() throws Exception {
		mockMvc.perform(
			get("/records/gender"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", is(3)))
			.andExpect(jsonPath("$[0].lastName", is("Kobe")))
			.andExpect(jsonPath("$[1].lastName", is("Ada")))
			.andExpect(jsonPath("$[2].lastName", is("Riya")));
	}

	@Test
	void getRecordsByGenderDesc() throws Exception {
		mockMvc.perform(
			get("/records/gender")
				.queryParam("sortOrder", "desc"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", is(3)))
			.andExpect(jsonPath("$[0].lastName", is("Ada")))
			.andExpect(jsonPath("$[1].lastName", is("Riya")))
			.andExpect(jsonPath("$[2].lastName", is("Kobe")));
	}

	@Test
	void getRecordsByBirthDate() throws Exception {
		mockMvc.perform(
			get("/records/birthdate"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", is(3)))
			.andExpect(jsonPath("$[0].lastName", is("Riya")))
			.andExpect(jsonPath("$[1].lastName", is("Kobe")))
			.andExpect(jsonPath("$[2].lastName", is("Ada")));
	}

	@Test
	void getRecordsByBirthDateDesc() throws Exception {
		mockMvc.perform(
			get("/records/birthdate")
				.queryParam("sortOrder", "desc"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", is(3)))
			.andExpect(jsonPath("$[0].lastName", is("Ada")))
			.andExpect(jsonPath("$[1].lastName", is("Kobe")))
			.andExpect(jsonPath("$[2].lastName", is("Riya")));
	}

	@Test
	void getRecordsByLastName() throws Exception {
		mockMvc.perform(
			get("/records/name"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", is(3)))
			.andExpect(jsonPath("$[0].lastName", is("Ada")))
			.andExpect(jsonPath("$[1].lastName", is("Kobe")))
			.andExpect(jsonPath("$[2].lastName", is("Riya")));
	}

	@Test
	void getRecordsByLastNameDesc() throws Exception {
		mockMvc.perform(
			get("/records/name")
				.queryParam("sortOrder", "desc"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()", is(3)))
			.andExpect(jsonPath("$[0].lastName", is("Riya")))
			.andExpect(jsonPath("$[1].lastName", is("Kobe")))
			.andExpect(jsonPath("$[2].lastName", is("Ada")));
	}

	@Test
	void createRecordAsCSV() throws Exception {
		CreateRecordCommand command = new CreateRecordCommand("Russell,Khalid,male,indigo,03/24/2078", ",");
		mockMvc.perform(
			post("/records")
				.content(GSON.toJson(command))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		List<Record> records = (List<Record>)ReflectionTestUtils.getField(recordController, "records");
		assertEquals(4, records.size());
		assertEquals("Russell", records.get(3).getLastName());
		assertEquals("Khalid", records.get(3).getFirstName());
		assertEquals(Gender.MALE, records.get(3).getGender());
		assertEquals("indigo", records.get(3).getFavoriteColor());
		assertEquals(LocalDate.of(2078, 3, 24), records.get(3).getDateOfBirth());
	}

	@Test
	void createRecordAsSSV() throws Exception {
		CreateRecordCommand command = new CreateRecordCommand("Russell Khalid male indigo 03/24/2078", " ");
		mockMvc.perform(
			post("/records")
				.content(GSON.toJson(command))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		List<Record> records = (List<Record>)ReflectionTestUtils.getField(recordController, "records");
		assertEquals(4, records.size());
		assertEquals("Russell", records.get(3).getLastName());
		assertEquals("Khalid", records.get(3).getFirstName());
		assertEquals(Gender.MALE, records.get(3).getGender());
		assertEquals("indigo", records.get(3).getFavoriteColor());
		assertEquals(LocalDate.of(2078, 3, 24), records.get(3).getDateOfBirth());
	}

	@Test
	void createRecordAsPSV() throws Exception {
		CreateRecordCommand command = new CreateRecordCommand("Russell|Khalid|male|indigo|03/24/2078", "|");
		mockMvc.perform(
			post("/records")
				.content(GSON.toJson(command))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		List<Record> records = (List<Record>)ReflectionTestUtils.getField(recordController, "records");
		assertEquals(4, records.size());
		assertEquals("Russell", records.get(3).getLastName());
		assertEquals("Khalid", records.get(3).getFirstName());
		assertEquals(Gender.MALE, records.get(3).getGender());
		assertEquals("indigo", records.get(3).getFavoriteColor());
		assertEquals(LocalDate.of(2078, 3, 24), records.get(3).getDateOfBirth());
	}

	@Test
	void createRecordWithInvalidDelimter() throws Exception {
		CreateRecordCommand command = new CreateRecordCommand("Russell|Khalid|male|indigo|03/24/2078", "/");
		mockMvc.perform(
			post("/records")
				.content(GSON.toJson(command))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("{\"status\": \"400\", \"response\": \"Invalid delimiter\"}"));
	}

	@Test
	void createRecordWithWrongDelimter() throws Exception {
		CreateRecordCommand command = new CreateRecordCommand("Russell|Khalid|male|indigo|03/24/2078", ",");
		mockMvc.perform(
			post("/records")
				.content(GSON.toJson(command))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("{\"status\": \"400\", \"response\": \"Unable to parse provided record data\"}"));
	}

	@Test
	void createRecordWithNoData() throws Exception {
		CreateRecordCommand command = new CreateRecordCommand(null, ",");
		mockMvc.perform(
			post("/records")
				.content(GSON.toJson(command))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("{\"status\": \"400\", \"response\": \"Record data must be provided\"}"));
	}

	@Test
	void createRecordWithInvalidData() throws Exception {
		CreateRecordCommand command = new CreateRecordCommand("Russell|male|indigo|03/24/2078", "|");
		mockMvc.perform(
			post("/records")
				.content(GSON.toJson(command))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("{\"status\": \"400\", \"response\": \"Unable to parse provided record data\"}"));
	}
}
