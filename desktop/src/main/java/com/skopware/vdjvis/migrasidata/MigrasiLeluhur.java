package com.skopware.vdjvis.migrasidata;

import com.skopware.javautils.Tuple2;
import com.skopware.vdjvis.api.entities.CellFoto;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.io.FileReader;
import java.util.*;

public class MigrasiLeluhur {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.load(new FileReader("datamigration.properties"));
        String srcJdbcUrl = props.getProperty("srcJdbcUrl");
        String srcUsername = props.getProperty("srcUsername");
        String srcPassword = props.getProperty("srcPassword");
        String destJdbcUrl = props.getProperty("destJdbcUrl");
        String destUsername = props.getProperty("destUsername");
        String destPassword = props.getProperty("destPassword");

        Jdbi sqlServerJdbi = Jdbi.create(srcJdbcUrl, srcUsername, srcPassword);
        Jdbi mysqlJdbi = Jdbi.create(destJdbcUrl, destUsername, destPassword);

        try (Handle srcConn = sqlServerJdbi.open(); Handle destConn = mysqlJdbi.open()) {
            List<SrcLeluhur> srcListLeluhur = srcConn.select("select * from pendaftaran_samanagara")
                    .map((rs, ctx) -> {
                        SrcLeluhur x = new SrcLeluhur();
                        x.idLeluhur = rs.getString("no_bukti");
                        x.tglDaftar = rs.getDate("tanggal");
                        x.idPenanggungJawab = rs.getString("id_umat");
                        x.nama = rs.getString("nama_mendiang");
                        x.tempatLahir = rs.getString("tempat_lahir");
                        x.tglLahir = rs.getDate("tanggal_lahir");
                        x.tempatMeninggal = rs.getString("tempat_meninggal");
                        x.tglMeninggal = rs.getDate("tanggal_meninggal");
                        x.hubungan = rs.getString("nama_hubungan_keluarga");
                        x.blok = rs.getString("nama_blok");
                        return x;
                    })
                    .list();
            destConn.useTransaction(destConn2 -> {
                for (SrcLeluhur x : srcListLeluhur) {
                    int param = 0;

                    String uuid = UUID.randomUUID().toString();
                    Optional<String> idUmat = destConn2.select("select uuid from umat where id_lama=?", x.idPenanggungJawab)
                            .mapTo(String.class)
                            .findFirst();

                    if (idUmat.isPresent()) {
                        destConn2.createUpdate("insert into leluhur_smngr(uuid, nama," +
                                "tempat_lahir, tgl_lahir, tempat_mati, tgl_mati," +
                                "hubungan_dgn_umat, tgl_daftar, umat_id)" +
                                " values(?, ?," +
                                "?, ?, ?, ?," +
                                "?, ?, ?)")
                                .bind(param++, uuid)
                                .bind(param++, x.nama)
                                .bind(param++, x.tempatLahir)
                                .bind(param++, x.tglLahir)
                                .bind(param++, x.tempatMeninggal)
                                .bind(param++, x.tglMeninggal)
                                .bind(param++, x.hubungan)
                                .bind(param++, x.tglDaftar)
                                .bind(param++, idUmat.get())
                                .execute();

                        if (x.blok != null) {
                            String namaPapan = x.blok.substring(0, 1);
                            int noCell = Integer.parseInt(x.blok.substring(1));
                            int boardWidth = destConn2.select("select width from papan_smngr where id=?", namaPapan)
                                    .mapTo(int.class)
                                    .findOnly();
                            Tuple2<Integer, Integer> rowCol = CellFoto.convertSeqNumToRowAndCol(noCell, boardWidth);

                            destConn2.createUpdate("update cell_papan set leluhur_smngr_id=?" +
                                    " where papan_smngr_id=? and `row`=? and col=?")
                                    .bind(0, uuid)
                                    .bind(1, namaPapan)
                                    .bind(2, rowCol.val1)
                                    .bind(3, rowCol.val2)
                                    .execute();
                            destConn2.createUpdate("update leluhur_smngr set cell_papan_id=? where uuid=?")
                                    .bind(0, namaPapan + " " + rowCol.val1 + " " + rowCol.val2)
                                    .bind(1, uuid)
                                    .execute();
                        }
                    }
                }
            });
        }
    }

    static class SrcLeluhur {
        String idLeluhur;
        Date tglDaftar;
        String idPenanggungJawab;
        String nama;
        String tempatLahir;
        Date tglLahir;
        String tempatMeninggal;
        Date tglMeninggal;
        String hubungan;
        String blok;
    }
}
