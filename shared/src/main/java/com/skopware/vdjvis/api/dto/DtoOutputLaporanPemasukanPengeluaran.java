package com.skopware.vdjvis.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoOutputLaporanPemasukanPengeluaran {
    @JsonProperty public int tahun;
    @JsonProperty public int bulan;
    @JsonProperty public int pemasukan;
    @JsonProperty public int pengeluaran;
}
