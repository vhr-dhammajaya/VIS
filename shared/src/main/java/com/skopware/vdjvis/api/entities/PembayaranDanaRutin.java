package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;
import com.skopware.javautils.db.DbRecord;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PembayaranDanaRutin extends BaseRecord<PembayaranDanaRutin> {
    // table columns
    @JsonProperty public LocalDate tgl;
    @JsonProperty public int totalNominal;
    @JsonProperty public String channel;
    @JsonProperty public String keterangan;

    // relationships
    @JsonProperty public Umat umat;
    @JsonProperty public Map<String, DetilPembayaranDanaRutin> mapDetilPembayaran = new HashMap<>();
}
