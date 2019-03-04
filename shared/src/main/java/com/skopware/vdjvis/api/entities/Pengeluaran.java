package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class Pengeluaran extends BaseRecord<Pengeluaran> {
    // table columns
    @JsonProperty public LocalDate tglTransaksi;
    @JsonProperty public int nominal;
    @JsonProperty public String keterangan;

    // relationships
    @JsonProperty public Acara acara;
}
