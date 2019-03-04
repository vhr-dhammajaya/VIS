package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class Leluhur extends BaseRecord<Leluhur> {
    //#region table columns
    @JsonProperty public String nama;
    @JsonProperty public String tempatLahir;
    @JsonProperty public LocalDate tglLahir;
    @JsonProperty public String tempatMati;
    @JsonProperty public LocalDate tglMati;
    @JsonProperty public String hubunganDgnUmat;
    @JsonProperty public LocalDate tglDaftar;
    //#endregion

    //#region relationships
    @JsonProperty public Umat penanggungJawab;
    @JsonProperty public CellFoto cellFoto;
    //#endregion
}
