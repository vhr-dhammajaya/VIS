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
import com.skopware.vdjvis.backend.jdbi.dao.PembayaranDanaRutinDAO;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

        jdbi.useHandle(handle -> {
            List<DetilPembayaranDanaRutin> listDetilUngrouped = handle
                    .select("select * from v_detil_pembayaran_dana_rutin where trx_id=?", record.uuid)
                    .mapTo(DetilPembayaranDanaRutin.class)
                    .list();
            Map<Tuple2<PendaftaranDanaRutin.Type, String>, List<DetilPembayaranDanaRutin>> detilGroupedByJenisAndIdDanaRutinAndIdLeluhur = ObjectHelper.groupList(listDetilUngrouped, detil -> new Tuple2<>(detil.jenis, detil.jenis == PendaftaranDanaRutin.Type.samanagara? detil.leluhurSamanagara.uuid : detil.danaRutin.uuid));
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<Tuple2<PendaftaranDanaRutin.Type, String>, List<DetilPembayaranDanaRutin>> e : detilGroupedByJenisAndIdDanaRutinAndIdLeluhur.entrySet()) {
                Tuple2<PendaftaranDanaRutin.Type, String> key = e.getKey();
                List<DetilPembayaranDanaRutin> listDetil = e.getValue();

                PendaftaranDanaRutin.Type jenisDana = key.val1;
                String namaLeluhur = jenisDana == PendaftaranDanaRutin.Type.samanagara? listDetil.get(0).leluhurSamanagara.nama : null;

                YearMonth smallestMonth = listDetil.stream().map(x -> x.untukBulan).min(Comparator.naturalOrder()).get();
                YearMonth largestMonth = listDetil.stream().map(x -> x.untukBulan).max(Comparator.naturalOrder()).get();

                if (jenisDana == PendaftaranDanaRutin.Type.samanagara) {
                    sb.append(String.format("Bayar samanagara ut leluhur %s dari %s s/d %s", namaLeluhur, smallestMonth, largestMonth));
                }
                else {
                    sb.append(String.format("Bayar dana %s dari %s s/d %s", jenisDana, smallestMonth, largestMonth));
                }
                sb.append('\n');
            }

            result.put("keperluanDana", sb.toString());
        });

        return result;
    }
}
