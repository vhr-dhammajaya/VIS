package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Umat extends BaseRecord<Umat> {
    //#region table columns
    @JsonProperty public String nama;
    @JsonProperty public String alamat;
    @JsonProperty public String kota;
    @JsonProperty public String kodePos;
    @JsonProperty public String noTelpon;
    @JsonProperty public String email;
    @JsonProperty public String tempatLahir;
    @JsonProperty public LocalDate tglLahir;
    @JsonProperty public String golDarah;
    @JsonProperty public String jenisKelamin;
    @JsonProperty public String statusNikah;

    @JsonProperty public String pendidikanTerakhir;
    @JsonProperty public String jurusan;

    @JsonProperty public String pekerjaan;
    @JsonProperty public String bidangUsaha;

    @JsonProperty public String namaKerabat;
    @JsonProperty public String alamatKerabat;
    @JsonProperty public String kotaKerabat;
    @JsonProperty public String kodePosKerabat;
    @JsonProperty public String noTelpKerabat;

    @JsonProperty public String namaUpasaka;
    @JsonProperty public String penahbis;
    @JsonProperty public LocalDate tglPenahbisan;

    @JsonProperty public String idBarcode;
    @JsonProperty public LocalDate tglDaftar;
    //#endregion

    //#region relationships
    @JsonProperty public Map<String, Leluhur> mapLeluhurSamanagara = new LinkedHashMap<>();
    @JsonProperty public Map<String, PendaftaranDanaRutin> mapPendaftaranDanaRutin = new LinkedHashMap<>();
    @JsonProperty public Map<String, Pendapatan> mapDanaSukarela = new LinkedHashMap<>();
    //#endregion

    @Override
    public String toUiString() {
        return nama;
    }
}
