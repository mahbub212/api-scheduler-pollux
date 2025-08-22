package id.co.msiglife.pollux.schduler.entity.Journal;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_PTC_TM", schema = "EKA")
@Data
public class MstPtcTm {

    @Id
    @Column(name = "NO_JM")
    private String noJm;

    @Column(name = "MTM_POSITION")
    private Integer position;

    @Column(name = "TGL_JURNAL")
    private Date tglJurnal;

    @Column(name = "USER_INPUT")
    private Integer userInput;

    @Column(name = "TGL_INPUT", insertable = false, updatable = false)
    private LocalDateTime tglInput;
}

