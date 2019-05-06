package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.YearMonth;

public class DetilPembayaranDanaRutin extends BaseRecord<DetilPembayaranDanaRutin> {
    // table columns
    @JsonProperty public Type jenis;
    @JsonProperty public YearMonth untukBulan;
    @JsonProperty public int nominal;

    // relationships
    @JsonProperty public PembayaranDanaRutin parentTrx;
    @JsonProperty public PendaftaranDanaRutin danaRutin;
    @JsonProperty public Leluhur leluhurSamanagara;

    public enum Type {
        sosial(), tetap(), samanagara;
    }
}
