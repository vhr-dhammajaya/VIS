package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class Siswa extends BaseRecord<Siswa> {
    // table columns
    @JsonProperty public String nama;

    @JsonProperty public String namaAyah;
    @JsonProperty public String namaIbu;

    @JsonProperty public String alamat;
    @JsonProperty public String kota;

    @JsonProperty public String noTelpon;

    @JsonProperty public String tempatLahir;
    @JsonProperty public LocalDate tglLahir;

    @JsonProperty public String sekolah;
    @JsonProperty public String alamatSekolah;

    @JsonProperty public String idBarcode;
    @JsonProperty public LocalDate tglDaftar;
}
