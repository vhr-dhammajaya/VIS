package com.skopware.vdjvis.api.laporan.pemasukan_pengeluaran_bulanan;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoOutputLaporanPemasukanPengeluaran {
    @JsonProperty public int tahun;
    @JsonProperty public int bulan;
    @JsonProperty public int pemasukan;
    @JsonProperty public int pengeluaran;
}
