package id.co.msiglife.pollux.schduler.util;

import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class ConvertUtil {

	public static String convertDateApproval(Date date) {
		if (date != null) {
			final String newFormat = "dd/MM/yyyy HH:mm:ss";
			SimpleDateFormat sdf = new SimpleDateFormat(newFormat);
			return sdf.format(date);
		} else {
			return "-";
		}
	}

    public static String convertDateToJakartaTime(Date date) {
        Instant instant = date.toInstant();
        ZonedDateTime jakartaTime = instant.atZone(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
        return jakartaTime.format(formatter);
    }

    public static String convertDateToJakartaTime(LocalDate localDate) {
        // Set time to 00:00 in Jakarta timezone
        ZonedDateTime jakartaTime = localDate.atStartOfDay(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
        return jakartaTime.format(formatter);
    }

    public static String currencyFormatID(BigDecimal n) {
        Locale locale = new Locale("id", "ID");
        DecimalFormat currencyFormatter = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        DecimalFormatSymbols symbols = currencyFormatter.getDecimalFormatSymbols();

        // Add a space after the currency symbol
        symbols.setCurrencySymbol("Rp ");
        currencyFormatter.setDecimalFormatSymbols(symbols);

        return currencyFormatter.format(n);
    }

    public static boolean isDiscountProgramExpired(Date endDate) {
        Date currentDate = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateStr = dateFormat.format(currentDate);
        String endDateStr = dateFormat.format(endDate);

        return endDate != null && endDateStr.compareTo(currentDateStr) < 0;
    }

    public static String safeString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    public static Date safeDate(Object obj) {
        return obj instanceof Date ? (Date) obj : null;
    }

    public static Long safeLong(Object obj) {
        if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // kalau string-nya ga bisa diparse
            }
        }
        return null;
    }

    public static String formattedOutputCurrency(BigDecimal n) {
        Locale locale = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        return format.format(n).replace("Rp", "Rp. ");
    }

    public static Integer safeInt(Object obj) {
        if (obj == null) return null;

        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).intValue();
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else {
            try {
                return Integer.parseInt(obj.toString());
            } catch (NumberFormatException e) {
                return null; // kalau bukan angka
            }
        }
    }

    public static String clobToString(Object clob) {
        if (clob instanceof Clob) {
            try (Reader reader = ((Clob) clob).getCharacterStream();
                 StringWriter writer = new StringWriter()) {
                char[] buffer = new char[1024];
                int bytesRead;
                while ((bytesRead = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, bytesRead);
                }
                return writer.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return clob != null ? clob.toString() : null;
    }

    public static boolean bigDecimalGTZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean bigDecimalNEQZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) != 0;
    }

    public static BigDecimal safeBigDecimal(Object obj) {
        return obj instanceof BigDecimal ? (BigDecimal) obj : null;
    }

    public static boolean stringBlank( final String s ) {
        return s == null || s.trim().isEmpty();
    }

}
