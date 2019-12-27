package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.db.BaseRecord;

public class CellFoto extends BaseRecord<CellFoto> {
    //#region table columns
    public int row;
    public int col;
    //#endregion

    //#region relationships
    public Leluhur leluhur;
    public PapanFoto papan;
    //#endregion
}
