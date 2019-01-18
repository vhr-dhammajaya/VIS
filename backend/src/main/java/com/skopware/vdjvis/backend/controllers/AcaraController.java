package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.Acara;
import com.skopware.vdjvis.backend.jdbi.dao.AcaraDAO;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/acara")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) // without this, will exception
public class AcaraController extends BaseCrudController<Acara, AcaraDAO> {
    public AcaraController(Jdbi jdbi) {
        super(jdbi, "acara", Acara.class, AcaraDAO.class);
    }
}
