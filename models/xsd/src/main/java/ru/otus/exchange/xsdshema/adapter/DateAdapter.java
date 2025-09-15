package ru.otus.exchange.xsdshema.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateAdapter {

    private DateAdapter() {}

    public static Date parseDate(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }

        // YYYY-MM-DD
        // SimpleDateFormat не потокобезопасный, поэтому явная реализация
        String[] split = s.split("\\-");

        int year = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int dayOfMonth = Integer.parseInt(split[2]);

        GregorianCalendar gc = new GregorianCalendar(year, month - 1, dayOfMonth);
        return gc.getTime();
    }

    public static String printDate(Date dt) {
        if (dt == null) {
            return null;
        }

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dt);

        int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        int month = gc.get(Calendar.MONTH) + 1;
        int year = gc.get(Calendar.YEAR);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%04d", year));
        sb.append('-');
        sb.append(String.format("%02d", month));
        sb.append('-');
        sb.append(String.format("%02d", dayOfMonth));
        return sb.toString();
    }
}
