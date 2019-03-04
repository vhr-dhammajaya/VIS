package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class TarifSamanagara extends BaseRecord<TarifSamanagara> {
    // table columns
    @JsonProperty public LocalDate startDate;
    @JsonProperty public LocalDate endDate;
    @JsonProperty public int nominal;
}
