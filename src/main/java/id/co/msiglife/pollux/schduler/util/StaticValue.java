package id.co.msiglife.pollux.schduler.util;

public class StaticValue {

	public final static String SECRET_KEY_LOGIN_TOKEN = "rdwPjGQK1IiCEd2QinzcwZJNTne3DbtN";
	public final static String EXP_TIME_LOGIN_TOKEN = "86400000"; // 1 day in millisecond
	public final static String BY_VIEWER = "POLLUX Viewer";
	public final static String BY_COORDINATOR = "POLLUX Coordinator";
	public final static String BY_UNDERWRITER = "POLLUX Underwriter";
	public final static String BY_LEADER= "POLLUX Team Leader";
	public final static String BY_DEPTHEAD= "POLLUX Dept Head";
	public final static Integer LEADER = 49;
	public final static Integer DEPTHEAD= 50;
	public final static Integer SURAT_KONFIRMASI_AWAL_REMINDER_DAYS = 7;
	public final static Integer TUNGGAKAN_PREMI_REMINDER_DAYS = 14;
	public final static Integer SERTIFIKAT_AKAN_DIBATALKAN_REMINDER_DAYS = 21;
	public final static Integer SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS = 36;
	public final static Integer SERTIFIKAT_DIBATALKAN_DAYS = 61;

	public final static Integer STATUS_WORKSHEET_INFORCED = 5;
	public final static Integer STATUS_WORKSHEET_DECLINED = 6;
	public final static Integer STATUS_WORKSHEET_POSTPONED = 7;
	public final static Integer STATUS_WORKSHEET_APPROVED = 11;

	public final static Integer SURAT_KONFIRMASI_AWAL_REMINDER_TYPE = 1;
	public final static Integer TUNGGAKAN_PREMI_REMINDER_TYPE = 2;
	public final static Integer SERTIFIKAT_AKAN_DIBATALKAN_REMINDER_TYPE = 3;
	public final static Integer SERTIFIKAT_DIBATALKAN_NOTIFICATION_TYPE = 4;
	public final static Integer SERTIFIKAT_DIBATALKAN_TYPE = 5;
	public final static Integer SERTIFIKAT_DIBATALKAN_NOTIFICATION_WITHOUT_EMAIL_PDF_TYPE = 6;
}
