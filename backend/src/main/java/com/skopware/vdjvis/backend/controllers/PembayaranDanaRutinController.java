package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.javautils.db.DbHelper;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import com.skopware.vdjvis.backend.jdbi.dao.PembayaranDanaRutinDAO;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/pembayaran_dana_rutin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PembayaranDanaRutinController extends BaseCrudController<PembayaranDanaRutin, PembayaranDanaRutinDAO> {

    public PembayaranDanaRutinController(Jdbi jdbi) {
        super(jdbi, "v_pembayaran_samanagara_sosial_tetap", PembayaranDanaRutin.class, PembayaranDanaRutinDAO.class);
    }
}
