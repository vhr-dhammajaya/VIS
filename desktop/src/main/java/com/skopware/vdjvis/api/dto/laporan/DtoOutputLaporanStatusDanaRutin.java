package com.skopware.vdjvis.api.dto.laporan;

import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.StatusBayar;

public class DtoOutputLaporanStatusDanaRutin {
    public String namaUmat;
    public String noTelpon;
    public String alamat;

    public DetilPembayaranDanaRutin.Type jenisDana;
    public String namaLeluhur;

    public StatusBayar statusBayar;
}
