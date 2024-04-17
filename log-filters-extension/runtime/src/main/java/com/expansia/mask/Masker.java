package com.expansia.mask;

import java.util.regex.Matcher;

public class Masker {

  /*
   * Replaces all occurrence of pattern in s with replacement.
   * 
   * @param s string to mask
   * 
   * @param mask record containing the pattern and mask.
   * 
   * @return a string with every occurrence of pattern replaced with replacement.
   */
  public static String mask(String s, RegexMask mask) {
    Matcher matcher = mask.pattern().matcher(s);
    return matcher.replaceAll(mask.replacement());
  }
}
