package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import javax.swing.*;
import java.util.List;

public class PapanFoto extends BaseRecord<PapanFoto> {
    @JsonProperty public String nama;
    @JsonProperty public int width;
    @JsonProperty public int height;

    @JsonIgnore public CellFoto[][] arrCellFoto;
    @JsonIgnore public JTable jTable;

    public void initCells() {
        arrCellFoto = new CellFoto[height][width];
    }
}
