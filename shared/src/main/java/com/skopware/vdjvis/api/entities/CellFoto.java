package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

public class CellFoto extends BaseRecord<CellFoto> {
    //#region table columns
    @JsonProperty public int row;
    @JsonProperty public int col;
    //#endregion

    //#region relationships
    @JsonProperty public Leluhur leluhur;
    @JsonProperty public PapanFoto papan;
    //#endregion
}
