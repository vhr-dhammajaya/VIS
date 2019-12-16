package com.skopware.vdjvis.api.dto.laporan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.StatusBayar;

public class DtoOutputLaporanStatusDanaRutin {
    @JsonProperty public String namaUmat;
    @JsonProperty public String noTelpon;
    @JsonProperty public String alamat;

    @JsonProperty public DetilPembayaranDanaRutin.Type jenisDana;
    @JsonProperty public String namaLeluhur;

    @JsonProperty public StatusBayar statusBayar;
}
