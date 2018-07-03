package com.belosh.dbmanager.util;

import java.util.regex.Pattern;

public class RegexpParser {
    // Split spring by semicolon which is not inside the quotes. Used to split queries.
    private static final Pattern pattern = Pattern.compile(";\\s*(?=([^']*'[^']*')*[^']*$)");

    public static String[] splitSemicolon(String line) {
        return pattern.split(line);
    }
}
