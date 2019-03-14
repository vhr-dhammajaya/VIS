package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.Umat;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Path("/absensi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AbsensiController {
    private Jdbi jdbi;

    public AbsensiController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GET
    @Path("/list_umat")
    public List<Umat> getListUmat() {
        return jdbi.withHandle(handle -> {
            return handle.select("select uuid, nama, alamat, tgl_lahir, id_barcode from umat")
                    .map((rs, ctx) -> {
                        Umat x = new Umat();
                        x.uuid = rs.getString("uuid");
                        x.nama = rs.getString("nama");
                        x.alamat = rs.getString("alamat");
                        x.tglLahir = DateTimeHelper.toLocalDate(rs.getDate("tgl_lahir"));
                        x.idBarcode = rs.getString("id_barcode");
                        return x;
                    })
                    .list();
        });
    }

    @GET
    @Path("/daftar_umat_hadir")
    public List<Umat> fetchUmatHadir() {
        return jdbi.withHandle(handle -> {
            return handle.select("select u.uuid, u.nama from kehadiran k" +
                    " join umat u on u.uuid=k.umat_id" +
                    " where k.tgl=?", LocalDate.now())
                    .map((rs, ctx) -> {
                        Umat x = new Umat();
                        x.uuid = rs.getString("uuid");
                        x.nama = rs.getString("nama");
                        return x;
                    })
                    .list();
        });
    }

    @POST
    @Path("/absen_umat")
    public boolean catatKehadiran(@NotNull Umat umat) {
        LocalDate today = LocalDate.now();

        jdbi.useHandle(handle -> {
            Optional<String> id = handle.select("select umat_id from kehadiran where umat_id=? and tgl=?", umat.uuid, today)
                    .mapTo(String.class)
                    .findFirst();

            if (!id.isPresent()) {
                handle.createUpdate("insert into kehadiran(umat_id, tgl) values(:umat_id, :tgl)")
                        .bind("umat_id", umat.uuid)
                        .bind("tgl", today)
                        .execute();
            }
        });

        return true;
    }
}
