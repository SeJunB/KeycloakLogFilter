package com.expansia.log.filter.extension.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.AfterEach;
import com.expansia.log.filter.extension.runtime.ipAddressFilter;
import com.expansia.mask.Constants;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import org.jboss.logging.Logger;
import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.InMemoryLogHandler;
import java.util.List;

// References:
// https://stackoverflow.com/questions/68375755/how-to-intercept-logging-messages-with-quarkus-for-testing-purposes
// https://quarkus.io/guides/writing-extensions#testing-extensions
public class ipAddressFilterTest {
    private static final String loggerName = "test-logger";
    private static final Logger LOG = Logger.getLogger(loggerName);
    // Based on https://github.com/quarkusio/quarkus/commit/57fb0cc57bf435bbdb86ca0614bf03dc04bea383
    private static final java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger(loggerName);
    private static final InMemoryLogHandler inMemoryLogHandler = new InMemoryLogHandler(
            record -> true);

    static {
        // By adding this handler, whenever a message is logged using the "test-logger",
        // that message will be stored in the inMemoryLogHandler.records list.
        rootLogger.addHandler(inMemoryLogHandler);
    }

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(ipAddressFilter.class))
            .overrideConfigKey("quarkus.log.console.filter", "ip-address-filter"); // Add the ip address filter.


    @AfterEach
    public void clearInMemoryLogRecords() {
        // There is a clearRecords method in
        // https://github.com/quarkusio/quarkus/blob/main/test-framework/junit5-internal/src/main/java/io/quarkus/test/InMemoryLogHandler.java
        // but it isn't public. close does the same thing as clearRecords but is public.
        inMemoryLogHandler.close();
    }

    @Test
    public void formattedLogMessagesAreLoggedCorrectly() {
        String expected = "apple, cat, david";
        LOG.infof("%s, %s, %s", "apple", "cat", "david");
        LOG.infov("{0}, {1}, {2}", "apple", "cat", "david");
        List<LogRecord> records = inMemoryLogHandler.getRecords();
        Assertions.assertEquals(2, records.size());
        Assertions.assertEquals(expected, records.get(0).getMessage());
        Assertions.assertEquals(expected, records.get(1).getMessage());
    }

    @Test
    public void logNotContainingIpAddressAreUnaffected() {
        String expected = "userId=123, goodbye=123";
        LOG.info(expected);
        List<LogRecord> records = inMemoryLogHandler.getRecords();
        Assertions.assertEquals(1, records.size());
        Assertions.assertEquals(expected, records.get(0).getMessage());
    }

    @Test
    public void ipAddressIsMasked() {
        String message = "code_id=123, ipAddress=127.0.0.1, authSessionTabId=124123";
        String expected = String.format("code_id=123, ipAddress=%s, authSessionTabId=124123", Constants.REDACTED);
        LOG.info(message);
        List<LogRecord> records = inMemoryLogHandler.getRecords();
        Assertions.assertEquals(1, records.size());
        Assertions.assertEquals(expected, records.get(0).getMessage());
    }
}
