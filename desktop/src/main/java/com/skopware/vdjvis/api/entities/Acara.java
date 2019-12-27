package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.db.BaseRecord;

import java.util.LinkedHashMap;
import java.util.Map;

public class Acara extends BaseRecord<Acara> {
    //#region table columns
    public String nama;
    public int noUrut;
    //#endregion

    //#region relationships
    public Map<String, Pendapatan> mapPendapatan = new LinkedHashMap<>();
    public Map<String, Pengeluaran> mapPengeluaran = new LinkedHashMap<>();
    //#endregion

    @Override
    public String toUiString() {
        return nama;
    }
}
