package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.Null;
import java.time.LocalDate;

public class Leluhur extends BaseRecord<Leluhur> {
    @JsonProperty
    @NotNull
    public String nama;

    @JsonProperty
    @Nullable
    public String tempatLahir;

    @JsonProperty
    @Nullable
    public LocalDate tglLahir;

    @JsonProperty
    @Nullable
    public String tempatMati;

    @JsonProperty
    @Nullable
    public LocalDate tglMati;

    @JsonProperty
    @NotNull
    public String hubunganDgnUmat;

    @JsonProperty
    @NotNull
    public LocalDate tglDaftar;

    @JsonProperty
    public String umatId;

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

    public String getUmatId() {
        return umatId;
    }

    public void setUmatId(String umatId) {
        this.umatId = umatId;
    }
}
