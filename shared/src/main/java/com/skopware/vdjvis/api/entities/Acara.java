package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Acara extends BaseRecord<Acara> {
    //#region table columns
    @JsonProperty public String nama;
    @JsonProperty public int noUrut;
    //#endregion

    //#region relationships
    @JsonProperty public Map<String, Pendapatan> mapPendapatan = new LinkedHashMap<>();
    @JsonProperty public Map<String, Pengeluaran> mapPengeluaran = new LinkedHashMap<>();
    //#endregion

    @Override
    public String toUiString() {
        return nama;
    }
}
