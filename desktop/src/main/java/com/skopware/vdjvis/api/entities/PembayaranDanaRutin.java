package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.CollectionHelper;
import com.skopware.javautils.db.BaseRecord;
import com.skopware.javautils.db.DbRecord;
import org.jdbi.v3.core.Handle;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class PembayaranDanaRutin extends BaseRecord<PembayaranDanaRutin> {
    // table columns
    public Type tipe;
    public LocalDate tgl;
    public int noSeq;
    public int totalNominal;
    public String channel;
    public String keterangan;
    public boolean correctionStatus;
    public String correctionRequestReason;
    public String idTrx;

    // relationships
    public Umat umat;
    public Map<String, DetilPembayaranDanaRutin> mapDetilPembayaran = new LinkedHashMap<>();
    public User user;

    public String computeKeperluanDana(Handle h) {
        if (tipe == PembayaranDanaRutin.Type.samanagara) {
            List<DetilPembayaranDanaRutin> listDetilUngrouped = h
                    .select("select * from v_detil_pembayaran_dana_rutin where trx_id=?", uuid)
                    .mapTo(DetilPembayaranDanaRutin.class)
                    .list();
            Map<String, List<DetilPembayaranDanaRutin>> detilGroupedByIdLeluhur = CollectionHelper.groupList(listDetilUngrouped, detil -> detil.leluhurSamanagara.uuid);
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, List<DetilPembayaranDanaRutin>> entry : detilGroupedByIdLeluhur.entrySet()) {
                List<DetilPembayaranDanaRutin> listDetil = entry.getValue();
                String namaLeluhur = listDetil.get(0).leluhurSamanagara.nama;

                YearMonth smallestMonth = listDetil.stream().map(x -> x.untukBulan).min(Comparator.naturalOrder()).get();
                YearMonth largestMonth = listDetil.stream().map(x -> x.untukBulan).max(Comparator.naturalOrder()).get();

                sb.append(String.format("Bayar samanagara ut leluhur %s dari %s s/d %s%n", namaLeluhur, smallestMonth, largestMonth));
            }

            return sb.toString();
        } else {
            List<DetilPembayaranDanaRutin> listDetil = h
                    .select("select * from v_detil_pembayaran_dana_rutin where trx_id=?", uuid)
                    .mapTo(DetilPembayaranDanaRutin.class)
                    .list();

            String jenisDana = listDetil.get(0).jenis.name();
            YearMonth smallestMonth = listDetil.stream().map(x -> x.untukBulan).min(Comparator.naturalOrder()).get();
            YearMonth largestMonth = listDetil.stream().map(x -> x.untukBulan).max(Comparator.naturalOrder()).get();

            return String.format("Bayar dana %s dari %s s/d %s", jenisDana, smallestMonth, largestMonth);
        }
    }

    public enum Type {
        samanagara, sosial_tetap;
    }
}
