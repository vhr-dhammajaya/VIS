package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.entities.Siswa;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.backend.jdbi.dao.SiswaDAO;
import com.skopware.vdjvis.backend.jdbi.dao.UmatDAO;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/siswa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SiswaController extends BaseCrudController<Siswa, SiswaDAO> {
    public SiswaController(Jdbi jdbi) {
        super(jdbi, "siswa", Siswa.class, SiswaDAO.class);
    }

    @POST
    @Override
    public Siswa create(@NotNull @Valid Siswa x) {
        return jdbi.withHandle(handle -> {
            int ymTglDaftar = DateTimeHelper.computeMySQLYearMonth(x.tglDaftar);
            int seqNum = handle.select("select count(*) from siswa where extract(year_month from tgl_daftar) = ?", ymTglDaftar)
                    .mapTo(int.class)
                    .findOnly();
            seqNum++;

            x.idBarcode = String.format("%s%02d%02d%02d%05d",
                    String.valueOf(x.tglDaftar.getYear()).substring(2), x.tglDaftar.getMonthValue(),
                    x.tglLahir.getDayOfMonth(), x.tglLahir.getMonthValue(),
                    seqNum);

            SiswaDAO dao = handle.attach(daoClass);
            dao.create(x);
            Siswa result = dao.get(x.getUuid());
            return result;
        });

    }
}
