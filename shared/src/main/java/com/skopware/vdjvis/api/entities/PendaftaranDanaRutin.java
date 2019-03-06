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
    @JsonProperty public Type tipe;
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
        Optional<LocalDate> lastPayment = handle.select("select max(ut_thn_bln) from pembayaran_dana_rutin where umat_id=? and dana_rutin_id=?", danaRutin.umat.uuid, danaRutin.uuid)
                .mapTo(LocalDate.class)
                .findFirst();
        YearMonth lastPaymentMonth;

        if (lastPayment.isPresent()) {
            lastPaymentMonth = YearMonth.from(lastPayment.get());
        }
        else {
            // have never paid yet
            lastPaymentMonth = YearMonth.from(danaRutin.tglDaftar).minusMonths(1);
        }

        int statusByr = lastPaymentMonth.compareTo(todayMonth); // 0=tepat waktu, -1=kurang bayar, 1=lebih bayar
        String jenisDana = danaRutin.tipe.name();
        String strStatusBayar;
        long diffInMonths;
        long totalRp;

        if (statusByr < 0) {
            strStatusBayar = "Kurang bayar";
            diffInMonths = lastPaymentMonth.until(todayMonth, ChronoUnit.MONTHS);
            totalRp = diffInMonths * danaRutin.nominal;
        }
        else if (statusByr == 0)  {
            strStatusBayar = "Tepat waktu";
            diffInMonths = 0;
            totalRp = 0;
        }
        else {
            strStatusBayar = "Lebih bayar";
            diffInMonths = todayMonth.until(lastPaymentMonth, ChronoUnit.MONTHS);
            totalRp = 0;
        }

        StatusBayar statusBayar = new StatusBayar();
        statusBayar.status = statusByr;
        statusBayar.strStatus = strStatusBayar;
        statusBayar.countBulan = diffInMonths;
        statusBayar.nominal = totalRp;

        return statusBayar;
    }

    public enum Type {
        sosial(), tetap(), samanagara;
    }
}
