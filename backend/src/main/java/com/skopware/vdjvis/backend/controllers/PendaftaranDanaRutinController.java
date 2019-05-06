package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.db.PageData;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.dto.DtoBayarDanaSosialDanTetap;
import com.skopware.vdjvis.api.entities.*;
import com.skopware.vdjvis.backend.jdbi.dao.PendaftaranDanaRutinDAO;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/pendaftaran_dana_rutin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PendaftaranDanaRutinController extends BaseCrudController<PendaftaranDanaRutin, PendaftaranDanaRutinDAO> {
    public PendaftaranDanaRutinController(Jdbi jdbi) {
        super(jdbi, "pendaftaran_dana_rutin", PendaftaranDanaRutin.class, PendaftaranDanaRutinDAO.class);
    }

    @GET
    @Override
    public PageData<PendaftaranDanaRutin> getList(@NotNull GridConfig gridConfig) {
        PageData<PendaftaranDanaRutin> pageData = super.getList(gridConfig);
        List<PendaftaranDanaRutin> rows = pageData.rows;

        try (Handle handle = jdbi.open()) {
            List<StatusBayar> statusBayarList = PendaftaranDanaRutin.computeStatusBayar(handle, rows, YearMonth.now());

            for (int i = 0; i < rows.size(); i++) {
                rows.get(i).statusBayar = statusBayarList.get(i);
            }
        }

        return pageData;
    }

    @POST
    @Override
    public PendaftaranDanaRutin create(@NotNull @Valid PendaftaranDanaRutin x) {
        PendaftaranDanaRutin danaRutin = super.create(x);

        try (Handle handle = jdbi.open()) {
            danaRutin.statusBayar = PendaftaranDanaRutin.computeStatusBayar(handle, danaRutin, YearMonth.now());
        }

        return danaRutin;
    }

    @Path("/bayar_dana_sosial_tetap")
    @POST
    public PembayaranDanaRutin bayarDanaRutin(@NotNull @Valid DtoBayarDanaSosialDanTetap input) {
        try (Handle handle = jdbi.open()) {
            PembayaranDanaRutin[] result = new PembayaranDanaRutin[1];

            handle.useTransaction(handle1 -> {
                PendaftaranDanaRutinDAO pendaftaranDanaRutinDAO = handle1.attach(PendaftaranDanaRutinDAO.class);
                PendaftaranDanaRutin pendaftaranDanaRutin = pendaftaranDanaRutinDAO.get(input.idPendaftaran);

                PembayaranDanaRutin pembayaran = new PembayaranDanaRutin();
                result[0] = pembayaran;
                pembayaran.uuid = UUID.randomUUID().toString();

                pembayaran.umat = new Umat();
                pembayaran.umat.uuid = pendaftaranDanaRutin.umat.uuid;
                pembayaran.umat.nama = handle1.select("select nama from umat where uuid=?", pendaftaranDanaRutin.umat.uuid).mapTo(String.class).findOnly();

                pembayaran.tipe = PembayaranDanaRutin.Type.sosial_tetap;
                pembayaran.tgl = input.tglTrans;
                pembayaran.totalNominal = input.countBulan * pendaftaranDanaRutin.nominal;
                pembayaran.channel = input.channel;
                pembayaran.keterangan = input.keterangan;

                handle1.createUpdate("insert into pembayaran_samanagara_sosial_tetap(uuid, umat_id, tipe, tgl, total_nominal, channel, keterangan)" +
                        " values(?, ?, ?, ?, ?, ?, ?)")
                        .bind(0, pembayaran.uuid)
                        .bind(1, pembayaran.umat.uuid)
                        .bind(2, pembayaran.tipe.name())
                        .bind(3, pembayaran.tgl)
                        .bind(4, pembayaran.totalNominal)
                        .bind(5, pembayaran.channel)
                        .bind(6, pembayaran.keterangan)
                        .execute();

                YearMonth lastPaidMonth = PendaftaranDanaRutin.fetchLastPaidMonth(handle1, pendaftaranDanaRutin.umat.uuid, input.idPendaftaran, pendaftaranDanaRutin.tglDaftar);
                YearMonth currentMonth = lastPaidMonth.plusMonths(1);

                for (int i = 0; i < input.countBulan; i++) {
                    DetilPembayaranDanaRutin detil = new DetilPembayaranDanaRutin();
                    detil.uuid = UUID.randomUUID().toString();

                    detil.parentTrx = new PembayaranDanaRutin();
                    detil.parentTrx.uuid = pembayaran.uuid;

                    detil.jenis = pendaftaranDanaRutin.tipe;

                    detil.danaRutin = new PendaftaranDanaRutin();
                    detil.danaRutin.uuid = pendaftaranDanaRutin.uuid;

                    detil.untukBulan = currentMonth;
                    detil.nominal = pendaftaranDanaRutin.nominal;

                    handle1.createUpdate("insert into detil_pembayaran_dana_rutin(uuid, trx_id, jenis, dana_rutin_id, ut_thn_bln, nominal)" +
                            " values(?, ?, ?, ?, ?, ?)")
                            .bind(0, detil.uuid)
                            .bind(1, detil.parentTrx.uuid)
                            .bind(2, detil.jenis.name())
                            .bind(3, detil.danaRutin.uuid)
                            .bind(4, LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), 1))
                            .bind(5, detil.nominal)
                            .execute();

                    pembayaran.mapDetilPembayaran.put(detil.uuid, detil);
                    currentMonth = currentMonth.plusMonths(1);
                }
            });

            return result[0];
        }
    }
}
