package com.expansia.mask;

import java.util.regex.Pattern;

public record RegexMask(Pattern pattern, String replacement) {
}
