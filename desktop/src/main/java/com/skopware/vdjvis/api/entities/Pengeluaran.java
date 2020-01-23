package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class Pengeluaran extends BaseRecord<Pengeluaran> {
    // table columns
    public int noSeq;
    public LocalDate tglTransaksi;
    public String penerima;
    public int nominal;
    public String keterangan;

    // relationships
    public Acara acara;
    public User user;

    public String getIdTransaksi() {
        return String.format("K/%d/%02d/%06d", tglTransaksi.getYear(), tglTransaksi.getMonthValue(), noSeq);
    }
}
