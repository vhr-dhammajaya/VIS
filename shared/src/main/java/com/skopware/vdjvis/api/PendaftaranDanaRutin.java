package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class PendaftaranDanaRutin extends BaseRecord<PendaftaranDanaRutin> {
    @JsonProperty public LocalDate tglDaftar = LocalDate.now();
    @JsonProperty public int nominal;
    @JsonProperty public Type tipe;
    @JsonProperty public String umatId;
    @JsonProperty public Umat umat;

    @Override
    public String toUiString() {
        return "";
    }

    public enum Type {
        sosial(), tetap(), samanagara;
    }

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

    public Type getTipe() {
        return tipe;
    }

    public void setTipe(Type tipe) {
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
