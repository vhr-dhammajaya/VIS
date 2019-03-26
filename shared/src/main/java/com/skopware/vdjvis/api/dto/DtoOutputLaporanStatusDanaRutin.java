package com.skopware.vdjvis.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;

public class DtoOutputLaporanStatusDanaRutin {
    @JsonProperty public String namaUmat;
    @JsonProperty public String noTelpon;
    @JsonProperty public String alamat;

    @JsonProperty public PendaftaranDanaRutin.Type jenisDana;
    @JsonProperty public String namaLeluhur;

    @JsonProperty public int statusBayar;
    @JsonProperty public String strStatusBayar;
    @JsonProperty public int countBulan;
    @JsonProperty public int nominal;


}
