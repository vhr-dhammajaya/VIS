package com.skopware.vdjvis.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import com.skopware.vdjvis.api.entities.StatusBayar;

public class DtoOutputLaporanStatusDanaRutin {
    @JsonProperty public String namaUmat;
    @JsonProperty public String noTelpon;
    @JsonProperty public String alamat;

    @JsonProperty public PendaftaranDanaRutin.Type jenisDana;
    @JsonProperty public String namaLeluhur;

    @JsonProperty public StatusBayar statusBayar;
}
