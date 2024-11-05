package it.dmi.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NullChecks {

    public static boolean nullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
