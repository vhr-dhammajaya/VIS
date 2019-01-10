package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.db.DbHelper;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.Acara;
import com.skopware.vdjvis.api.Umat;
import com.skopware.vdjvis.backend.jdbi.AcaraDAO;
import com.skopware.vdjvis.backend.jdbi.UmatDAO;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

@Path("/umat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) // without this, will exception
public class UmatController extends BaseCrudController<Umat, UmatDAO> {
    public UmatController(Jdbi jdbi) {
        super(jdbi, "umat", Umat.class, UmatDAO.class);
    }
}
