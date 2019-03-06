package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple3;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.dto.DtoPembayaranSamanagara;
import com.skopware.vdjvis.api.dto.DtoStatusBayarLeluhur;
import com.skopware.vdjvis.api.entities.Leluhur;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import com.skopware.vdjvis.api.entities.StatusBayar;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.backend.jdbi.dao.LeluhurDAO;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Path("/leluhur")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LeluhurController extends BaseCrudController<Leluhur, LeluhurDAO> {
    public LeluhurController(Jdbi jdbi) {
        super(jdbi, "v_leluhur", Leluhur.class, LeluhurDAO.class);
    }

    @GET
    @Override
    public PageData<Leluhur> getList(@NotNull GridConfig gridConfig) {
        PageData<Leluhur> pageData = super.getList(gridConfig);
        List<Leluhur> rows = pageData.rows;

        try (Handle handle = jdbi.open()) {
            List<StatusBayar> listStatusBayar = Leluhur.computeStatusBayar(handle, rows, YearMonth.now(), Leluhur.fetchListTarifSamanagara(handle));

            for (int i = 0; i < rows.size(); i++) {
                Leluhur leluhur = rows.get(i);
                StatusBayar statusBayar = listStatusBayar.get(i);

                leluhur.statusBayar = statusBayar;
            }
        }

        return pageData;
    }

    @POST
    @Override
    public Leluhur create(@NotNull @Valid Leluhur x) {
        Leluhur leluhur = super.create(x);

        try (Handle handle = jdbi.open()) {
            leluhur.statusBayar = Leluhur.computeStatusBayar(handle, leluhur, YearMonth.now(), Leluhur.fetchListTarifSamanagara(handle));
        }

        return leluhur;
    }

    @Path("/status_bayar")
    @GET
    public List<DtoStatusBayarLeluhur> computeStatusBayarLeluhur(@NotNull Umat umat) {
        class Leluhur2 {
            public String uuid;
            public String nama;
            public LocalDate tglDaftar;
        }

        List<DtoStatusBayarLeluhur> result = new ArrayList<>();

        YearMonth todayMonth = YearMonth.now();

        try (Handle handle = jdbi.open()) {
            List<Leluhur2> listLeluhur = handle.select("select l.uuid, l.nama, l.tgl_daftar from leluhur_smngr l where l.active=1 and l.umat_id=?", umat.uuid)
                    .map((rs, ctx) -> {
                        Leluhur2 x = new Leluhur2();
                        x.uuid = rs.getString("uuid");
                        x.nama = rs.getString("nama");
                        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));
                        return x;
                    })
                    .list();

            List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara = Leluhur.fetchListTarifSamanagara(handle);

            for (Leluhur2 leluhur : listLeluhur) {
                Optional<LocalDate> lastPayment = handle.select("select max(ut_thn_bln) from pembayaran_dana_rutin where umat_id=? and leluhur_id=?", umat.uuid, leluhur.uuid)
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

                    // hitung jumlah kurang bayar
                    totalRp = Leluhur.hitungTotalHutangIuranSamanagara(diffInMonths, lastPaymentMonth, leluhur.tglDaftar, listTarifSamanagara);
                }
                else if (statusBayar == 0) {
                    strStatusBayar = "Tepat waktu";
                    diffInMonths = 0;
                    totalRp = 0;
                }
                else {
                    strStatusBayar = "Lebih bayar";
                    diffInMonths = todayMonth.until(lastPaymentMonth, ChronoUnit.MONTHS);
                    totalRp = 0;
                }

                String strStatusBayar2 = strStatusBayar;
                long diffInMonths2 = diffInMonths;
                long totalRp2 = totalRp;

                result.add(ObjectHelper.apply(new DtoStatusBayarLeluhur(), x -> {
                    x.leluhurId = leluhur.uuid;
                    x.leluhurNama = leluhur.nama;
                    x.leluhurTglDaftar = leluhur.tglDaftar;

                    x.statusBayar = statusBayar;
                    x.strStatusBayar = strStatusBayar2;
                    x.lastPaymentMonth = lastPaymentMonth;
                    x.diffInMonths = diffInMonths2;
                    x.nominal = totalRp2;
                }));
            }
        }

        return result;
    }

    @Path("/bayar_iuran_samanagara")
    @POST
    public boolean bayarIuranSamanagara(@NotNull DtoPembayaranSamanagara pembayaran) {
        try (Handle handle = jdbi.open()) {
            List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara = Leluhur.fetchListTarifSamanagara(handle);

            for (DtoStatusBayarLeluhur leluhur : pembayaran.listLeluhur) {
                Optional<LocalDate> lastPaidMonth = handle.select("select max(ut_thn_bln) from pembayaran_dana_rutin" +
                        " where umat_id=? and leluhur_id=?", pembayaran.umatId, leluhur.leluhurId)
                        .mapTo(LocalDate.class)
                        .findFirst();
                LocalDate currentMonth;

                if (lastPaidMonth.isPresent()) {
                    currentMonth = lastPaidMonth.get().plusMonths(1);
                }
                else {
                    currentMonth = leluhur.leluhurTglDaftar.withDayOfMonth(1);
                }

                for (int i = 0; i < leluhur.mauBayarBrpBulan; i++) {
                    LocalDate currDate = leluhur.leluhurTglDaftar.withYear(currentMonth.getYear()).withMonth(currentMonth.getMonthValue());
                    int nominal = DateTimeHelper.findValueInDateRange(currDate, listTarifSamanagara).get();

                    handle.createUpdate("insert into pembayaran_dana_rutin(uuid, umat_id, jenis, leluhur_id, ut_thn_bln, nominal, tgl, channel, keterangan)" +
                            " values(:uuid, :umat_id, :jenis, :leluhur_id, :ut_thn_bln, :nominal, :tgl, :channel, :keterangan)")
                            .bind("uuid", UUID.randomUUID().toString())
                            .bind("umat_id", pembayaran.umatId)
                            .bind("jenis", PendaftaranDanaRutin.Type.samanagara.name())
                            .bind("leluhur_id", leluhur.leluhurId)
                            .bind("ut_thn_bln", currentMonth)
                            .bind("nominal", nominal)
                            .bind("tgl", pembayaran.tglTrans)
                            .bind("channel", pembayaran.channel)
                            .bind("keterangan", pembayaran.keterangan)
                            .execute();

                    currentMonth = currentMonth.plusMonths(1);
                }
            }
        }

        return true;
    }
}
