package com.skopware.vdjvis.backend.controllers;

import com.skopware.vdjvis.api.CellFoto;
import com.skopware.vdjvis.api.PapanFoto;
import com.skopware.vdjvis.api.PlacePhotoRequestParam;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.Query;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/lokasi_foto")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LokasiFotoController {
    private Jdbi jdbi;

    public LokasiFotoController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GET
    @Path("/list_papan")
    public List<PapanFoto> getListPapan() {
        return jdbi.withHandle(h -> {
            return h.select("select * from papan_smngr")
                    .mapTo(PapanFoto.class)
                    .list();
        });
    }

    @GET
    @Path("/list_cell")
    public List<CellFoto> getListCellFoto() {
        return jdbi.withHandle(h -> {
            Query select = h.select("select c.id, c.row, c.col, c.papan_smngr_id, c.leluhur_smngr_id, l.nama as leluhur_nama from cell_papan c" +
                    " left join leluhur_smngr l on l.uuid = c.leluhur_smngr_id");
            ResultIterable<CellFoto> rs = select.mapTo(CellFoto.class);
            List<CellFoto> list = rs.list();
            return list;
        });
    }

    @POST
    public boolean placePhoto(@NotNull PlacePhotoRequestParam param) {
        jdbi.useHandle(h -> {
            h.useTransaction(h2 -> {
                if (param.originCellId != null) {
                    h.createUpdate("update cell_papan set leluhur_smngr_id = null where id = ?").bind(0, param.originCellId).execute();
                }

                if (param.destCellExistingIdMendiang != null) {
                    h.createUpdate("update leluhur_smngr set cell_papan_id = null where uuid = ?").bind(0, param.destCellExistingIdMendiang).execute();
                }

                String leluhurId = param.idMendiang;
                String cellFotoId = param.destCellId;
                h.createUpdate("update cell_papan set leluhur_smngr_id = ? where id = ?")
                        .bind(0, leluhurId)
                        .bind(1, cellFotoId)
                        .execute();
                h.createUpdate("update leluhur_smngr set cell_papan_id = ? where uuid = ?")
                        .bind(0, cellFotoId)
                        .bind(1, leluhurId)
                        .execute();
            });
        });

        return true;
    }
}
