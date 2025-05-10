package com.eg.blpsreports.utils;

import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

public class CalendarUtils {

    public static String generateCalendarHtml(List<LocalDate> availableDates) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstDay = currentMonth.atDay(1);
        LocalDate lastDay = currentMonth.atEndOfMonth();

        Set<LocalDate> available = new HashSet<>(availableDates);

        StringBuilder html = new StringBuilder();
        html.append("<table style='border-collapse: separate; border-spacing: 4px; width: 100%; font-family: sans-serif;'>");
        html.append("<thead><tr>");

        for (DayOfWeek day : DayOfWeek.values()) {
            html.append("<th style='padding: 4px;'>")
                    .append(day.getDisplayName(TextStyle.SHORT, new Locale("ru")))
                    .append("</th>");
        }

        html.append("</tr></thead><tbody><tr>");

        int valueOfFirstDay = firstDay.getDayOfWeek().getValue();
        if (valueOfFirstDay != 7) {
            for (int i = 1; i < valueOfFirstDay; i++) {
                html.append("<td></td>");
            }
        }

        LocalDate current = firstDay;
        while (!current.isAfter(lastDay)) {
            if (current.getDayOfWeek().getValue() == 1 && !current.equals(firstDay)) {
                html.append("</tr><tr>");
            }

            String bgColor = available.contains(current)
                    ? "#d4edda"
                    : "#f8d7da";

            html.append("<td style='padding: 6px; text-align: center; background-color: ")
                    .append(bgColor)
                    .append("; border-radius: 6px;'>")
                    .append(current.getDayOfMonth())
                    .append("</td>");

            current = current.plusDays(1);
        }

        html.append("</tr></tbody></table>");
        return html.toString();
    }

}
