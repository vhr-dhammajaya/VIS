package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TarifSamanagara extends BaseRecord<TarifSamanagara> {
    @JsonProperty
    public LocalDate startDate;

    @JsonProperty
    public LocalDate endDate;

    @JsonProperty
    public int nominal;

    @Override
    public String toUiString() {
        return "";
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getNominal() {
        return nominal;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }
}
