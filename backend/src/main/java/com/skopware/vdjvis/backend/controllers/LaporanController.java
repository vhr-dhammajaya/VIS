package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple3;
import com.skopware.vdjvis.api.dto.*;
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
import java.time.Period;
import java.time.YearMonth;
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

            List<PendaftaranDanaRutin> listDanaRutin = selPendaftaranDanaRutin
                    .map((rs, ctx) -> {
                        PendaftaranDanaRutin x = new PendaftaranDanaRutin();

                        x.uuid = rs.getString("id");
                        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));
                        x.nominal = rs.getInt("nominal");
                        x.tipe = PendaftaranDanaRutin.Type.valueOf(rs.getString("tipe"));

                        x.umat = new Umat();
                        x.umat.uuid = rs.getString("umat_id");
                        x.umat.nama = rs.getString("nama_umat");
                        x.umat.noTelpon = rs.getString("no_telpon");
                        x.umat.alamat = rs.getString("alamat");

                        return x;
                    })
                    .list();
            List<StatusBayar> listStatusBayarDanaRutin = PendaftaranDanaRutin.computeStatusBayar(handle, listDanaRutin, todayMonth);

            for (int i = 0; i < listDanaRutin.size(); i++) {
                PendaftaranDanaRutin danaRutin = listDanaRutin.get(i);
                StatusBayar statusBayar = listStatusBayarDanaRutin.get(i);

                result.add(ObjectHelper.apply(new DtoOutputLaporanStatusDanaRutin(), x -> {
                    x.namaUmat = danaRutin.umat.nama;
                    x.noTelpon = danaRutin.umat.noTelpon;
                    x.alamat = danaRutin.umat.alamat;

                    x.jenisDana = danaRutin.tipe;

                    x.statusBayar = statusBayar.status;
                    x.strStatusBayar = statusBayar.strStatus;
                    x.diffInMonths = statusBayar.countBulan;
                    x.nominal = statusBayar.nominal;
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
            List<StatusBayar> listStatusBayarSamanagara = Leluhur.computeStatusBayar(handle, listLeluhur, todayMonth, listTarifSamanagara);

            for (int i = 0; i < listLeluhur.size(); i++) {
                Leluhur leluhur = listLeluhur.get(i);
                StatusBayar statusBayar = listStatusBayarSamanagara.get(i);

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

    @GET
    @Path("/pemasukan_pengeluaran")
    public List<DtoOutputLaporanPemasukanPengeluaran> computeLaporanPemasukanPengeluaran(@NotNull DtoInputLaporanPemasukanPengeluaran param) {
        try (Handle handle = jdbi.open()) {
            List<DtoOutputLaporanPemasukanPengeluaran> result = new ArrayList<>();

            YearMonth curr = param.startInclusive;
            while (!curr.isAfter(param.endInclusive)) {
                int ymCurr = DateTimeHelper.computeMySQLYearMonth(curr);
                int jmlPengeluaran = handle.select("select sum(nominal) from pengeluaran where extract(year_month from tgl_trx) = ?", ymCurr)
                        .mapTo(int.class)
                        .findOnly();
                int jmlPendapatanNonRutin = handle.select("select sum(nominal) from pendapatan where extract(year_month from tgl_trx) = ?", ymCurr)
                        .mapTo(int.class)
                        .findOnly();
                int jmlPendapatanRutin = handle.select("select sum(nominal) from pembayaran_dana_rutin where extract(year_month from ut_thn_bln) = ?", ymCurr)
                        .mapTo(int.class)
                        .findOnly();
                int jmlPendapatan = jmlPendapatanNonRutin + jmlPendapatanRutin;

                DtoOutputLaporanPemasukanPengeluaran x = new DtoOutputLaporanPemasukanPengeluaran();
                x.tahun = curr.getYear();
                x.bulan = curr.getMonthValue();
                x.pemasukan = jmlPendapatan;
                x.pengeluaran = jmlPengeluaran;
                result.add(x);

                curr = curr.plusMonths(1);
            }

            return result;
        }
    }

    @GET
    @Path("/absensi_umat")
    public List<DtoOutputLaporanAbsensiUmat> computeLaporanAbsensiUmat() {
        List<DtoOutputLaporanAbsensiUmat> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        try (Handle handle = jdbi.open()) {
            result = handle.select("select u.uuid, u.nama, u.alamat, u.no_telpon, max(k.tgl) as tgl_terakhir_hadir" +
                    " from umat u" +
                    " left join kehadiran k on k.umat_id = u.uuid" +
                    " group by u.uuid, u.nama, u.alamat, u.no_telpon" +
                    " order by tgl_terakhir_hadir")
                    .map((rs, ctx) -> {
                        DtoOutputLaporanAbsensiUmat x = new DtoOutputLaporanAbsensiUmat();
                        x.namaUmat = rs.getString("nama");
                        x.alamat = rs.getString("alamat");
                        x.noTelpon = rs.getString("no_telpon");
                        x.tglTerakhirHadir = DateTimeHelper.toLocalDate(rs.getDate("tgl_terakhir_hadir"));
                        if (x.tglTerakhirHadir != null) {
                            x.sdhBerapaLamaAbsen = Period.between(x.tglTerakhirHadir, today);
                        }
                        else {
                            x.sdhBerapaLamaAbsen = null;
                        }
                        return x;
                    })
                    .list();
        }

        return result;
    }
}
