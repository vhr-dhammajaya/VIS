package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple3;
import com.skopware.vdjvis.api.dto.DtoInputLaporanStatusDanaRutin;
import com.skopware.vdjvis.api.dto.DtoOutputLaporanStatusDanaRutin;
import com.skopware.vdjvis.api.entities.Leluhur;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import com.skopware.vdjvis.api.entities.StatusBayar;
import com.skopware.vdjvis.api.entities.Umat;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Path("/laporan")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LaporanController {
    private Jdbi jdbi;

    public LaporanController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Path("/status_dana_rutin")
    @GET
    public List<DtoOutputLaporanStatusDanaRutin> computeLaporanStatusDanaRutin(@NotNull DtoInputLaporanStatusDanaRutin rq) {
        String idUmat = rq.idUmat;

        List<DtoOutputLaporanStatusDanaRutin> result = new ArrayList<>();

        YearMonth todayMonth = YearMonth.now();

        try (Handle handle = jdbi.open()) {
            //#region hitung status dana sosial & tetap
            class PendaftaranDanaRutin2 {
                public String uuid;
                public LocalDate tglDaftar;
                public int nominal;
                public PendaftaranDanaRutin.Type tipe;

                public String idUmat;
                public String namaUmat;
                public String alamat;
                public String noTelpon;
            }

            Query selPendaftaranDanaRutin;
            if (idUmat != null) {
                selPendaftaranDanaRutin = handle.select("select p.*, u.nama as nama_umat, u.no_telpon, u.alamat" +
                        " from pendaftaran_dana_rutin p" +
                        " join umat u on u.uuid = p.umat_id" +
                        " where p.active=1 and p.umat_id=?", idUmat);
            }
            else {
                selPendaftaranDanaRutin = handle.select("select p.*, u.nama as nama_umat, u.no_telpon, u.alamat" +
                        " from pendaftaran_dana_rutin p" +
                        " join umat u on u.uuid = p.umat_id" +
                        " where p.active=1");
            }

            List<PendaftaranDanaRutin2> listDanaRutin = selPendaftaranDanaRutin
                    .map((rs, ctx) -> {
                        PendaftaranDanaRutin2 x = new PendaftaranDanaRutin2();

                        x.uuid = rs.getString("id");
                        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));
                        x.nominal = rs.getInt("nominal");
                        x.tipe = PendaftaranDanaRutin.Type.valueOf(rs.getString("tipe"));
                        x.idUmat = rs.getString("umat_id");
                        x.namaUmat = rs.getString("nama_umat");
                        x.noTelpon = rs.getString("no_telpon");
                        x.alamat = rs.getString("alamat");

                        return x;
                    })
                    .list();

            for (PendaftaranDanaRutin2 danaRutin : listDanaRutin) {
                Optional<LocalDate> lastPayment = handle.select("select max(ut_thn_bln) from pembayaran_dana_rutin where umat_id=? and dana_rutin_id=?", danaRutin.idUmat, danaRutin.uuid)
                        .mapTo(LocalDate.class)
                        .findFirst();
                YearMonth lastPaymentMonth;

                if (lastPayment.isPresent()) {
                    lastPaymentMonth = YearMonth.from(lastPayment.get());
                }
                else {
                    // have never paid yet
                    lastPaymentMonth = YearMonth.from(danaRutin.tglDaftar).minusMonths(1);
                }

                int statusBayar = lastPaymentMonth.compareTo(todayMonth); // 0=tepat waktu, -1=kurang bayar, 1=lebih bayar
                String jenisDana = danaRutin.tipe.name();
                String strStatusBayar;
                long diffInMonths;
                long totalRp;

                if (statusBayar < 0) {
                    strStatusBayar = "Kurang bayar";
                    diffInMonths = lastPaymentMonth.until(todayMonth, ChronoUnit.MONTHS);
                    totalRp = diffInMonths * danaRutin.nominal;
                }
                else if (statusBayar == 0)  {
                    strStatusBayar = "Tepat waktu";
                    diffInMonths = 0;
                    totalRp = 0;
                }
                else {
                    strStatusBayar = "Lebih bayar";
                    diffInMonths = todayMonth.until(lastPaymentMonth, ChronoUnit.MONTHS);
                    totalRp = 0;
                }

                String strStatusBayar2 = strStatusBayar;
                long diffInMonths2 = diffInMonths;
                long totalRp2 = totalRp;

                result.add(ObjectHelper.apply(new DtoOutputLaporanStatusDanaRutin(), x -> {
                    x.namaUmat = danaRutin.namaUmat;
                    x.noTelpon = danaRutin.noTelpon;
                    x.alamat = danaRutin.alamat;

                    x.jenisDana = PendaftaranDanaRutin.Type.valueOf(jenisDana);

                    x.statusBayar = statusBayar;
                    x.strStatusBayar = strStatusBayar2;
                    x.diffInMonths = diffInMonths2;
                    x.nominal = totalRp2;
                }));
            }
            //#endregion

            //#region hitung status iuran samanagara
            Query qSelLeluhur;

            if (idUmat != null) {
                qSelLeluhur = handle.select("select l.*, u.nama as nama_umat, u.no_telpon, u.alamat" +
                        " from leluhur_smngr l" +
                        " join umat u on u.uuid=l.umat_id" +
                        " where l.active=1 and l.umat_id=?", idUmat);
            }
            else {
                qSelLeluhur = handle.select("select l.*, u.nama as nama_umat, u.no_telpon, u.alamat" +
                        " from leluhur_smngr l" +
                        " join umat u on u.uuid=l.umat_id" +
                        " where l.active=1");
            }

            List<Leluhur> listLeluhur = qSelLeluhur
                    .map((rs, ctx) -> {
                        Leluhur x = new Leluhur();
                        x.uuid = rs.getString("uuid");
                        x.nama = rs.getString("nama");
                        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));

                        x.penanggungJawab = new Umat();
                        x.penanggungJawab.uuid = rs.getString("umat_id");
                        x.penanggungJawab.nama = rs.getString("nama_umat");
                        x.penanggungJawab.alamat = rs.getString("alamat");
                        x.penanggungJawab.noTelpon = rs.getString("no_telpon");
                        return x;
                    })
                    .list();

            List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara = Leluhur.fetchListTarifSamanagara(handle);
            List<StatusBayar> listStatusBayar = Leluhur.computeStatusBayar(handle, listLeluhur, todayMonth, listTarifSamanagara);

            for (int i = 0; i < listLeluhur.size(); i++) {
                Leluhur leluhur = listLeluhur.get(i);
                StatusBayar statusBayar = listStatusBayar.get(i);

                result.add(ObjectHelper.apply(new DtoOutputLaporanStatusDanaRutin(), x -> {
                    x.namaUmat = leluhur.penanggungJawab.nama;
                    x.noTelpon = leluhur.penanggungJawab.noTelpon;
                    x.alamat = leluhur.penanggungJawab.alamat;
                    x.namaLeluhur = leluhur.nama;

                    x.jenisDana = PendaftaranDanaRutin.Type.samanagara;

                    x.statusBayar = statusBayar.status;
                    x.strStatusBayar = statusBayar.strStatus;
                    x.diffInMonths = statusBayar.countBulan;
                    x.nominal = statusBayar.nominal;
                }));
            }
            //#endregion
        }

        // todo sort result
        // 1. kurang bayar > tepat waktu > lebih bayar
        // 2. nominal (descending / largest to smallest)
        Collections.sort(result, (a, b) -> {
            // statusBayar can be < -1 or > 1. Need to normalize so that all "Kurang bayar", "Lebih bayar" is -1, 1
            int statusBayarANormalized = a.statusBayar < 0? -1 : a.statusBayar == 0? 0 : 1;
            int statusBayarBNormalized = b.statusBayar < 0? -1 : b.statusBayar == 0? 0 : 1;

            int cmpStatusBayar = Integer.compare(statusBayarANormalized, statusBayarBNormalized);
            if (cmpStatusBayar == 0) {
                // compare nominal
                int cmpNominalDesc = Long.compare(a.nominal, b.nominal);
                return -cmpNominalDesc;
            }
            else {
                return cmpStatusBayar;
            }
        });

        return result;
    }
}
