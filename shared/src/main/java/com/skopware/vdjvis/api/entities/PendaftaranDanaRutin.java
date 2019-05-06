package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;
import org.jdbi.v3.core.Handle;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PendaftaranDanaRutin extends BaseRecord<PendaftaranDanaRutin> {
    //#region table columns
    @JsonProperty public LocalDate tglDaftar = LocalDate.now();
    @JsonProperty public int nominal;
    @JsonProperty public DetilPembayaranDanaRutin.Type tipe;
    //#endregion

    // calculated fields
    @JsonProperty public StatusBayar statusBayar;

    // relationships
    @JsonProperty public Umat umat;

    public static List<StatusBayar> computeStatusBayar(Handle handle, List<PendaftaranDanaRutin> listDanaRutin, YearMonth todayMonth) {
        List<StatusBayar> result = new ArrayList<>();

        for (PendaftaranDanaRutin danaRutin : listDanaRutin) {
            StatusBayar statusBayar = computeStatusBayar(handle, danaRutin, todayMonth);
            result.add(statusBayar);
        }

        return result;
    }

    public static StatusBayar computeStatusBayar(Handle handle, PendaftaranDanaRutin danaRutin, YearMonth todayMonth) {
        YearMonth lastPaidMonth = fetchLastPaidMonth(handle, danaRutin.umat.uuid, danaRutin.uuid, danaRutin.tglDaftar);

        int statusByr = lastPaidMonth.compareTo(todayMonth); // 0=tepat waktu, -1=kurang bayar, 1=lebih bayar
        String strStatusBayar;
        int diffInMonths;
        int totalRp;

        if (statusByr < 0) {
            strStatusBayar = "Kurang bayar";
            diffInMonths = (int) lastPaidMonth.until(todayMonth, ChronoUnit.MONTHS);
            totalRp = diffInMonths * danaRutin.nominal;
        }
        else if (statusByr == 0)  {
            strStatusBayar = "Tepat waktu";
            diffInMonths = 0;
            totalRp = 0;
        }
        else {
            strStatusBayar = "Lebih bayar";
            diffInMonths = (int) todayMonth.until(lastPaidMonth, ChronoUnit.MONTHS);
            totalRp = 0;
        }

        StatusBayar statusBayar = new StatusBayar();
        statusBayar.status = statusByr;
        statusBayar.strStatus = strStatusBayar;
        statusBayar.lastPaidMonth = lastPaidMonth;
        statusBayar.countBulan = diffInMonths;
        statusBayar.nominal = totalRp;

        return statusBayar;
    }

    public static YearMonth fetchLastPaidMonth(Handle handle, String umatId, String danaRutinId, LocalDate tglDaftar) {
        Optional<LocalDate> lastPayment = handle.select("select max(d.ut_thn_bln) from" +
                " detil_pembayaran_dana_rutin d" +
                " join pembayaran_samanagara_sosial_tetap p on p.uuid = d.trx_id" +
                " where p.active=1 and p.umat_id=? and d.dana_rutin_id=?", umatId, danaRutinId)
                .mapTo(LocalDate.class)
                .findFirst();
        if (lastPayment.isPresent()) {
            return YearMonth.from(lastPayment.get());
        }
        else {
            return YearMonth.from(tglDaftar).minusMonths(1);
        }
    }

}
