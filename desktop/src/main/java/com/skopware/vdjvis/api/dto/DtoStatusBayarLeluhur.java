package com.skopware.vdjvis.api.dto;

import com.skopware.vdjvis.api.entities.StatusBayar;

import java.time.LocalDate;

public class DtoStatusBayarLeluhur {
    public String leluhurId;
    public String leluhurNama;
    public LocalDate leluhurTglDaftar;

    public StatusBayar statusBayar;

    public int mauBayarBrpBulan = 0;
    public int nominalYgMauDibayarkan;
}
