package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusBayar {
    @JsonProperty public int status;
    @JsonProperty public String strStatus;
    @JsonProperty public long countBulan;
    @JsonProperty public long nominal;
}
