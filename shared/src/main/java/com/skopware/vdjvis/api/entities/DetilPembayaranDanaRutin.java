package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;
import java.time.YearMonth;

public class DetilPembayaranDanaRutin extends BaseRecord<DetilPembayaranDanaRutin> {
    // table columns
    @JsonProperty public PendaftaranDanaRutin.Type jenis;
    @JsonProperty public YearMonth untukBulan;
    @JsonProperty public int nominal;

    // relationships
    @JsonProperty public PembayaranDanaRutin parentTrx;
    @JsonProperty public PendaftaranDanaRutin danaRutin;
    @JsonProperty public Leluhur leluhurSamanagara;

    @JsonIgnore
    public String getKeperluanDana() {
        StringBuilder sb = new StringBuilder();

        if (leluhurSamanagara != null) {
            // samanagara
            return String.format("Iuran samanagara untuk leluhur %s bulan %d tahun %d", leluhurSamanagara.nama, untukBulan.getMonthValue(), untukBulan.getYear());
        }
        else if (danaRutin != null) {
            // dana sosial / tetap
            return String.format("Dana %s untuk bulan %d tahun %d", jenis.name(), untukBulan.getMonthValue(), untukBulan.getYear());
        }
        else {
            throw new IllegalStateException();
        }
    }
}
