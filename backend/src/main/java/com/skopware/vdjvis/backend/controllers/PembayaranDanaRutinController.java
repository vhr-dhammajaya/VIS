package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.javautils.db.DbHelper;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import com.skopware.vdjvis.api.entities.Pendapatan;
import com.skopware.vdjvis.backend.jdbi.dao.PembayaranDanaRutinDAO;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/pembayaran_dana_rutin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PembayaranDanaRutinController extends BaseCrudController<PembayaranDanaRutin, PembayaranDanaRutinDAO> {

    public PembayaranDanaRutinController(Jdbi jdbi) {
        super(jdbi, "v_pembayaran_samanagara_sosial_tetap", PembayaranDanaRutin.class, PembayaranDanaRutinDAO.class);
    }

    @Path("/get_keperluan")
    @GET
    public Map<String, String> getKeperluanDana(@NotNull PembayaranDanaRutin record) {
        Map<String, String> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        try (Handle handle = jdbi.open()) {
            if (record.tipe == PembayaranDanaRutin.Type.samanagara) {
                List<DetilPembayaranDanaRutin> listDetilUngrouped = handle
                        .select("select * from v_detil_pembayaran_dana_rutin where trx_id=?", record.uuid)
                        .mapTo(DetilPembayaranDanaRutin.class)
                        .list();
                Map<String, List<DetilPembayaranDanaRutin>> detilGroupedByIdLeluhur = ObjectHelper.groupList(listDetilUngrouped, detil -> detil.leluhurSamanagara.uuid);

                for (Map.Entry<String, List<DetilPembayaranDanaRutin>> e : detilGroupedByIdLeluhur.entrySet()) {
                    List<DetilPembayaranDanaRutin> listDetil = e.getValue();
                    String namaLeluhur = listDetil.get(0).leluhurSamanagara.nama;

                    YearMonth smallestMonth = listDetil.stream().map(x -> x.untukBulan).min(Comparator.naturalOrder()).get();
                    YearMonth largestMonth = listDetil.stream().map(x -> x.untukBulan).max(Comparator.naturalOrder()).get();

                    sb.append(String.format("Bayar samanagara ut leluhur %s dari %s s/d %s%n", namaLeluhur, smallestMonth, largestMonth));
                }
            }
            else {
                List<DetilPembayaranDanaRutin> listDetil = handle
                        .select("select * from v_detil_pembayaran_dana_rutin where trx_id=?", record.uuid)
                        .mapTo(DetilPembayaranDanaRutin.class)
                        .list();

                String jenisDana = listDetil.get(0).jenis.name();
                YearMonth smallestMonth = listDetil.stream().map(x -> x.untukBulan).min(Comparator.naturalOrder()).get();
                YearMonth largestMonth = listDetil.stream().map(x -> x.untukBulan).max(Comparator.naturalOrder()).get();

                sb.append(String.format("Bayar dana %s dari %s s/d %s", jenisDana, smallestMonth, largestMonth));
            }
        }

        result.put("keperluanDana", sb.toString());

        return result;
    }

    @POST
    @Path("/request_koreksi")
    public boolean requestKoreksi(@NotNull PembayaranDanaRutin x) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("update pembayaran_samanagara_sosial_tetap set correction_status=1, corr_req_reason=:reason" +
                    " where uuid=:uuid")
                    .bind("reason", x.correctionRequestReason)
                    .bind("uuid", x.uuid)
                    .execute();
        });

        return true;
    }
}
