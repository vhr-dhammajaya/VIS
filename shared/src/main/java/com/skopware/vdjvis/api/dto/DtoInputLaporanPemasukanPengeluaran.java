package com.skopware.vdjvis.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.YearMonth;

public class DtoInputLaporanPemasukanPengeluaran {
    @JsonProperty public YearMonth startInclusive;
    @JsonProperty public YearMonth endInclusive;
}
