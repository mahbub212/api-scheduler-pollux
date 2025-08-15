package id.co.msiglife.pollux.schduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_UNDERWRITING_PLX", schema = "EKA")
public class MasterUnderwriting {

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "underwriting_plx_seq")
//    @SequenceGenerator(name = "underwriting_plx_seq", sequenceName = "\"EKA\".\"ISEQ$$_692331\"", allocationSize = 1)
    @Column(name = "UW_ID")
    private Long uwId;

    @Column(name = "REG_SPAJ")
    private String regSpaj;

    @Column(name = "INSURED_NAME")
    private String insuredName;

    @Column(name = "PRODUCT")
    private String product;

    @Column(name = "DOB")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date dob;

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "GENDER")
    private Integer gender;

    @Column(name = "SUM_ASSURED")
    private BigDecimal sumAssured;

    @Column(name = "PREMIUM")
    private BigDecimal premium;

    @Column(name = "TENOR")
    private Integer tenor;

    @Column(name = "TOTAL_SAR")
    private BigDecimal totalSar;

    @Lob
    @Column(name = "PREVIOUS_POLICY", columnDefinition = "CLOB")
    private String previousPolicy;

    @Lob
    @Column(name = "CLAIM_HISTORY", columnDefinition = "CLOB")
    private String claimHistory;

    @Column(name = "OCCUPATION")
    private String occupation;

    @Column(name = "SOURCE_INCOME")
    private String sourceIncome;

    @Column(name = "ANNUAL_INCOME")
    private String annualIncome;

    @Column(name = "BANK_PARTNER")
    private String bankPartner;

    @Column(name = "AO_NAME")
    private String aoName;

    @Column(name = "LEADER_NAME")
    private String leaderName;

    @Lob
    @Column(name = "CFQ", columnDefinition = "CLOB")
    private String cfq;

    @Column(name = "HOBBY_HABITS")
    private String hobby;

    @Column(name = "BMI")
    private Double bmi;

    @Column(name = "SMOKING_INFO")
    private String smokingInfo;

    @Column(name = "FAMILY_HISTORY")
    private String familyHistory;

    @Column(name = "PAST_MEDICAL_HISTORY")
    private String pastMedicalHistory;

    @Column(name = "PRESENT_MEDICAL_HISTORY")
    private String presentMedicalHistory;

    @Column(name = "MCU_DATE_PROVIDER")
    private String mcuDateProvider;

    @Lob
    @Column(name = "PRESENT_ABNORMAL_FINDINGS", columnDefinition = "CLOB")
    private String presentAbnormalFindings;

    @Lob
    @Column(name = "PRESENT_NORMAL_FINDINGS", columnDefinition = "CLOB")
    private String presentNormalFindings;

    @Lob
    @Column(name = "SUMMARY_FINDINGS", columnDefinition = "CLOB")
    private String summaryFindings;

    @Column(name = "REINSURANCE_DECISION")
    private String reinsuranceDecision;

    @Lob
    @Column(name = "OTHER_INSURANCE_NOTE", columnDefinition = "CLOB")
    private String otherInsuranceNote;

    @Column(name = "USER_ID")
    private Integer userId;

    @Column(name = "DECISION_TYPE")
    private String decisionType;

    @Column(name = "FINANCIAL_STATUS")
    private String financialStatus;

    @Column(name = "MEDICAL_STATUS")
    private String medicalStatus;

    @Column(name = "SUGGESTION_TYPE")
    private String suggestionType;

    @Column(name = "REFER_TO")
    private String referTo;

    @Column(name = "FINAL_DECISION")
    private String finalDecision;

    @Column(name = "FINAL_APPROVER")
    private String finalApprover;

    @Column(name = "ACCEPTANCE_DATE")
    @JsonFormat(timezone = "Asia/Jakarta", shape = JsonFormat.Shape.STRING)
    private Date acceptanceDate;

    @Lob
    @Column(name = "FINAL_REMARK", columnDefinition = "CLOB")
    private String finalRemark;

    @Column(name = "CREATED_DATE")
    @JsonFormat(timezone = "Asia/Jakarta", shape = JsonFormat.Shape.STRING)
    private Date createdDate;

    @Column(name = "UPDATED_DATE")
    @JsonFormat(timezone = "Asia/Jakarta", shape = JsonFormat.Shape.STRING)
    private Date updatedDate;

    @Lob
    @Column(name = "PERHITUNGAN_UP")
    private String perhitunganUp;

    @Lob
    @Column(name = "PERHITUNGAN_PREMI")
    private String perhitunganPremi;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "REASON_NOTE")
    private String reasonNote;

    @Column(name = "REAPPLY_NOTE")
    private String reApplyNote;

    @Column(name = "PATH_KPS_BCA")
    private String pathKpsBca;

    @Column(name = "FILENAME_KPS_BCA")
    private String FileNameKpsBca;
}
