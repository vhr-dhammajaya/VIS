package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.db.DbHelper;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.Acara;
import com.skopware.vdjvis.backend.jdbi.AcaraDAO;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/acara")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) // without this, will exception
public class AcaraController extends BaseCrudController<Acara, AcaraDAO> {
    public AcaraController(Jdbi jdbi) {
        super(jdbi, "acara", Acara.class, AcaraDAO.class);
    }
}
