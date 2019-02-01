package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;
import com.skopware.javautils.db.DbRecord;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pendapatan extends BaseRecord<Pendapatan> {
    @JsonProperty public String umatId;
    @JsonProperty public Umat umat;
    @JsonProperty public LocalDate tglTransaksi;
    @JsonProperty public int nominal;
    @JsonProperty public String channel;
    @JsonProperty public JenisDana jenisDana;
    @JsonProperty public String keterangan;
    @JsonProperty public String acaraId;
    @JsonProperty public Acara acara;

    public enum JenisDana {
        IURAN_SAMANAGARA("Iuran samanagara"), DANA_SOSIAL("Dana sosial"), DANA_TETAP("Dana tetap"), DANA_SUKARELA("Dana sukarela"), DANA_PARAMITTA("Dana paramitta"), HAPPY_SUNDAY("Happy Sunday"), KOTAK_DANA("Kotak dana"), LAIN_2("Lain-2");

        public String label;

        JenisDana(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public String toUiString() {
        return "";
    }

    public String getUmatId() {
        return umatId;
    }

    public void setUmatId(String umatId) {
        this.umatId = umatId;
    }

    public String getAcaraId() {
        return acaraId;
    }

    public void setAcaraId(String acaraId) {
        this.acaraId = acaraId;
    }

    public Umat getUmat() {
        return umat;
    }

    public void setUmat(Umat umat) {
        this.umat = umat;
        umatId = umat == null? null : umat.uuid;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public JenisDana getJenisDana() {
        return jenisDana;
    }

    public void setJenisDana(JenisDana jenisDana) {
        this.jenisDana = jenisDana;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Acara getAcara() {
        return acara;
    }

    public void setAcara(Acara acara) {
        this.acara = acara;
        acaraId = acara == null? null : acara.uuid;
    }
}
