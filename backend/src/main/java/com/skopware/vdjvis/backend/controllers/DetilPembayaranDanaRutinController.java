package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.db.DbHelper;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.backend.jdbi.dao.DetilPembayaranDanaRutinDAO;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/detil_pembayaran_dana_rutin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DetilPembayaranDanaRutinController extends BaseCrudController<DetilPembayaranDanaRutin, DetilPembayaranDanaRutinDAO> {
    public DetilPembayaranDanaRutinController(Jdbi jdbi) {
        super(jdbi, "v_detil_pembayaran_dana_rutin", DetilPembayaranDanaRutin.class, DetilPembayaranDanaRutinDAO.class);
    }

    @GET
    @Override
    public PageData<DetilPembayaranDanaRutin> getList(@NotNull GridConfig gridConfig) {
        return jdbi.withHandle(h -> {
            PageData<DetilPembayaranDanaRutin> result = DbHelper.fetchPageData(h, tableNameForSelect, gridConfig, clazz, false);
            return result;
        });
    }
}
