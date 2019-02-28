package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.PembayaranDanaSosialDanTetap;
import com.skopware.vdjvis.api.PendaftaranDanaRutin;
import com.skopware.vdjvis.backend.jdbi.dao.PendaftaranDanaRutinDAO;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Path("/pendaftaran_dana_rutin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PendaftaranDanaRutinController extends BaseCrudController<PendaftaranDanaRutin, PendaftaranDanaRutinDAO> {
    public PendaftaranDanaRutinController(Jdbi jdbi) {
        super(jdbi, "pendaftaran_dana_rutin", PendaftaranDanaRutin.class, PendaftaranDanaRutinDAO.class);
    }

    @Path("/bayar_dana_sosial_tetap")
    @POST
    public boolean bayarDanaRutin(@NotNull @Valid PembayaranDanaSosialDanTetap x) {
        try (Handle h = jdbi.open()) {
            PendaftaranDanaRutinDAO pendaftaranDanaRutinDAO = h.attach(PendaftaranDanaRutinDAO.class);
            PendaftaranDanaRutin pendaftaranDanaRutin = pendaftaranDanaRutinDAO.get(x.idPendaftaran);

            Optional<LocalDate> lastPaidMonth = h.select("select max(ut_thn_bln) from pembayaran_dana_rutin" +
                    " where umat_id=? and dana_rutin_id=?", pendaftaranDanaRutin.umatId, x.idPendaftaran)
                    .mapTo(LocalDate.class)
                    .findFirst();
            LocalDate current;

            if (lastPaidMonth.isPresent()) {
                current = lastPaidMonth.get().plusMonths(1);
            }
            else {
                current = pendaftaranDanaRutin.tglDaftar.withDayOfMonth(1);
            }

            for (int i = 0; i < x.countBulan; i++) {
                h.createUpdate("insert into pembayaran_dana_rutin(uuid, umat_id, jenis, dana_rutin_id, ut_thn_bln, nominal, tgl, channel, keterangan)" +
                        " values(:uuid, :umat_id, :jenis, :dana_rutin_id, :ut_thn_bln, :nominal, :tgl, :channel, :keterangan)")
                        .bind("uuid", UUID.randomUUID().toString())
                        .bind("umat_id", pendaftaranDanaRutin.umatId)
                        .bind("jenis", pendaftaranDanaRutin.tipe.name())
                        .bind("dana_rutin_id", x.idPendaftaran)
                        .bind("ut_thn_bln", current)
                        .bind("nominal", pendaftaranDanaRutin.nominal)
                        .bind("tgl", x.tglTrans)
                        .bind("channel", x.channel)
                        .bind("keterangan", x.keterangan)
                        .execute();
                current = current.plusMonths(1);
            }
        }

        return true;
    }
}
