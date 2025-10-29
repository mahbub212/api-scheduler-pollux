package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterAutoCancelEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MasterAutoCancelEmailLogRepository  extends JpaRepository<MasterAutoCancelEmailLog, String> {
    MasterAutoCancelEmailLog findByRegSpajAndReminderDays(String regSpaj, Integer reminderDays);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE EKA.MST_AUTO_CANCEL_EMAIL_LOG  " +
            "      SET FLAG_POLIS_CANCELED = 1, UPDATED_AT = SYSDATE   " +
            "      WHERE ( EKA.MST_AUTO_CANCEL_EMAIL_LOG.REG_SPAJ IN (:regSpaj) ) AND  " +
            "        ( EKA.MST_AUTO_CANCEL_EMAIL_LOG.REMINDER_DAYS = :daysPolicyCanceled )",
            nativeQuery = true)
    void updatePolisCanceled(@Param("regSpaj") List<String> regSpaj, @Param("daysPolicyCanceled") Integer days);
}
