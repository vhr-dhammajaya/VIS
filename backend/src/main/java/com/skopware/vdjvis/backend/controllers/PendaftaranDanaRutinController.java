package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.db.PageData;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.dto.DtoBayarDanaSosialDanTetap;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import com.skopware.vdjvis.api.entities.StatusBayar;
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
    public boolean bayarDanaRutin(@NotNull @Valid DtoBayarDanaSosialDanTetap x) {
        try (Handle h = jdbi.open()) {
            PendaftaranDanaRutinDAO pendaftaranDanaRutinDAO = h.attach(PendaftaranDanaRutinDAO.class);
            PendaftaranDanaRutin pendaftaranDanaRutin = pendaftaranDanaRutinDAO.get(x.idPendaftaran);

            Optional<LocalDate> lastPaidMonth = h.select("select max(ut_thn_bln) from pembayaran_dana_rutin" +
                    " where active=1 and umat_id=? and dana_rutin_id=?", pendaftaranDanaRutin.umat.uuid, x.idPendaftaran)
                    .mapTo(LocalDate.class)
                    .findFirst();
            LocalDate currentMonth;

            if (lastPaidMonth.isPresent()) {
                currentMonth = lastPaidMonth.get().plusMonths(1);
            }
            else {
                currentMonth = pendaftaranDanaRutin.tglDaftar.withDayOfMonth(1);
            }

            for (int i = 0; i < x.countBulan; i++) {
                h.createUpdate("insert into pembayaran_dana_rutin(uuid, umat_id, jenis, dana_rutin_id, ut_thn_bln, nominal, tgl, channel, keterangan)" +
                        " values(:uuid, :umat_id, :jenis, :dana_rutin_id, :ut_thn_bln, :nominal, :tgl, :channel, :keterangan)")
                        .bind("uuid", UUID.randomUUID().toString())
                        .bind("umat_id", pendaftaranDanaRutin.umat.uuid)
                        .bind("jenis", pendaftaranDanaRutin.tipe.name())
                        .bind("dana_rutin_id", x.idPendaftaran)
                        .bind("ut_thn_bln", currentMonth)
                        .bind("nominal", pendaftaranDanaRutin.nominal)
                        .bind("tgl", x.tglTrans)
                        .bind("channel", x.channel)
                        .bind("keterangan", x.keterangan)
                        .execute();
                currentMonth = currentMonth.plusMonths(1);
            }
        }

        return true;
    }
}
