package ru.otus.exchange.xsdshema.adapter;

import java.util.regex.Pattern;

public class GMonthAdapter {

    private GMonthAdapter() {}

    private static Pattern pattern = Pattern.compile("\\-\\-(0[1-9]|1[012])");

    public static Integer parseDate(String s) {
        if (!pattern.matcher(s).matches()) {
            throw new IllegalArgumentException("Значение '" + s + "' не соответствует типу gMonth.");
        }

        String intString = s.substring(2, 4);
        return Integer.valueOf(intString);
    }

    public static String printDate(Integer dt) {
        return "--" + String.format("%02d", dt);
    }
}
