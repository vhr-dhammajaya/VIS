package com.skopware.vdjvis.migrasidata;

import com.skopware.javautils.DateTimeHelper;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.io.FileReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class MigrasiAbsensiUmat {
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
            List<SrcAbsensiUmat> srcAbsensiUmat = srcConn.select("select distinct d.id_umat, a.tanggal" +
                    " from absensi_umat a" +
                    " join absensi_umat_detail d on d.no_bukti=a.no_bukti" +
                    " where datepart(weekday, a.tanggal)=1") //
                    .map((rs, ctx) -> {
                        SrcAbsensiUmat x = new SrcAbsensiUmat();
                        x.idUmat = rs.getString("id_umat");
                        x.tgl = DateTimeHelper.toLocalDate(rs.getDate("tanggal"));
                        return x;
                    })
                    .list();
            destConn.useTransaction(destConn2 -> {
                for (SrcAbsensiUmat x : srcAbsensiUmat) {
                    int param = 0;

                    Optional<String> idUmat = destConn2.select("select uuid from umat where id_lama=?", x.idUmat)
                            .mapTo(String.class)
                            .findFirst();
                    if (idUmat.isPresent()) {
                        destConn2.createUpdate("insert into kehadiran(umat_id, tgl) values(?, ?)")
                                .bind(param++, idUmat.get())
                                .bind(param++, x.tgl)
                                .execute();
                    }
                }
            });
        }
    }

    static class SrcAbsensiUmat {
        String idUmat;
        LocalDate tgl;
    }
}
