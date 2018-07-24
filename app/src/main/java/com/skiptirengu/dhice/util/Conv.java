package com.skiptirengu.dhice.util;

import android.databinding.InverseMethod;

public class Conv {

    @InverseMethod("stringToInt")
    public static String intToString(int num) {
        return String.valueOf(num);
    }

    public static int stringToInt(String num) {
        return num == null || num.isEmpty() ? 0 : Integer.valueOf(num);
    }

    public static boolean nullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
