package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;
import com.skopware.javautils.db.DbRecord;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PembayaranDanaRutin extends BaseRecord<PembayaranDanaRutin> {
    // table columns
    @JsonProperty public Type tipe;
    @JsonProperty public LocalDate tgl;
    @JsonProperty public int noSeq;
    @JsonProperty public int totalNominal;
    @JsonProperty public String channel;
    @JsonProperty public String keterangan;
    @JsonProperty public boolean correctionStatus;
    @JsonProperty public String correctionRequestReason;

    // relationships
    @JsonProperty public Umat umat;
    @JsonProperty public Map<String, DetilPembayaranDanaRutin> mapDetilPembayaran = new LinkedHashMap<>();

    @JsonIgnore
    public String getIdTransaksi() {
        String sb;
        if (tipe == Type.samanagara) {
            sb = ("M/SMNGR/");
        }
        else {
            sb = ("M/ST/");
        }

        sb += (String.format("%d/%02d/%06d", tgl.getYear(), tgl.getMonthValue(), noSeq));
        return sb;
    }

    public enum Type {
        samanagara, sosial_tetap;
    }
}
