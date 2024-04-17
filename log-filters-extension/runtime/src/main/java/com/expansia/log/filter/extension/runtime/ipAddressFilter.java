package com.expansia.log.filter.extension.runtime;

import java.util.Collections;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import io.quarkus.logging.LoggingFilter;
import java.util.regex.Pattern;
import com.expansia.mask.RegexMask;
import com.expansia.mask.Masker;
import com.expansia.mask.Constants;
import java.util.ArrayList;
import org.jboss.logmanager.ExtLogRecord;
import java.text.MessageFormat;

// To use this log, you must set quarkus.log.console.filter=ip-address-filter in quarkus.properties.
@LoggingFilter(name = "ip-address-filter")
public class ipAddressFilter implements Filter {

  private static final List<RegexMask> masks;

  static {
    List<RegexMask> temp = new ArrayList<>();
    temp.add(new RegexMask(Pattern.compile("ipAddress=[^,]+"), "ipAddress=" + Constants.REDACTED));
    masks = Collections.unmodifiableList(temp);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean isLoggable(LogRecord record) {
    /*
     * In JBoss, there are 3 main ways of logging at a level for ex. at the info
     * level
     * LOG.info(..), LOG.infof("%s", "hi"), LOG.infov("{0}", "hi")
     * When a message is logged using infof or infov, the `record.getMessage()`
     * returns
     * the format string. For some reason, if you change the format string directly
     * for ex.
     * `record.getMessage(record.setMessage()), this results in only the format
     * string being logged.
     * To address this, this filter sets the record.message to the formatted
     * message.
     * See for more info https://github.com/quarkusio/quarkus/issues/27844
     */
    final String message;
    if (record instanceof ExtLogRecord) {
      message = ((ExtLogRecord) record).getFormattedMessage();
    } else {
      message = MessageFormat.format(record.getMessage(), record.getParameters());
    }
    String maskedMessage = message;
    for (RegexMask mask : masks) {
      maskedMessage = Masker.mask(maskedMessage, mask);
    }
    record.setMessage(maskedMessage);
    return true;
  }
}
