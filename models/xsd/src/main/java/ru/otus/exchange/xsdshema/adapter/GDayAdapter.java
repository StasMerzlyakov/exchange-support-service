package ru.otus.exchange.xsdshema.adapter;

import java.util.regex.Pattern;

public class GDayAdapter {

    private GDayAdapter() {}

    private static Pattern pattern = Pattern.compile("\\-\\-\\-(0[1-9]|[12]\\d|3[01])");

    public static Integer parseDate(String s) {
        if (!pattern.matcher(s).matches()) {
            throw new IllegalArgumentException("Значение '" + s + "' не соответствует типу gDay.");
        }

        String intString = s.substring(3, 5);
        return Integer.valueOf(intString);
    }

    public static String printDate(Integer dt) {
        return "---" + String.format("%02d", dt);
    }
}
