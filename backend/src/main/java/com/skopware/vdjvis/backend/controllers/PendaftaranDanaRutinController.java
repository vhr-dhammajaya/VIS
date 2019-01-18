package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.PendaftaranDanaRutin;
import com.skopware.vdjvis.backend.jdbi.dao.PendaftaranDanaRutinDAO;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/pendaftaran_dana_rutin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PendaftaranDanaRutinController extends BaseCrudController<PendaftaranDanaRutin, PendaftaranDanaRutinDAO> {
    public PendaftaranDanaRutinController(Jdbi jdbi) {
        super(jdbi, "pendaftaran_dana_rutin", PendaftaranDanaRutin.class, PendaftaranDanaRutinDAO.class);
    }
}
