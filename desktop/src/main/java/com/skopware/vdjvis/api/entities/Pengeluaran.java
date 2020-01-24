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
    public String idTrx;

    // relationships
    public Acara acara;
    public User user;
}
