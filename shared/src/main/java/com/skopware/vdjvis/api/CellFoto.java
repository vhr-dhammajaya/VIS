package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

public class CellFoto extends BaseRecord<CellFoto> {
    @JsonProperty public int row;
    @JsonProperty public int col;
    @JsonProperty public String papanId;

    @JsonProperty public Leluhur leluhur;

    @JsonIgnore public PapanFoto papan;

//    public String computeLabel() {
//        return String.format("%s %02d", papan.nama, col*papan.width + col + 1);
//    }
}
