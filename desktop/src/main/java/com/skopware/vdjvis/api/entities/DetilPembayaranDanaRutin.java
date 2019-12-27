package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.db.BaseRecord;

import java.time.YearMonth;

public class DetilPembayaranDanaRutin extends BaseRecord<DetilPembayaranDanaRutin> {
    // table columns
    public Type jenis;
    public YearMonth untukBulan;
    public int nominal;

    // relationships
    public PembayaranDanaRutin parentTrx;
    public PendaftaranDanaRutin danaRutin;
    public Leluhur leluhurSamanagara;

    public enum Type {
        sosial(), tetap(), samanagara;
    }
}
