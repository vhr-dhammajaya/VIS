package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.Pendapatan;
import com.skopware.vdjvis.api.Umat;
import com.skopware.vdjvis.backend.jdbi.dao.PendapatanDAO;
import com.skopware.vdjvis.backend.jdbi.dao.UmatDAO;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.Consumes;
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
}
