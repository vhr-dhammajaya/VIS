package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.db.DbHelper;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.entities.TarifSamanagara;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Path("/tarif_samanagara")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TarifSamanagaraController {
    private Jdbi jdbi;

    public TarifSamanagaraController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GET
    public PageData<TarifSamanagara> getList(@NotNull GridConfig gridConfig) {
        return jdbi.withHandle(h -> {
            PageData<TarifSamanagara> result = DbHelper.fetchPageData(h, "hist_biaya_smngr", gridConfig, TarifSamanagara.class, false);
            return result;
        });
    }

    @POST
    public boolean updateNominal(int nominal) {
        return jdbi.withHandle(h -> {
            // insert row baru dgn start_date = tgl hari ini & end_date = 31/12/9999
            //  kecuali jika sudah ada row baru dgn start_date = hari ini, update row existing
            // update end_date ut row berisi harga sebelumnya = tgl hari ini
            LocalDate today = LocalDate.now();

            Optional<String> existingUuid = h.select("select id from hist_biaya_smngr where start_date = ?", today)
                    .mapTo(String.class)
                    .findFirst();
            if (existingUuid.isPresent()) {
                h.createUpdate("update hist_biaya_smngr set nominal = ? where id = ?")
                        .bind(0, nominal)
                        .bind(1, existingUuid.get())
                        .execute();
            }
            else {
                String prevRecordId = h.createQuery("select id from hist_biaya_smngr order by start_date desc limit 1")
                        .mapTo(String.class).findOnly();
                h.createUpdate("update hist_biaya_smngr set end_date = ? where id = ?")
                        .bind(0, today)
                        .bind(1, prevRecordId)
                        .execute();

                TarifSamanagara r = new TarifSamanagara();
                r.uuid = UUID.randomUUID().toString();
                r.startDate = today;
                r.endDate = LocalDate.of(3000, 12, 31);
                r.nominal = nominal;

                h.createUpdate("insert into hist_biaya_smngr(id, start_date, end_date, nominal) values(:uuid, :startDate, :endDate, :nominal)")
                        .bindFields(r)
                        .execute();
            }

            return true;
        });
    }
}
