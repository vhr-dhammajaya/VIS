package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;
import java.time.YearMonth;

public class DetilPembayaranDanaRutin extends BaseRecord<DetilPembayaranDanaRutin> {
    // table columns
    @JsonProperty public PendaftaranDanaRutin.Type jenis;
    @JsonProperty public YearMonth untukBulan;
    @JsonProperty public int nominal;
    @JsonProperty public LocalDate tglTrans;
    @JsonProperty public String channel;
    @JsonProperty public String keterangan;
    @JsonProperty public boolean correctionStatus;
    @JsonProperty public String correctionRequestReason;

    // relationships
    @JsonProperty public Umat umat;
    @JsonProperty public PendaftaranDanaRutin danaRutin;
    @JsonProperty public Leluhur leluhurSamanagara;
}
