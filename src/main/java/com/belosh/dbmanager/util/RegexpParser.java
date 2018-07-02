package com.belosh.dbmanager.util;

import java.util.regex.Pattern;

public class RegexpParser {
    // Split by semicolon which not inside the double-quotes
    private static final Pattern pattern = Pattern.compile(";\\s*(?=([^']*'[^']*')*[^']*$)");

    public static String[] splitSemicolon(String line) {
        return pattern.split(line);
    }
}
