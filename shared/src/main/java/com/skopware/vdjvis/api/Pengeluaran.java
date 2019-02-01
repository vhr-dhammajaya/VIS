package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;
import com.skopware.javautils.db.DbRecord;

import java.time.LocalDate;

public class Pengeluaran extends BaseRecord<Pengeluaran> {
    @JsonProperty public LocalDate tglTransaksi;
    @JsonProperty public int nominal;
    @JsonProperty public String keterangan;

    @JsonProperty public String acaraId;
    @JsonProperty public Acara acara;

    @Override
    public String toUiString() {
        return "";
    }

    public LocalDate getTglTransaksi() {
        return tglTransaksi;
    }

    public void setTglTransaksi(LocalDate tglTransaksi) {
        this.tglTransaksi = tglTransaksi;
    }

    public int getNominal() {
        return nominal;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getAcaraId() {
        return acaraId;
    }

    public void setAcaraId(String acaraId) {
        this.acaraId = acaraId;
    }

    public Acara getAcara() {
        return acara;
    }

    public void setAcara(Acara acara) {
        this.acara = acara;
        this.acaraId = acara==null? null : acara.getUuid();
    }
}
