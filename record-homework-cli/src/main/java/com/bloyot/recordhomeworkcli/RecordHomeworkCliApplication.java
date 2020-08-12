package com.bloyot.recordhomeworkcli;

import com.bloyot.recordhomeworkcommon.Record;
import com.bloyot.recordhomeworkcommon.RecordParser;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SpringBootApplication
public class RecordHomeworkCliApplication implements ApplicationRunner {

    public static final int EXIT_MISSING_ARGS = 1;
    public static final int EXIT_MISSING_INPUT_FILE = 2;
    public static final int EXIT_INVALID_SORT = 3;

    public static void main(String[] args) {
        SpringApplication.run(RecordHomeworkCliApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // validate that we have all the required options
        // assumption per spec is we have 3 input files, 1 of each delimiter type,
        // as we as the method of sorting
        if (!args.containsOption("input-csv") ||
                !args.containsOption("input-ssv") ||
                !args.containsOption("input-psv") ||
                !args.containsOption("sort-type")) {

            exitWithError(EXIT_MISSING_ARGS);
        }

        // parse our 4 required input values, the above check validates we have at least on for each option value, and if
        // multiple are provided we default to the first
        String csvInput = args.getOptionValues("input-csv").get(0);
        String ssvInput = args.getOptionValues("input-ssv").get(0);
        String psvInput = args.getOptionValues("input-psv").get(0);

        String sortTypeString = args.getOptionValues("sort-type").get(0);
        SortType sortType = SortType.toSortType(sortTypeString);

        // validate the input options themselves, terminating if invalid
        validateInputOptions(csvInput, ssvInput, psvInput, sortType);

        // parse records and join into a single list
        List<Record> records = new ArrayList<>();
        records.addAll(RecordParser.parseFile(Paths.get(csvInput), ","));
        records.addAll(RecordParser.parseFile(Paths.get(ssvInput), " "));
        records.addAll(RecordParser.parseFile(Paths.get(psvInput), "|"));

        // sort by sort type and display the result
        records.stream().sorted(getSortComparator(sortType)).forEach(System.out::println);
    }

    private Comparator<Record> getSortComparator(SortType sortType) {
        if (sortType == SortType.GENDER) {
            // by default (since we defined male first in the enum), male comes first in the sort, so just reverse it.
            // then comparing provides sub sort after gender
            return Comparator.comparing(Record::getGender).reversed().thenComparing(Record::getLastName);
        }
        if (sortType == SortType.BIRTH_DATE) {
            return Comparator.comparing(Record::getDateOfBirth);
        }
        if (sortType == SortType.LAST_NAME) {
            return Comparator.comparing(Record::getLastName).reversed();
        }

        // dead code, but required for compiling
        return null;
    }

    private void validateInputOptions(String csvInput, String ssvInput, String psvInput, SortType sortType) {
        if (sortType == null) {
            System.out.println("Sort type must be one of [gender|birth_date|last_name]");
            exitWithError(EXIT_INVALID_SORT);
        }

        if (!Paths.get(csvInput).toFile().exists()) {
            System.out.println("CSV input file does not exist!");
            exitWithError(EXIT_MISSING_INPUT_FILE);
        }

        if (!Paths.get(ssvInput).toFile().exists()) {
            System.out.println("SSV input file does not exist!");
            exitWithError(EXIT_MISSING_INPUT_FILE);
        }

        if (!Paths.get(psvInput).toFile().exists()) {
            System.out.println("PSV input file does not exist!");
            exitWithError(EXIT_MISSING_INPUT_FILE);
        }
    }

    protected void exitWithError(int statusCode) {
        System.out.println("Usage: java -jar <jar> --input-csv=<path to csv file> --input-psv=<path to psv file> --input-ssv=<path to ssv file> --sort-type=[gender|birth_date|last_name]");
        System.exit(statusCode);
    }
}
