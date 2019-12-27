package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.Tuple3;
import com.skopware.javautils.Tuple4;
import com.skopware.javautils.db.BaseRecord;
import org.jdbi.v3.core.Handle;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class Leluhur extends BaseRecord<Leluhur> {
    //#region table columns
    public String nama;
    public String tempatLahir;
    public LocalDate tglLahir;
    public String tempatMati;
    public LocalDate tglMati;
    public String hubunganDgnUmat;
    public LocalDate tglDaftar;
    //#endregion

    //#region calculated fields
    public StatusBayar statusBayar;
    //#endregion

    //#region relationships
    public Umat penanggungJawab;
    public CellFoto cellFoto;
    //#endregion

    public static void computeStatusBayar(Handle handle, List<Leluhur> listLeluhur, YearMonth todayMonth, List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara) {
        for (Leluhur leluhur : listLeluhur) {
            StatusBayar statusBayar = computeStatusBayar(handle, leluhur, todayMonth, listTarifSamanagara);
            leluhur.statusBayar = statusBayar;
        }
    }

    public static StatusBayar computeStatusBayar(Handle handle, Leluhur leluhur, YearMonth todayMonth, List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara) {
        return computeStatusBayar(handle, leluhur.penanggungJawab.uuid, leluhur.uuid, leluhur.tglDaftar, todayMonth, listTarifSamanagara);
    }

    public static StatusBayar computeStatusBayar(Handle handle, String umatId, String leluhurId, LocalDate tglDaftar, YearMonth todayMonth, List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara) {
        // hitung status bayar ut leluhur ini
        YearMonth lastPaidMonth = fetchLastPaidMonth(handle, umatId, leluhurId, tglDaftar);

        int statusBayar = lastPaidMonth.compareTo(todayMonth);
        String strStatusBayar;
        int diffInMonths;
        int totalRp;

        if (statusBayar < 0) {
            strStatusBayar = "Kurang bayar";
            diffInMonths = (int) lastPaidMonth.until(todayMonth, ChronoUnit.MONTHS);
            totalRp = hitungTotalHutangIuranSamanagara(diffInMonths, lastPaidMonth, tglDaftar, listTarifSamanagara);
        } else if (statusBayar == 0) {
            strStatusBayar = "Tepat waktu";
            diffInMonths = 0;
            totalRp = 0;
        } else {
            strStatusBayar = "Lebih bayar";
            diffInMonths = (int) todayMonth.until(lastPaidMonth, ChronoUnit.MONTHS);
            totalRp = 0;
        }

        StatusBayar result = new StatusBayar();
        result.status = statusBayar;
        result.strStatus = strStatusBayar;
        result.lastPaidMonth = lastPaidMonth;
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

    public static YearMonth fetchLastPaidMonth(Handle handle, String umatId, String leluhurId, LocalDate tglDaftar) {
        Optional<LocalDate> lastPaidMonth = handle.select("select max(d.ut_thn_bln)" +
                " from detil_pembayaran_dana_rutin d" +
                " join pembayaran_samanagara_sosial_tetap p on p.uuid = d.trx_id" +
                " where p.active=1 and p.umat_id=? and d.leluhur_id=?", umatId, leluhurId)
                .mapTo(LocalDate.class)
                .findFirst();
        if (lastPaidMonth.isPresent()) {
            return YearMonth.from(lastPaidMonth.get());
        } else {
            return YearMonth.from(tglDaftar).minusMonths(1);
        }
    }

    public static int hitungTotalHutangIuranSamanagara(int berapaBulan, YearMonth lastPaymentMonth, LocalDate tglDaftar, List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara) {
        int totalRp = 0;

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
