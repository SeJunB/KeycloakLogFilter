package com.expansia.mask;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.regex.Pattern;

public class MaskerTest {

  @Test
  public void maskClientId() {
    String logMessage = "type=LOGOUT,clientId=123122123123,ipAddress=1";
    Pattern pattern = Pattern.compile("clientId=[^,]+");
    String replacement = "clientId=****";
    RegexMask mask = new RegexMask(pattern, replacement);
    String masked = Masker.mask(logMessage, mask);
    Assertions.assertEquals("type=LOGOUT,clientId=****,ipAddress=1", masked);
  }

  @Test
  public void maskHasNoEffect() {
    String logMessage = "type=LOGOUT, ipAddress=123";
    Pattern pattern = Pattern.compile("clientId=[^,]+");
    String replacement = "clientId=****";
    RegexMask mask = new RegexMask(pattern, replacement);
    String masked = Masker.mask(logMessage, mask);
    Assertions.assertEquals(logMessage, masked);
  }

  @Test
  public void maskAuthSessionTabId() {
    String logMessage = "type=LOGOUT, ipAddress=123, authSessionTabId=123123";
    Pattern pattern = Pattern.compile("authSessionTabId=[^,]+");
    String replacement = "authSessionTabId=****";
    RegexMask mask = new RegexMask(pattern, replacement);
    String masked = Masker.mask(logMessage, mask);
    Assertions.assertEquals("type=LOGOUT, ipAddress=123, authSessionTabId=****", masked);
  }

}
