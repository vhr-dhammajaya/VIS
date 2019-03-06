package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.Tuple3;
import com.skopware.javautils.db.BaseRecord;
import org.jdbi.v3.core.Handle;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Leluhur extends BaseRecord<Leluhur> {
    //#region table columns
    @JsonProperty public String nama;
    @JsonProperty public String tempatLahir;
    @JsonProperty public LocalDate tglLahir;
    @JsonProperty public String tempatMati;
    @JsonProperty public LocalDate tglMati;
    @JsonProperty public String hubunganDgnUmat;
    @JsonProperty public LocalDate tglDaftar;
    //#endregion

    //#region calculated fields
    @JsonProperty public StatusBayar statusBayar;
    //#endregion

    //#region relationships
    @JsonProperty public Umat penanggungJawab;
    @JsonProperty public CellFoto cellFoto;
    //#endregion

    public static List<StatusBayar> computeStatusBayar(Handle handle, List<Leluhur> listLeluhur, YearMonth todayMonth, List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara) {
        List<StatusBayar> result = new ArrayList<>(listLeluhur.size());

        for (Leluhur leluhur : listLeluhur) {
            StatusBayar statusBayar = computeStatusBayar(handle, leluhur, todayMonth, listTarifSamanagara);
            result.add(statusBayar);
        }

        return result;
    }

    public static StatusBayar computeStatusBayar(Handle handle, Leluhur leluhur, YearMonth todayMonth, List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara) {
        // hitung status bayar ut leluhur ini
        Optional<LocalDate> lastPayment = handle.select("select max(ut_thn_bln) from pembayaran_dana_rutin where umat_id=? and leluhur_id=?", leluhur.penanggungJawab.uuid, leluhur.uuid)
                .mapTo(LocalDate.class)
                .findFirst();
        YearMonth lastPaymentMonth;

        if (lastPayment.isPresent()) {
            lastPaymentMonth = YearMonth.from(lastPayment.get());
        }
        else {
            lastPaymentMonth = YearMonth.from(leluhur.tglDaftar).minusMonths(1);
        }

        int statusBayar = lastPaymentMonth.compareTo(todayMonth);
        String strStatusBayar;
        long diffInMonths;
        long totalRp;

        if (statusBayar < 0) {
            strStatusBayar = "Kurang bayar";
            diffInMonths = lastPaymentMonth.until(todayMonth, ChronoUnit.MONTHS);
            totalRp = hitungTotalHutangIuranSamanagara(diffInMonths, lastPaymentMonth, leluhur.tglDaftar, listTarifSamanagara);
        }
        else if (statusBayar == 0)  {
            strStatusBayar = "Tepat waktu";
            diffInMonths = 0;
            totalRp = 0;
        }
        else {
            strStatusBayar = "Lebih bayar";
            diffInMonths = todayMonth.until(lastPaymentMonth, ChronoUnit.MONTHS);
            totalRp = 0;
        }

        StatusBayar result = new StatusBayar();
        result.status = statusBayar;
        result.strStatus = strStatusBayar;
        result.countBulan = diffInMonths;
        result.nominal = totalRp;
        return result;
    }

    public static List<Tuple3<LocalDate, LocalDate, Integer>> fetchListTarifSamanagara(Handle handle) {
        List<Tuple3<LocalDate, LocalDate, Integer>> result = handle.select("select start_date, end_date, nominal from hist_biaya_smngr order by start_date desc")
                .map((rs, ctx) -> {
                    Tuple3<LocalDate, LocalDate, Integer> x = new Tuple3<>();
                    x.val1 = DateTimeHelper.toLocalDate(rs.getDate("start_date"));
                    x.val2 = DateTimeHelper.toLocalDate(rs.getDate("end_date"));
                    x.val3 = rs.getInt("nominal");
                    return x;
                })
                .list();
        return result;
    }

    public static long hitungTotalHutangIuranSamanagara(long berapaBulan, YearMonth lastPaymentMonth, LocalDate tglDaftar, List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara) {
        long totalRp = 0;

        for (int i = 1; i <= berapaBulan; i++) {
            YearMonth currYm = lastPaymentMonth.plusMonths(i);
            LocalDate currDate = tglDaftar.withYear(currYm.getYear()).withMonth(currYm.getMonthValue());

            // hitung jumlah yg harus dibayarkan bulan ini (lihat listTarifSamanagara)
            int nominalBulanIni = DateTimeHelper.findValueInDateRange(currDate, listTarifSamanagara).get();
            totalRp += nominalBulanIni;
        }

        return totalRp;
    }
}
