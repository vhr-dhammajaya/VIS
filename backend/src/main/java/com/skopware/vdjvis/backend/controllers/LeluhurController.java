package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple3;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.dto.DtoPembayaranSamanagara;
import com.skopware.vdjvis.api.dto.DtoStatusBayarLeluhur;
import com.skopware.vdjvis.api.entities.*;
import com.skopware.vdjvis.backend.jdbi.dao.LeluhurDAO;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.YearMonth;
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
                StatusBayar statusBayar = Leluhur.computeStatusBayar(handle, umat.uuid, leluhur.uuid, leluhur.tglDaftar, todayMonth, listTarifSamanagara);

                result.add(ObjectHelper.apply(new DtoStatusBayarLeluhur(), x -> {
                    x.leluhurId = leluhur.uuid;
                    x.leluhurNama = leluhur.nama;
                    x.leluhurTglDaftar = leluhur.tglDaftar;

                    x.statusBayar = statusBayar;
                }));
            }
        }

        return result;
    }

    @Path("/bayar_iuran_samanagara")
    @POST
    public PembayaranDanaRutin bayarIuranSamanagara(@NotNull DtoPembayaranSamanagara input) {
        try (Handle handle = jdbi.open()) {
            PembayaranDanaRutin[] result = new PembayaranDanaRutin[1];

            handle.useTransaction(handle1 -> {
                List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara = Leluhur.fetchListTarifSamanagara(handle1);

                PembayaranDanaRutin pembayaran = new PembayaranDanaRutin();
                result[0] = pembayaran;
                pembayaran.uuid = UUID.randomUUID().toString();

                pembayaran.umat = new Umat();
                pembayaran.umat.uuid = input.umatId;
                pembayaran.umat.nama = handle1.select("select nama from umat where uuid=?", input.umatId).mapTo(String.class).findOnly();

                pembayaran.tipe = PembayaranDanaRutin.Type.samanagara;
                pembayaran.tgl = input.tglTrans;
                pembayaran.totalNominal = input.listLeluhur.stream().mapToInt(e -> e.nominalYgMauDibayarkan).reduce(0, (left, right) -> left + right);
                pembayaran.channel = input.channel;
                pembayaran.keterangan = input.keterangan;

                handle1.createUpdate("insert into pembayaran_samanagara_sosial_tetap(uuid, umat_id, tipe, tgl, total_nominal, channel, keterangan)" +
                        " values(:uuid, :umatId, :tipe, :tgl, :totalNominal, :channel, :keterangan)")
                        .bind("uuid", pembayaran.uuid)
                        .bind("umatId", pembayaran.umat.uuid)
                        .bind("tipe", pembayaran.tipe.name())
                        .bind("tgl", pembayaran.tgl)
                        .bind("totalNominal", pembayaran.totalNominal)
                        .bind("channel", pembayaran.channel)
                        .bind("keterangan", pembayaran.keterangan)
                        .execute();

                for (DtoStatusBayarLeluhur leluhur : input.listLeluhur) {
                    DetilPembayaranDanaRutin templateDetil = new DetilPembayaranDanaRutin();

                    templateDetil.parentTrx = new PembayaranDanaRutin();
                    templateDetil.parentTrx.uuid = pembayaran.uuid;

                    templateDetil.jenis = DetilPembayaranDanaRutin.Type.samanagara;

                    templateDetil.leluhurSamanagara = new Leluhur();
                    templateDetil.leluhurSamanagara.uuid = leluhur.leluhurId;

                    YearMonth lastPaidMonth = Leluhur.fetchLastPaidMonth(handle1, pembayaran.umat.uuid, templateDetil.leluhurSamanagara.uuid, leluhur.leluhurTglDaftar);
                    YearMonth currentMonth = lastPaidMonth.plusMonths(1);

                    for (int i = 0; i < leluhur.mauBayarBrpBulan; i++) {
                        LocalDate currDate = leluhur.leluhurTglDaftar.withYear(currentMonth.getYear()).withMonth(currentMonth.getMonthValue());
                        int nominal = DateTimeHelper.findValueInDateRange(currDate, listTarifSamanagara).get();

                        DetilPembayaranDanaRutin detil = templateDetil.clone();
                        detil.uuid = UUID.randomUUID().toString();
                        detil.untukBulan = currentMonth;
                        detil.nominal = nominal;

                        handle1.createUpdate("insert into detil_pembayaran_dana_rutin(uuid, trx_id, jenis, leluhur_id, ut_thn_bln, nominal)" +
                                " values(:uuid, :trx_id, :jenis, :leluhur_id, :ut_thn_bln, :nominal)")
                                .bind("uuid", detil.uuid)
                                .bind("trx_id", detil.parentTrx.uuid)
                                .bind("jenis", detil.jenis.name())
                                .bind("leluhur_id", detil.leluhurSamanagara.uuid)
                                .bind("ut_thn_bln", LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), 1))
                                .bind("nominal", detil.nominal)
                                .execute();

                        pembayaran.mapDetilPembayaran.put(detil.uuid, detil);
                        currentMonth = currentMonth.plusMonths(1);
                    }
                }
            });

            return result[0];
        }
    }
}
