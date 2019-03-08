package com.skopware.vdjvis.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.Period;

public class DtoOutputLaporanAbsensi {
    @JsonProperty public String namaUmat;
    @JsonProperty public String alamat;
    @JsonProperty public String noTelpon;

    @JsonProperty public LocalDate tglTerakhirHadir;
    @JsonProperty public Period sdhBerapaLamaAbsen;
}
