package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.db.BaseRecord;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import com.skopware.vdjvis.api.requestparams.RqLaporanStatusDanaRutin;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Path("/laporan")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LaporanController {
    private Jdbi jdbi;

    public LaporanController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Path("/status_dana_rutin")
    @GET
    public List<Map<String, Object>> computeLaporanStatusDanaRutin(@NotNull RqLaporanStatusDanaRutin param) {
        class PendaftaranDanaRutin2 extends BaseRecord<PendaftaranDanaRutin2> {
            public LocalDate tglDaftar;
            public int nominal;
            public PendaftaranDanaRutin.Type tipe;
            public String idUmat;

            public String namaUmat;
            public String alamat;
            public String noTelpon;

            @Override
            public String toUiString() {
                return null;
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();

        String idUmat = param.idUmat; // nullable
        YearMonth todayMonth = YearMonth.now();

        try (Handle h = jdbi.open()) {
            //#region hitung status dana sosial & tetap
            Query selPendaftaranDanaRutin;
            if (idUmat != null) {
                selPendaftaranDanaRutin = h.select("select p.*, u.nama as nama_umat, u.no_telpon, u.alamat" +
                        " from pendaftaran_dana_rutin p" +
                        " join umat u on u.uuid = p.umat_id" +
                        " where p.active=1 and p.umat_id=?", idUmat);
            }
            else {
                selPendaftaranDanaRutin = h.select("select p.*, u.nama as nama_umat, u.no_telpon, u.alamat" +
                        " from pendaftaran_dana_rutin p" +
                        " join umat u on u.uuid = p.umat_id" +
                        " where p.active=1");
            }

            List<PendaftaranDanaRutin2> listDanaRutin = selPendaftaranDanaRutin
                    .map((rs, ctx) -> {
                        PendaftaranDanaRutin2 x = new PendaftaranDanaRutin2();

                        x.uuid = rs.getString("id");
                        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));
                        x.nominal = rs.getInt("nominal");
                        x.tipe = PendaftaranDanaRutin.Type.valueOf(rs.getString("tipe"));
                        x.idUmat = rs.getString("umat_id");
                        x.namaUmat = rs.getString("nama_umat");
                        x.noTelpon = rs.getString("no_telpon");
                        x.alamat = rs.getString("alamat");

                        return x;
                    })
                    .list();

            for (PendaftaranDanaRutin2 danaRutin : listDanaRutin) {
                Optional<LocalDate> lastPayment = h.select("select max(ut_thn_bln) from pembayaran_dana_rutin where umat_id=? and dana_rutin_id=?", danaRutin.idUmat, danaRutin.uuid)
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

                int statusBayar = lastPaymentMonth.compareTo(todayMonth); // 0=tepat waktu, -1=kurang bayar, 1=lebih bayar
                String jenisDana = danaRutin.tipe.name();
                String strStatusBayar = "";
                long diffInMonths = 0;
                long totalRp = 0;

                if (statusBayar < 0) {
                    strStatusBayar = "Kurang bayar";
                    diffInMonths = lastPaymentMonth.until(todayMonth, ChronoUnit.MONTHS);
                    totalRp = diffInMonths * danaRutin.nominal;
                }
                else if (statusBayar == 0)  {
                    strStatusBayar = "Tepat waktu";
                }
                else if (statusBayar > 0)  {
                    strStatusBayar = "Lebih bayar";
                    diffInMonths = todayMonth.until(lastPaymentMonth, ChronoUnit.MONTHS);
                    totalRp = diffInMonths * danaRutin.nominal;
                }

                String strStatusBayar2 = strStatusBayar;
                long diffInMonths2 = diffInMonths;
                long totalRp2 = totalRp;

                result.add(ObjectHelper.apply(new HashMap<>(), x -> {
                    x.put("namaUmat", danaRutin.namaUmat);
                    x.put("noTelpon", danaRutin.noTelpon);
                    x.put("alamat", danaRutin.alamat);

                    x.put("jenisDana", jenisDana);
                    x.put("status", strStatusBayar2);
                    x.put("months", diffInMonths2);
                    x.put("totalRp", totalRp2);
                }));
            }
            //#endregion
        }

        return result; //
    }
}
