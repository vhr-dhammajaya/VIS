package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class PembayaranDanaSosialDanTetap extends BaseRecord<PembayaranDanaSosialDanTetap> {
    @JsonProperty public String idPendaftaran;
    @JsonIgnore public PendaftaranDanaRutin pendaftaran;

    @JsonProperty public LocalDate tglTrans;

    @JsonProperty public int countBulan;

    @JsonProperty public String channel;

    @JsonProperty public String keterangan;

    @Override
    public String toUiString() {
        return null;
    }

    public String getIdPendaftaran() {
        return idPendaftaran;
    }

    public void setIdPendaftaran(String idPendaftaran) {
        this.idPendaftaran = idPendaftaran;
    }

    public PendaftaranDanaRutin getPendaftaran() {
        return pendaftaran;
    }

    public void setPendaftaran(PendaftaranDanaRutin pendaftaran) {
        this.pendaftaran = pendaftaran;
    }

    public LocalDate getTglTrans() {
        return tglTrans;
    }

    public void setTglTrans(LocalDate tglTrans) {
        this.tglTrans = tglTrans;
    }

    public int getCountBulan() {
        return countBulan;
    }

    public void setCountBulan(int countBulan) {
        this.countBulan = countBulan;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
