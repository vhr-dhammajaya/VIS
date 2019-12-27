package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class TarifSamanagara extends BaseRecord<TarifSamanagara> {
    // table columns
    public LocalDate startDate;
    public LocalDate endDate;
    public int nominal;
}
