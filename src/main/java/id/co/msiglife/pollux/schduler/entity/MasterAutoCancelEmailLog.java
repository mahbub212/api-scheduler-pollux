package id.co.msiglife.pollux.schduler.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MST_AUTO_CANCEL_EMAIL_LOG", schema = "EKA")
public class MasterAutoCancelEmailLog {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "LOG_ID")
        private Long logId;

        @Column(name = "ME_ID", nullable = true, length = 14)
        private String meId;

        @Column(name = "REG_SPAJ", nullable = false, length = 50)
        private String regSpaj;

        @Column(name = "PATH_ATTACH", nullable = true, length = 1000)
        private String pathAttach;

        @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP DEFAULT SYSTIMESTAMP")
        private Date createdAt;

        @Column(name = "UPDATED_AT")
        private Date updatedAt;

        @Column(name = "REMINDER_DAYS", nullable = false, length = 3)
        private Integer reminderDays;

        @Column(name = "FLAG_DONE", nullable = false, length = 3)
        private Integer flagDone;

}
