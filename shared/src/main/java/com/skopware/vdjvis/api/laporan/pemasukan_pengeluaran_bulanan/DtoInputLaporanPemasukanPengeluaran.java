package com.skopware.vdjvis.api.laporan.pemasukan_pengeluaran_bulanan;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.YearMonth;

public class DtoInputLaporanPemasukanPengeluaran {
    @JsonProperty public YearMonth startInclusive;
    @JsonProperty public YearMonth endInclusive;
}
