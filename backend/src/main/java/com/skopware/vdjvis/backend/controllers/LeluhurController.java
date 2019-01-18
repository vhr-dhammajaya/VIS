package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.Leluhur;
import com.skopware.vdjvis.backend.jdbi.dao.LeluhurDAO;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/leluhur")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LeluhurController extends BaseCrudController<Leluhur, LeluhurDAO> {
    public LeluhurController(Jdbi jdbi) {
        super(jdbi, "leluhur_smngr", Leluhur.class, LeluhurDAO.class);
    }
}
