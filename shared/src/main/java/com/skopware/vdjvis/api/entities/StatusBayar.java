package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.YearMonth;

public class StatusBayar {
    @JsonProperty public int status;
    @JsonProperty public String strStatus;
    @JsonProperty public YearMonth lastPaidMonth;
    @JsonProperty public int countBulan;
    @JsonProperty public int nominal;
}
