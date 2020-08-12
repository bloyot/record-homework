package com.bloyot.recordhomeworkapi;

import com.bloyot.recordhomeworkcommon.Record;
import com.bloyot.recordhomeworkcommon.RecordParseException;
import com.bloyot.recordhomeworkcommon.RecordParser;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.bloyot.recordhomeworkcommon.RecordParser.VALID_DELIMITERS;

/**
 * Controller that allows creating and retrieving records.
 */
@RestController
@RequestMapping("/records")
public class RecordController {

    /**
     * This is our stand-in for a database/datastore in this toy application. Simply add records here when post requests
     * are made, and return them when get requests are made.
     */
    private final List<Record> records = new ArrayList<>();

    /**
     * Allows creation of a record by sending the record data and delimiter in a json message mapped to {@link CreateRecordCommand}.
     * Note: I originally implemented this sending in the raw string data and a header to indicate the delimiter, but that was a bit messy
     * (and didn't allow sending " " as a header value) and the provided requirements were unclear if sending a json message in was acceptable.
     * @return 204 if successful, 400 if the request body is invalid
     */
    @PostMapping
    public ResponseEntity createRecord(@RequestBody CreateRecordCommand recordCommand) {

        if (StringUtils.isEmpty(recordCommand.getData())) {
            return badRequest("Record data must be provided");
        }

        // if no delimiter provided or not one of the valid options
        if (StringUtils.isEmpty(recordCommand.getDelimiter()) || !VALID_DELIMITERS.contains(recordCommand.getDelimiter())) {
            return badRequest("Invalid delimiter: " + recordCommand.getDelimiter());
        }

        // parse and add to the record list
        try {
            records.add(RecordParser.parseRecord(recordCommand.getData(), recordCommand.getDelimiter()));
        } catch (RecordParseException e) {
            return badRequest("Unable to parse provided record using delimiter " +
                    recordCommand.getDelimiter() + ": " + recordCommand.getData());
        }

        // indicates successful create, but nothing to explicitly return
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns the records sorted by gender.
     * @param sortOrder whether to sort int ascending or descending order, defaults to asc
     * @return A list of {@link Record}
     */
    @GetMapping("/gender")
    public List<Record> getRecordsSortedByGender(@RequestParam(required = false) String sortOrder) {
        return getRecords(Comparator.comparing(Record::getGender), sortOrder);
    }

    /**
     * Returns the records sorted by birth date.
     * @param sortOrder whether to sort int ascending or descending order, defaults to asc
     * @return A list of {@link Record}
     */
    @GetMapping("/birthdate")
    public List<Record> getRecordsSortedByBirthDate(@RequestParam(required = false) String sortOrder) {
        return getRecords(Comparator.comparing(Record::getDateOfBirth), sortOrder);
    }

    /**
     * Returns the records sorted by lastName.
     * @param sortOrder whether to sort int ascending or descending order, defaults to asc
     * @return A list of {@link Record}
     */
    @GetMapping("/name")
    public List<Record> getRecordsSortedByLastName(@RequestParam(required = false) String sortOrder) {
        return getRecords(Comparator.comparing(Record::getLastName), sortOrder);
    }

    private ResponseEntity badRequest(String message) {
        return ResponseEntity.badRequest().body("{\"status\": 400, \"response\": " + message);
    }

    /**
     * Helper method to return sorted records
     */
    private List<Record> getRecords(Comparator<Record> comparator, String sortOrder) {
        // no need to check for asc as that is the default
        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return records.stream().sorted(comparator).collect(Collectors.toList());
    }

}
