package com.bloyot.recordhomeworkcommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses records from csv, ssv, or psv input files. Because for our use case we know there will be no need to escape commas, pipes, or spaces
 * within the records themselves, we can get away with simple string splitting. In practice, you would use something like OpenCSV or Commons CSV to
 * handle the parsing in a more robust way.
 */
public class RecordParser {
    /**
     * Parses a set of records from an input file, using the provided delimiter. Returns a list of {@link Record} objects,
     * or throws an exception if it is unable to read or parse the file.
     * @param filePath - the input file path
     * @param delimiter - the delimiter to parse with (typically "," "|" or " ")
     * @return The list of records
     * @throws IOException if unable to read the fiel
     * @throws RecordParseException - if unable to parse any line of the files
     */
    public static List<Record> parse(Path filePath, String delimiter) throws IOException, RecordParseException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            List<Record> records = new ArrayList<>();
            while (reader.ready()) {
                records.add(parseRecord(reader.readLine(), delimiter));
            }
            return records;
        }
    }

    /**
     * Parse an individual line of the input file using the delimiter. Throws a record parse exception if unable to parse.
     */
    private static Record parseRecord(String line, String delimiter) throws RecordParseException {
        String[] split = line.split(delimiter);

        // too many or too few fields, something is wrong
        if (split.length != 5) {
            throw new RecordParseException("Expected 5 fields while parsing record, found " + split.length);
        }

        String lastName = split[0];
        String firstName = split[1];
        
        Gender gender = Gender.toGender(split[2]);
        if (gender == null) {
            throw new RecordParseException("Invalid gender, expected one of [male|female], found " + split[2]);
        }
        
        String favoriteColor = split[3];
        Instant dateOfBirth;
        try {
            dateOfBirth = Record.DATE_FORMAT.parse(split[4]).toInstant();
        } catch (ParseException e) {
            throw new RecordParseException("Invalid date format " + split[4] + " while parsing record. Expected " + Record.DATE_FORMAT_STRING, e);
        }

        return new Record(lastName, firstName, gender, favoriteColor, dateOfBirth);
    }

}
