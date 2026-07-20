package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterAutoCancelEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MasterAutoCancelEmailLogRepository  extends JpaRepository<MasterAutoCancelEmailLog, String> {
    MasterAutoCancelEmailLog findByRegSpajAndReminderDays(String regSpaj, Integer reminderDays);

    MasterAutoCancelEmailLog findByRegSpajAndReminderType(String regSpaj, Integer reminderType);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE EKA.MST_AUTO_CANCEL_EMAIL_LOG  " +
            "      SET FLAG_POLIS_CANCELED = 1, UPDATED_AT = SYSDATE   " +
            "      WHERE ( EKA.MST_AUTO_CANCEL_EMAIL_LOG.REG_SPAJ IN (:regSpaj) ) AND  " +
            "        ( EKA.MST_AUTO_CANCEL_EMAIL_LOG.REMINDER_TYPE = :reminderType )",
            nativeQuery = true)
    void updatePolisCanceled(@Param("regSpaj") List<String> regSpaj, @Param("reminderType") Integer reminderType);

    @Modifying
    @Query(value = "UPDATE EKA.MST_AUTO_CANCEL_EMAIL_LOG  " +
            "      SET FLAG_DONE = 1, UPDATED_AT = SYSDATE, ME_ID = :meId   " +
            "      WHERE ( EKA.MST_AUTO_CANCEL_EMAIL_LOG.REG_SPAJ IN (:regSpaj) ) AND  " +
            "        ( EKA.MST_AUTO_CANCEL_EMAIL_LOG.REMINDER_TYPE = :reminderType )",
            nativeQuery = true)
    int updateEmailLogDone(@Param("regSpaj") String regSpaj, @Param("reminderType") Integer reminderType, @Param("meId") String meId);
}
