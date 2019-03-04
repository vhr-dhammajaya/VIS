package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class PendaftaranDanaRutin extends BaseRecord<PendaftaranDanaRutin> {
    //#region table columns
    @JsonProperty public LocalDate tglDaftar = LocalDate.now();
    @JsonProperty public int nominal;
    @JsonProperty public Type tipe;
    //#endregion

    // relationships
    @JsonProperty public Umat umat;

    public enum Type {
        sosial(), tetap(), samanagara;
    }
}
