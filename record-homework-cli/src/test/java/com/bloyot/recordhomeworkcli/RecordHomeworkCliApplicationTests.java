package com.bloyot.recordhomeworkcli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

import java.nio.file.Paths;

import static com.bloyot.recordhomeworkcli.RecordHomeworkCliApplication.*;
import static org.mockito.Mockito.*;

/**
 * Tests the actual running of the cli. Cli apps are kind of tough to test, but most of the actual logic is tested
 * separately (in the common lib). The main difficulty here is that we have to stop the app from actually exiting in order to correctly
 * verify the behaviour and pass the test. We do this by overriding the exit method to throw an exception instead and spying it to verify the status code.
 *
 * In practice I probably wouldn't spend too much time on testing this piece of the CLI since it's mainly validating and parsing the input
 * and the best "solution" I could work up is a bit complex. But for the sake of completion (and the requested 80% code coverage) this should suffice.
 */
class RecordHomeworkCliApplicationTests {
    private RecordHomeworkCliApplication cli;

    @BeforeEach
    public void setup() {
        cli = spy(new RecordHomeworkCliApplication() {
            @Override
            protected void exitWithError(int statusCode) {
                throw new TestAbortedException();
            };
        });
    }

    @Test
    public void validRun() throws Exception {
        ApplicationArguments args = new DefaultApplicationArguments("--sort-type=birth_date",
                "--input-csv=" + Paths.get("src", "test", "resources", "records.csv").toAbsolutePath(),
                "--input-ssv=" + Paths.get("src", "test", "resources", "records.ssv").toAbsolutePath(),
                "--input-psv=" + Paths.get("src", "test", "resources", "records.psv").toAbsolutePath());
        cli.run(args);

        // ensure that we never call exit with an error code
        verify(cli, never()).exitWithError(anyInt());
    }

    @Test
    public void invalidInputFiles() throws Exception {
        ApplicationArguments args = new DefaultApplicationArguments("--sort-type=birth_date",
                "--input-csv=" + Paths.get("src", "test", "resources", "records.csv").toAbsolutePath(),
                "--input-ssv=" + Paths.get("z", "foo", "bar", "records.ssv").toAbsolutePath(), // doesn't exist
                "--input-psv=" + Paths.get("src", "test", "resources", "records.psv").toAbsolutePath());
        try {
            cli.run(args);
        } catch (TestAbortedException e) {};

        verify(cli).exitWithError(EXIT_MISSING_INPUT_FILE);
    }

    @Test
    public void invalidSort() throws Exception {
        ApplicationArguments args = new DefaultApplicationArguments("--sort-type=not_a_real_sort",
                "--input-csv=" + Paths.get("src", "test", "resources", "records.csv").toAbsolutePath(),
                "--input-ssv=" + Paths.get("src", "test", "resources", "records.ssv").toAbsolutePath(),
                "--input-psv=" + Paths.get("src", "test", "resources", "records.psv").toAbsolutePath());
        try {
            cli.run(args);
        } catch (TestAbortedException e) {};

        verify(cli).exitWithError(EXIT_INVALID_SORT);
    }

    @Test
    public void tooFewArguments() throws Exception {
        ApplicationArguments args = new DefaultApplicationArguments("--sort-type=birth_date",
                "--input-csv=" + Paths.get("src", "test", "resources", "records.csv").toAbsolutePath(),
                "--input-psv=" + Paths.get("src", "test", "resources", "records.psv").toAbsolutePath());
        try {
            cli.run(args);
        } catch (TestAbortedException e) {};

        verify(cli).exitWithError(EXIT_MISSING_ARGS);
    }

}
