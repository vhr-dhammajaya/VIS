package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.db.BaseRecord;

import javax.swing.*;

public class PapanFoto extends BaseRecord<PapanFoto> {
    //#region table columns
    public String nama;
    public int width;
    public int height;
    //#endregion

    //#region relationships
    public CellFoto[][] arrCellFoto;
    //#endregion

    public JTable jTable;

    public void setDimension(int w, int h) {
        this.width = w;
        this.height = h;
        arrCellFoto = new CellFoto[height][width];
    }
}
