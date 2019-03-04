package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.entities.Pengeluaran;
import com.skopware.vdjvis.backend.jdbi.dao.PengeluaranDAO;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/pengeluaran")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) // without this, will exception
public class PengeluaranController extends BaseCrudController<Pengeluaran, PengeluaranDAO> {
    public PengeluaranController(Jdbi jdbi) {
        super(jdbi, "v_pengeluaran", Pengeluaran.class, PengeluaranDAO.class);
    }
}
