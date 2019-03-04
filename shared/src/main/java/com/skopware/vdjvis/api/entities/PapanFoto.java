package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import javax.swing.*;

public class PapanFoto extends BaseRecord<PapanFoto> {
    //#region table columns
    @JsonProperty public String nama;
    @JsonProperty public int width;
    @JsonProperty public int height;
    //#endregion

    //#region relationships
    @JsonProperty public CellFoto[][] arrCellFoto;
    //#endregion

    @JsonIgnore public JTable jTable;

    public void setDimension(int w, int h) {
        this.width = w;
        this.height = h;
        arrCellFoto = new CellFoto[height][width];
    }
}
