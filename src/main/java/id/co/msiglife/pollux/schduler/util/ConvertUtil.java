package id.co.msiglife.pollux.schduler.util;

import java.text.SimpleDateFormat;
import java.util.Date;

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

	public static boolean isDiscountProgramExpired(Date endDate) {
		Date currentDate = new Date();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currentDateStr = dateFormat.format(currentDate);
		String endDateStr = dateFormat.format(endDate);

		return endDate != null && endDateStr.compareTo(currentDateStr) < 0;
	}

	public static boolean stringBlank( final String s ) {
		return s == null || s.trim().isEmpty();
	}

}
