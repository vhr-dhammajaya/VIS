package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class Pengeluaran extends BaseRecord<Pengeluaran> {
    // table columns
    @JsonProperty public int noSeq;
    @JsonProperty public LocalDate tglTransaksi;
    @JsonProperty public String penerima;
    @JsonProperty public int nominal;
    @JsonProperty public String keterangan;

    // relationships
    @JsonProperty public Acara acara;

    @JsonIgnore
    public String getIdTransaksi() {
        return String.format("K/%d/%02d/%06d", tglTransaksi.getYear(), tglTransaksi.getMonthValue(), noSeq);
    }
}
