package roronya.kanaicli;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class TimeTool {
    @Tool(name = "get_now", description = "return current date as String")
    public String getNow() {
        return LocalDateTime
                .now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();
    }

    @Tool(name = "get_day_of_week",
            description = "return day of week. arg are dateString and formatPattern.")
    public String getDayOfWeek(String dateString, String formatPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        LocalDate date = LocalDate.parse(dateString, formatter);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.JAPANESE);
    }
}

