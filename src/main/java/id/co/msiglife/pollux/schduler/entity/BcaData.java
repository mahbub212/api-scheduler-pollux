package id.co.msiglife.pollux.schduler.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.co.msiglife.pollux.schduler.composite.BcaDataId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_ORDERCL_API", schema = "EKA")
@IdClass(BcaDataId.class)
public class BcaData {

    @Id
    @Column(name = "TIPE_TRANS")
    private Integer tipeTrans;

    @Id
    @Column(name = "REFERENCE_NO")
    private String referenceNo;

    @Id
    @Column(name = "NO_URUT")
    private Integer noUrut;

    @Column(name = "NAMA_CABANG")
    private String namaCabang;

    @Column(name = "KODE_CABANG_PENGIRIM_ORDER")
    private String kodeCabangPengirimOrder;

    @Column(name = "CABANG_PENGIRIM_ORDER")
    private String cabangPengirimOrder;

    @Column(name = "PIC_BCA")
    private String picBca;

    @Column(name = "NOMOR_ID_TERTANGGUNG")
    private String nomorIdTertanggung;

    @Column(name = "NAMA_LENGKAP_TERTANGGUNG")
    private String namaLengkapTertanggung;

    @Column(name = "JENIS_KELAMIN")
    private String jenisKelamin;

    @Column(name = "ALAMAT_TERTANGGUNG_1")
    private String alamatTertanggung1;

    @Column(name = "ALAMAT_TERTANGGUNG_2")
    private String alamatTertanggung2;

    @Column(name = "ALAMAT_TERTANGGUNG_3")
    private String alamatTertanggung3;

    @Column(name = "ALAMAT_TERTANGGUNG_4")
    private String alamatTertanggung4;

    @Column(name = "KEWARGANEGARAAN")
    private String kewarganegaraan;

    @Column(name = "TELEPON_RUMAH")
    private String teleponRumah;

    @Column(name = "TELEPON_KANTOR")
    private String teleponKantor;

    @Column(name = "HANDPHONE")
    private String handphone;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TEMPAT_LAHIR")
    private String tempatLahir;

    @Column(name = "TANGGAL_LAHIR")
    private String tanggalLahir;

    @Column(name = "PEKERJAAN")
    private String pekerjaan;

    @Column(name = "COMPANY_NAME")
    private String companyName;

    @Column(name = "COMPANY_NAME_OTHER")
    private String companyNameOther;

    @Column(name = "DESIGNATION")
    private String designation;

    @Column(name = "KARYAWAN_BCA")
    private String karyawanBca;  // Sesuaikan dengan VARCHAR2

    @Column(name = "PROMOTION_PACKAGE")
    private String promotionPackage;

    @Column(name = "MASA_PERTANGGUNGAN")
    private Integer masaPertanggungan;

    @Column(name = "NILAI_PERTANGGUNGAN")
    private Double nilaiPertanggungan;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "TANGGAL_ORDER")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tanggalOrder;

    @Column(name = "PIC_ORDER")
    private String picOrder;

    @Column(name = "INSURANCE_COMPANY")
    private String insuranceCompany;

    @Column(name = "INSURANCE_COMPANY_CODE")
    private String insuranceCompanyCode;

    @Column(name = "INSURANCE_EMAIL")
    private String insuranceEmail;

    @Column(name = "RES_HTTP_CODE")
    private String resHttpCode;

    @Column(name = "RES_HTTP_STATUS")
    private String resHttpStatus;

    @Column(name = "RES_MESSAGE_CODE")
    private String resMessageCode;

    @Column(name = "RES_MESSAGE")
    private String resMessage;

    @Id
    @Column(name = "ORDER_ID")
    private String orderId;

    @Id
    @Column(name = "REG_SPAJ")
    private String regSpaj;

    @Column(name = "TGL_INSERT_API")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date tglInsertApi;

    @Column(name = "METHOD")
    private Integer method;

    @Column(name = "ORDER_IPADDRESS")
    private String orderIpaddress;

    @Column(name = "ORDER_STATUS")
    private Integer orderStatus;

    @Column(name = "ORDER_PROCESS_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Jakarta")
    private Date orderProcessDate;

    @Column(name = "ORDER_USER_PROCESS")
    private Integer orderUserProcess;

    @Column(name = "NAMA_FILE_COVERNOTE")
    private String namaFileCovernote;

    @Column(name = "NAMA_GADIS_IBU_KANDUNG")
    private String namaGadisIbuKandung;
}
