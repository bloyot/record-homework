package com.bloyot.recordhomeworkcommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Parses records from csv, ssv, or psv input files. Because for our use case we know there will be no need to escape commas, pipes, or spaces
 * within the records themselves, we can get away with simple string splitting. In practice, you would use something like OpenCSV or Commons CSV to
 * handle the parsing in a more robust way.
 */
public class RecordParser {

    public static final Set<String> VALID_DELIMITERS = new HashSet<>(Arrays.asList(",", " ", "|"));

    /**
     * Parses a set of records from an input file, using the provided delimiter. Returns a list of {@link Record} objects,
     * or throws an exception if it is unable to read or parse the file.
     * @param filePath - the input file path
     * @param delimiter - the delimiter to parse with (typically "," "|" or " ")
     * @return The list of records
     * @throws IOException if unable to read the file
     * @throws RecordParseException - if unable to parse any line of the files
     */
    public static List<Record> parseFile(Path filePath, String delimiter) throws IOException, RecordParseException {
        // input validations
        if (filePath == null || !filePath.toFile().exists()) {
            throw new RecordParseException("Invalid file path");
        }
        if (!VALID_DELIMITERS.contains(delimiter)) {
            throw new RecordParseException("Invalid delimiter \"" + delimiter + "\" provided");
        }

        // read each line, parse it, and add it to the record list
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            List<Record> records = new ArrayList<>();
            while (reader.ready()) {
                records.add(parseLine(reader.readLine(), delimiter));
            }
            return records;
        }
    }

    /**
     * Parse an individual line of the input file using the delimiter. Throws a record parse exception if unable to parse.
     * @param line the specific line to parse
     * @param delimiter  the delimiter to use
     * @return an {@link Record} parsed from the string
     * @throws RecordParseException - if unable to parse for some reason
     */
    public static Record parseLine(String line, String delimiter) throws RecordParseException {
        // input validations
        if (line == null || line.isEmpty()) {
            throw new RecordParseException("Invalid record line \"" + line + "\" provided for parsing");
        }

        if (!VALID_DELIMITERS.contains(delimiter)) {
            throw new RecordParseException("Invalid delimiter \"" + delimiter + "\" provided");
        }

        // handle the annoying edge case because split treats pipe as a regex character
        String splitDelimiter = delimiter;
        if ("|".equals(splitDelimiter)) {
            splitDelimiter = "\\|";
        }
        String[] split = line.split(splitDelimiter);

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
        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(split[4], Record.DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RecordParseException("Invalid date format " + split[4] + " while parsing record. Expected " + Record.DATE_FORMAT_STRING, e);
        }

        return new Record(lastName, firstName, gender, favoriteColor, dateOfBirth);
    }

}
