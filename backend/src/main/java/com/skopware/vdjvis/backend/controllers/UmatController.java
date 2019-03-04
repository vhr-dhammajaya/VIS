package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.backend.jdbi.dao.UmatDAO;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/umat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) // without this, will exception
public class UmatController extends BaseCrudController<Umat, UmatDAO> {
    public UmatController(Jdbi jdbi) {
        super(jdbi, "umat", Umat.class, UmatDAO.class);
    }
}
