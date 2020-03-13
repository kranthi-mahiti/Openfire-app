package com.sanjay.openfire.xmpputils;

public class StringUtils {


    public static String getFirstLetter(String name) {
        String defaultname = "T";
        if (!name.isEmpty() && name != null && !name.equalsIgnoreCase("")) {
            return String.valueOf(name.toUpperCase().charAt(0));
        }
        return defaultname;
    }
}
