package com.skopware.vdjvis.api.dto.laporan;

public class DtoOutputLaporanPemasukanPengeluaranHarian {
    //#region common
    public int nominalMasuk;
    public int nominalKeluar;
    public String keterangan;
    public String namaAcara;
    //#endregion

    //#region dana masuk
    public String jenisDanaMasuk;
    public String channel;
    public String namaDonatur;
    //#endregion

    //#region dana keluar
    public String dibayarkanKepada;
    //#endregion
}
