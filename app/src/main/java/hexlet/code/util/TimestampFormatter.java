package hexlet.code.util;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class TimestampFormatter {
    public static String format(Timestamp timestamp, String pattern) {
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern(pattern)); //"dd/MM/yyyy HH:mm"
    }
}
