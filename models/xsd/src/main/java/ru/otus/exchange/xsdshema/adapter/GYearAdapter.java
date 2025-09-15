package ru.otus.exchange.xsdshema.adapter;

import java.util.regex.Pattern;

public class GYearAdapter {

    private GYearAdapter() {}

    private static Pattern pattern = Pattern.compile("[\\d]{4}");

    public static Integer parseDate(String s) {
        if (!pattern.matcher(s).matches()) {
            throw new IllegalArgumentException("Значение '" + s + "' не соответствует типу gYear.");
        }

        return Integer.valueOf(s);
    }

    public static String printDate(Integer dt) {
        return String.format("%04d", dt);
    }
}
