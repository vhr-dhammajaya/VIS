package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class Leluhur extends BaseRecord<Leluhur> {
    @JsonProperty public String nama;
    @JsonProperty public String tempatLahir;
    @JsonProperty public LocalDate tglLahir;
    @JsonProperty public String tempatMati;
    @JsonProperty public LocalDate tglMati;
    @JsonProperty public String hubunganDgnUmat;
    @JsonProperty public LocalDate tglDaftar;

    @JsonProperty public String penanggungJawabId;
    @JsonProperty public Umat penanggungJawab;

    @JsonProperty public String cellFotoId;

    @JsonIgnore public CellFoto cellFoto;

    @Override
    public String toUiString() {
        return "";
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }

    public LocalDate getTglLahir() {
        return tglLahir;
    }

    public void setTglLahir(LocalDate tglLahir) {
        this.tglLahir = tglLahir;
    }

    public String getTempatMati() {
        return tempatMati;
    }

    public void setTempatMati(String tempatMati) {
        this.tempatMati = tempatMati;
    }

    public LocalDate getTglMati() {
        return tglMati;
    }

    public void setTglMati(LocalDate tglMati) {
        this.tglMati = tglMati;
    }

    public String getHubunganDgnUmat() {
        return hubunganDgnUmat;
    }

    public void setHubunganDgnUmat(String hubunganDgnUmat) {
        this.hubunganDgnUmat = hubunganDgnUmat;
    }

    public LocalDate getTglDaftar() {
        return tglDaftar;
    }

    public void setTglDaftar(LocalDate tglDaftar) {
        this.tglDaftar = tglDaftar;
    }

    public String getPenanggungJawabId() {
        return penanggungJawabId;
    }

    public void setPenanggungJawabId(String penanggungJawabId) {
        this.penanggungJawabId = penanggungJawabId;
    }

    public Umat getPenanggungJawab() {
        return penanggungJawab;
    }

    public void setPenanggungJawab(Umat penanggungJawab) {
        this.penanggungJawab = penanggungJawab;
    }
}
