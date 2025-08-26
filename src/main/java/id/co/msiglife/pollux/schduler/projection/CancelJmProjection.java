package id.co.msiglife.pollux.schduler.projection;

import java.math.BigDecimal;

public interface CancelJmProjection {
    String getNO_JM();
    Integer getNO_JURNAL();
    String getKETERANGAN();
    java.math.BigDecimal getMJM_JUMLAH();
    String getMJM_CD();
    String getMJM_PROJECT();
    String getMJM_BUDGET();
    Integer getTYPE_TRX();
    String getKEY_JURNAL();
    String getLKU_ID();
    BigDecimal getCONV_RATE();
    BigDecimal getMJM_JUMLAH_USD();
    String getNO_POLIS();
    Integer getTAXNO();
}

