package com.skopware.vdjvis.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.vdjvis.api.entities.StatusBayar;

import java.time.LocalDate;
import java.time.YearMonth;

public class DtoStatusBayarLeluhur {
    @JsonProperty public String leluhurId;
    @JsonProperty public String leluhurNama;
    @JsonProperty public LocalDate leluhurTglDaftar;

    @JsonProperty public StatusBayar statusBayar;

    @JsonProperty public int mauBayarBrpBulan = 0;
    @JsonProperty public int nominalYgMauDibayarkan;
}
