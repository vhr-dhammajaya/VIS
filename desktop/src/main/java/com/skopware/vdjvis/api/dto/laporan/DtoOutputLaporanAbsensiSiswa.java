package com.skopware.vdjvis.api.dto.laporan;

import java.time.LocalDate;
import java.time.Period;

public class DtoOutputLaporanAbsensiSiswa {
    public String namaSiswa;
    public String namaAyah;
    public String namaIbu;
    public String alamat;
    public String noTelpon;

    public LocalDate tglTerakhirHadir;
    public Period sdhBerapaLamaAbsen;
}
