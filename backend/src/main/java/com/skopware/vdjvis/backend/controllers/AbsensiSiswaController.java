package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.Siswa;
import com.skopware.vdjvis.api.entities.Umat;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Path("/absensi_siswa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AbsensiSiswaController {
    private Jdbi jdbi;

    public AbsensiSiswaController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GET
    @Path("/list")
    public List<Siswa> getListUmat() {
        return jdbi.withHandle(handle -> {
            return handle.select("select uuid, nama, alamat, tgl_lahir, id_barcode from siswa")
                    .map((rs, ctx) -> {
                        Siswa x = new Siswa();
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
    @Path("/daftar_hadir")
    public List<Siswa> getDaftarHadir() {
        return jdbi.withHandle(handle -> {
            return handle.select("select u.uuid, u.nama from kehadiran_siswa k" +
                    " join siswa u on u.uuid=k.siswa_uuid" +
                    " where k.tgl=?", LocalDate.now())
                    .map((rs, ctx) -> {
                        Siswa x = new Siswa();
                        x.uuid = rs.getString("uuid");
                        x.nama = rs.getString("nama");
                        return x;
                    })
                    .list();
        });
    }

    @POST
    @Path("/absen")
    public boolean catatKehadiran(@NotNull Siswa x) {
        LocalDate today = LocalDate.now();

        jdbi.useHandle(handle -> {
            Optional<String> id = handle.select("select siswa_uuid from kehadiran_siswa where siswa_uuid=? and tgl=?", x.uuid, today)
                    .mapTo(String.class)
                    .findFirst();

            if (!id.isPresent()) {
                handle.createUpdate("insert into kehadiran_siswa(siswa_uuid, tgl) values(:siswa_uuid, :tgl)")
                        .bind("siswa_uuid", x.uuid)
                        .bind("tgl", today)
                        .execute();
            }
        });

        return true;
    }
}
