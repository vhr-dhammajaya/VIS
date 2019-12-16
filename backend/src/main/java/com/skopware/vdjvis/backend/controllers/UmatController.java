package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.jdbi.dao.UmatDAO;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/umat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) // without this, will exception
public class UmatController extends BaseCrudController<Umat, UmatDAO> {
    public UmatController(Jdbi jdbi) {
        super(jdbi, "umat", Umat.class, UmatDAO.class);
    }

    @POST
    @Override
    public Umat create(@NotNull @Valid Umat x) {
        return jdbi.withHandle(handle -> {
            int ymTglDaftar = DateTimeHelper.computeMySQLYearMonth(x.tglDaftar);
            int seqNum = handle.select("select count(*) from umat where extract(year_month from tgl_daftar) = ?", ymTglDaftar)
                    .mapTo(int.class)
                    .findOnly();
            seqNum++;

            x.idBarcode = String.format("%s%02d%02d%02d%05d",
                    String.valueOf(x.tglDaftar.getYear()).substring(2), x.tglDaftar.getMonthValue(),
                    x.tglLahir.getDayOfMonth(), x.tglLahir.getMonthValue(),
                    seqNum);

            UmatDAO dao = handle.attach(daoClass);
            dao.create(x);
            Umat result = dao.get(x.getUuid());
            return result;
        });
    }
}
