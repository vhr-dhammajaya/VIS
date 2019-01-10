package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;
import com.skopware.javautils.db.DbRecord;

import java.time.LocalDate;

public class Umat extends BaseRecord<Umat> {
    @JsonProperty
    public String nama;
    @JsonProperty
    public String alamat;
    @JsonProperty
    public String kota;
    @JsonProperty
    public String kodePos;
    @JsonProperty
    public String noTelpon;
    @JsonProperty
    public String email;
    @JsonProperty
    public String tempatLahir;
    @JsonProperty
    public LocalDate tglLahir;
    @JsonProperty
    public String golDarah;
    @JsonProperty
    public String jenisKelamin;
    @JsonProperty
    public String statusNikah;

    @JsonProperty
    public String pendidikanTerakhir;
    @JsonProperty
    public String jurusan;

    @JsonProperty
    public String pekerjaan;
    @JsonProperty
    public String bidangUsaha;

    @JsonProperty
    public String namaKerabat;
    @JsonProperty
    public String alamatKerabat;
    @JsonProperty
    public String kotaKerabat;
    @JsonProperty
    public String kodePosKerabat;
    @JsonProperty
    public String noTelpKerabat;

    @JsonProperty
    public String namaUpasaka;
    @JsonProperty
    public String penahbis;
    @JsonProperty
    public LocalDate tglPenahbisan;

    @JsonProperty
    public boolean active;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getKodePos() {
        return kodePos;
    }

    public void setKodePos(String kodePos) {
        this.kodePos = kodePos;
    }

    public String getNoTelpon() {
        return noTelpon;
    }

    public void setNoTelpon(String noTelpon) {
        this.noTelpon = noTelpon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getGolDarah() {
        return golDarah;
    }

    public void setGolDarah(String golDarah) {
        this.golDarah = golDarah;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getStatusNikah() {
        return statusNikah;
    }

    public void setStatusNikah(String statusNikah) {
        this.statusNikah = statusNikah;
    }

    public String getPendidikanTerakhir() {
        return pendidikanTerakhir;
    }

    public void setPendidikanTerakhir(String pendidikanTerakhir) {
        this.pendidikanTerakhir = pendidikanTerakhir;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }

    public String getBidangUsaha() {
        return bidangUsaha;
    }

    public void setBidangUsaha(String bidangUsaha) {
        this.bidangUsaha = bidangUsaha;
    }

    public String getNamaKerabat() {
        return namaKerabat;
    }

    public void setNamaKerabat(String namaKerabat) {
        this.namaKerabat = namaKerabat;
    }

    public String getAlamatKerabat() {
        return alamatKerabat;
    }

    public void setAlamatKerabat(String alamatKerabat) {
        this.alamatKerabat = alamatKerabat;
    }

    public String getKotaKerabat() {
        return kotaKerabat;
    }

    public void setKotaKerabat(String kotaKerabat) {
        this.kotaKerabat = kotaKerabat;
    }

    public String getKodePosKerabat() {
        return kodePosKerabat;
    }

    public void setKodePosKerabat(String kodePosKerabat) {
        this.kodePosKerabat = kodePosKerabat;
    }

    public String getNoTelpKerabat() {
        return noTelpKerabat;
    }

    public void setNoTelpKerabat(String noTelpKerabat) {
        this.noTelpKerabat = noTelpKerabat;
    }

    public String getNamaUpasaka() {
        return namaUpasaka;
    }

    public void setNamaUpasaka(String namaUpasaka) {
        this.namaUpasaka = namaUpasaka;
    }

    public String getPenahbis() {
        return penahbis;
    }

    public void setPenahbis(String penahbis) {
        this.penahbis = penahbis;
    }

    public LocalDate getTglPenahbisan() {
        return tglPenahbisan;
    }

    public void setTglPenahbisan(LocalDate tglPenahbisan) {
        this.tglPenahbisan = tglPenahbisan;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
