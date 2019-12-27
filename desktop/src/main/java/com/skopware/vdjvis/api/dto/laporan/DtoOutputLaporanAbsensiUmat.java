package com.skopware.vdjvis.api.dto.laporan;

import java.time.LocalDate;
import java.time.Period;

public class DtoOutputLaporanAbsensiUmat {
    public String namaUmat;
    public String alamat;
    public String noTelpon;

    public LocalDate tglTerakhirHadir;
    public Period sdhBerapaLamaAbsen;
}
