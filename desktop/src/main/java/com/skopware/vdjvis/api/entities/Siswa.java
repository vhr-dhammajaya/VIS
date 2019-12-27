package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class Siswa extends BaseRecord<Siswa> {
    // table columns
    public String nama;

    public String namaAyah;
    public String namaIbu;

    public String alamat;
    public String kota;

    public String noTelpon;

    public String tempatLahir;
    public LocalDate tglLahir;

    public String sekolah;
    public String alamatSekolah;

    public String idBarcode;
    public LocalDate tglDaftar;
}
