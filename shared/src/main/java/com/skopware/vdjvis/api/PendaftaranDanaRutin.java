package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class PendaftaranDanaRutin extends BaseRecord<PendaftaranDanaRutin> {
    @JsonProperty public LocalDate tglDaftar = LocalDate.now();
    @JsonProperty public int nominal;
    @JsonProperty public String tipe;
    @JsonProperty public String umatId;
    @JsonProperty public Umat umat;

    public LocalDate getTglDaftar() {
        return tglDaftar;
    }

    public void setTglDaftar(LocalDate tglDaftar) {
        this.tglDaftar = tglDaftar;
    }

    public int getNominal() {
        return nominal;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getUmatId() {
        return umatId;
    }

    public void setUmatId(String umatId) {
        this.umatId = umatId;
    }

    public Umat getUmat() {
        return umat;
    }

    public void setUmat(Umat umat) {
        this.umat = umat;
    }
}
