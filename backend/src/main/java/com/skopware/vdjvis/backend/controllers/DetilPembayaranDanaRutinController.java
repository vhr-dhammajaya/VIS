package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.backend.jdbi.dao.DetilPembayaranDanaRutinDAO;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/detil_pembayaran_dana_rutin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DetilPembayaranDanaRutinController extends BaseCrudController<DetilPembayaranDanaRutin, DetilPembayaranDanaRutinDAO> {
    public DetilPembayaranDanaRutinController(Jdbi jdbi) {
        super(jdbi, "v_pembayaran_dana_rutin", DetilPembayaranDanaRutin.class, DetilPembayaranDanaRutinDAO.class);
    }

    @POST
    @Override
    public DetilPembayaranDanaRutin create(@NotNull @Valid DetilPembayaranDanaRutin x) {
        // don't allow create
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/request_koreksi")
    public boolean requestKoreksi(@NotNull DetilPembayaranDanaRutin x) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("update pembayaran_dana_rutin set correction_status=1, corr_req_reason=:reason" +
                    " where uuid=:uuid")
                    .bind("reason", x.correctionRequestReason)
                    .bind("uuid", x.uuid)
                    .execute();
        });

        return true;
    }
}
