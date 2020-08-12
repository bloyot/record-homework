package com.bloyot.recordhomeworkcommon;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecordParserTest {

    // Tests for RecordParse#parseRecord
    @Test
    public void parseValidCSV() throws RecordParseException {
        Record record = RecordParser.parseLine("Nelson,Lukas,male,magenta,09/03/2031", ",");
        assertEquals("Nelson,Lukas,male,magenta,09/03/2031", record.toString());
    }

    @Test
    public void parseValidPSV() throws RecordParseException {
        Record record = RecordParser.parseLine("Nelson|Lukas|male|magenta|09/03/2031", "|");
        assertEquals("Nelson,Lukas,male,magenta,09/03/2031", record.toString());
    }

    @Test
    public void parseValidSSV() throws RecordParseException {
        Record record = RecordParser.parseLine("Nelson Lukas male magenta 09/03/2031", " ");
        assertEquals("Nelson,Lukas,male,magenta,09/03/2031", record.toString());
    }

    // I'm considering no last name, first name, or favorite color as valid as long as the delimiters are there,
    // but it could be easily changed to disallow that by validating in the parser in the same place we check date and
    // gender if desired
    @Test
    public void parseValidNoLastName() throws RecordParseException {
        Record record = RecordParser.parseLine("Nelson  male magenta 09/03/2031", " ");
        assertEquals("Nelson,,male,magenta,09/03/2031", record.toString());
    }

    @Test
    public void invalidDelimiter() {
        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseLine("Nelson,Lukas,male,magenta,09/03/2031", "/");
        });

        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseLine("Nelson,Lukas,male,magenta,09/03/2031", "");
        });

        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseLine("Nelson,Lukas,male,magenta,09/03/2031", "|");
        });
    }

    @Test
    public void emptyRecordString() {
        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseLine("", "|");
        });
    }

    @Test
    public void tooManyFields() {
        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseLine("Nelson,Lukas,male,magenta,09/03/2031,anotherfield", ",");
        });
    }

    @Test
    public void tooFewFields() {
        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseLine("Nelson,male,magenta,09/03/2031,anotherfield", ",");
        });
    }

    @Test
    public void invalidGender() {
        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseLine("Nelson,Lukas,Lukas,magenta,09/03/2031,", ",");
        });
    }

    @Test
    public void invalidDate() {
        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseLine("Nelson,Lukas,male,magenta,090/03/2031", ",");
        });
    }

    // tests for RecordParser#parse
    @Test
    public void parseValidFile() throws IOException, RecordParseException {
        List<Record> records = RecordParser.parseFile(Paths.get("src", "test", "resources", "records_valid.psv"), "|");
        assertEquals(3, records.size());
        assertEquals("Ada,Weaver,female,red,12/15/2037", records.get(0).toString());
        assertEquals("Kobe,Bass,male,green,10/11/1949", records.get(1).toString());
        assertEquals("Riya,Murray,female,green,09/26/1945", records.get(2).toString());
    }

    @Test
    public void parseEmptyFile() throws IOException, RecordParseException {
        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseFile(Paths.get("src", "test", "resources", "records_empty.psv"), "/");
        });
    }

    @Test
    public void parseInvalidFile() throws IOException, RecordParseException {
        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseFile(Paths.get("src", "test", "resources", "records_invalid.psv"), "/");
        });
    }

    @Test
    public void parseFileBadDelimiter() throws IOException, RecordParseException {
        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseFile(Paths.get("src", "test", "resources", "records_valid.psv"), "/");
        });
    }

    @Test
    public void parseNonExistantFile() throws IOException, RecordParseException {
        assertThrows(RecordParseException.class, () -> {
            RecordParser.parseFile(Paths.get("z", "foo", "bar", "nonexistant.csv"), ",");
        });
    }
}
