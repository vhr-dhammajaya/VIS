package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.CollectionHelper;
import com.skopware.vdjvis.api.entities.CellFoto;
import com.skopware.vdjvis.api.entities.Leluhur;
import com.skopware.vdjvis.api.entities.PapanFoto;
import com.skopware.vdjvis.api.dto.DtoPlacePhoto;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

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
        try (Handle handle = jdbi.open()) {
            List<PapanFoto> listPapan = handle.select("select * from papan_smngr order by nama")
                    .map((rs, ctx) -> {
                        PapanFoto x = new PapanFoto();
                        x.uuid = rs.getString("id");
                        x.nama = rs.getString("nama");
                        int width = rs.getInt("width");
                        int height = rs.getInt("height");
                        x.setDimension(width, height);
                        return x;
                    })
                    .list();

            Map<String, PapanFoto> mapPapanById = CollectionHelper.groupListById(listPapan, papanFoto -> papanFoto.uuid);

            List<CellFoto> listCellFoto = handle.select("select c.*, l.nama as leluhur_nama" +
                    " from cell_papan c" +
                    " left join leluhur_smngr l on l.uuid=c.leluhur_smngr_id")
                    .map((rs, ctx) -> {
                        CellFoto x = new CellFoto();
                        x.uuid = rs.getString("id");
                        x.row = rs.getInt("row");
                        x.col = rs.getInt("col");

                        String leluhur_smngr_id = rs.getString("leluhur_smngr_id");
                        if (leluhur_smngr_id != null) {
                            x.leluhur = new Leluhur();
                            x.leluhur.uuid = leluhur_smngr_id;
                            x.leluhur.nama = rs.getString("leluhur_nama");
                            x.leluhur.cellFoto = new CellFoto();
                            x.leluhur.cellFoto.uuid = x.uuid;
                        }

                        x.papan = new PapanFoto();
                        x.papan.uuid = rs.getString("papan_smngr_id");
                        return x;
                    })
                    .list();

            for (CellFoto cell : listCellFoto) {
                PapanFoto papan = mapPapanById.get(cell.papan.uuid);
                papan.arrCellFoto[cell.row][cell.col] = cell;
            }

            return listPapan;
        }
    }

    @POST
    public boolean placePhoto(@NotNull DtoPlacePhoto param) {
        jdbi.useHandle(h -> {
            h.useTransaction(h2 -> {
                if (param.mendiangOriginCellId != null) {
                    h.createUpdate("update cell_papan set leluhur_smngr_id = null where id = ?").bind(0, param.mendiangOriginCellId).execute();
                }

                if (param.existingIdMendiangInDestCell != null) {
                    h.createUpdate("update leluhur_smngr set cell_papan_id = null where uuid = ?").bind(0, param.existingIdMendiangInDestCell).execute();
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
