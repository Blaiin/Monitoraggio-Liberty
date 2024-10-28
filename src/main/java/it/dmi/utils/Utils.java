package it.dmi.utils;

public class Utils {

    public static String capitalize(String s) {
        if (s == null) throw new IllegalArgumentException("String to capitalize cannot be null");
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

}
