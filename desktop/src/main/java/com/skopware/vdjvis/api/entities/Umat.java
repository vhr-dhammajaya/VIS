package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class Umat extends BaseRecord<Umat> {
    //#region table columns
    public String nama;
    public String alamat;
    public String kota;
    public String kodePos;
    public String noTelpon;
    public String email;
    public String tempatLahir;
    public LocalDate tglLahir;
    public String golDarah;
    public String jenisKelamin;
    public String statusNikah;

    public String pendidikanTerakhir;
    public String jurusan;

    public String pekerjaan;
    public String bidangUsaha;

    public String namaKerabat;
    public String alamatKerabat;
    public String kotaKerabat;
    public String kodePosKerabat;
    public String noTelpKerabat;

    public String namaUpasaka;
    public String penahbis;
    public LocalDate tglPenahbisan;

    public String idBarcode;
    public LocalDate tglDaftar;
    //#endregion

    //#region relationships
    public Map<String, Leluhur> mapLeluhurSamanagara = new LinkedHashMap<>();
    public Map<String, PendaftaranDanaRutin> mapPendaftaranDanaRutin = new LinkedHashMap<>();
    public Map<String, Pendapatan> mapDanaSukarela = new LinkedHashMap<>();
    //#endregion

    @Override
    public String toUiString() {
        return nama;
    }
}
