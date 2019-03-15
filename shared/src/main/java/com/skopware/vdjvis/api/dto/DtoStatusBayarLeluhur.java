package com.skopware.vdjvis.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.YearMonth;

public class DtoStatusBayarLeluhur {
    @JsonProperty public String leluhurId;
    @JsonProperty public String leluhurNama;
    @JsonProperty public LocalDate leluhurTglDaftar;

    @JsonProperty public int statusBayar;
    @JsonProperty public String strStatusBayar;
    @JsonProperty public YearMonth lastPaymentMonth;
    @JsonProperty public long diffInMonths;
    @JsonProperty public long nominal;

    @JsonProperty public int mauBayarBrpBulan = 0;
}