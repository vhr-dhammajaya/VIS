package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.entities.Pendapatan;
import com.skopware.vdjvis.jdbi.dao.PendapatanDAO;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/pendapatan")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) // without this, will exception
public class PendapatanController extends BaseCrudController<Pendapatan, PendapatanDAO> {
    public PendapatanController(Jdbi jdbi) {
        super(jdbi, "v_pendapatan", Pendapatan.class, PendapatanDAO.class);
    }

    @POST
    @Path("/request_koreksi")
    public boolean requestKoreksi(@NotNull Pendapatan x) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("update pendapatan set correction_status=1, corr_req_reason=:reason" +
                    " where id=:id")
                    .bind("reason", x.correctionRequestReason)
                    .bind("id", x.uuid)
                    .execute();
        });

        return true;
    }
}
