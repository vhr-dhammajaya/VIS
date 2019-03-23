package com.skopware.vdjvis.migrasidata;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.vdjvis.api.entities.Umat;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.load(new FileReader("config.properties"));
        String srcJdbcUrl = props.getProperty("srcJdbcUrl");
        String srcUsername = props.getProperty("srcUsername");
        String srcPassword = props.getProperty("srcPassword");
        String destJdbcUrl = props.getProperty("destJdbcUrl");
        String destUsername = props.getProperty("destUsername");
        String destPassword = props.getProperty("destPassword");

        Jdbi sqlServerJdbi = Jdbi.create(srcJdbcUrl, srcUsername, srcPassword);
        Jdbi mysqlJdbi = Jdbi.create(destJdbcUrl, destUsername, destPassword);

        try (Handle srcConn = sqlServerJdbi.open(); Handle destConn = mysqlJdbi.open()) {
            List<Umat> srcListUmat = srcConn.select("select * from umat")
                    .map((rs, ctx) -> {
                        Umat x = new Umat();
                        x.uuid = UUID.randomUUID().toString();
                        x.idBarcode = rs.getString("id_umat");
                        x.nama = rs.getString("nama");
                        x.alamat = rs.getString("alamat");
                        x.kota = rs.getString("nama_kota");
                        x.kodePos = rs.getString("kodepos");
                        x.noTelpon = joinNoTelpon(rs, "gsm1", "gsm2", "cdma1", "cdma2", "telepon_rumah", "telepon_kantor");
                        x.email = rs.getString("email");
                        x.tempatLahir = rs.getString("tempat_lahir");
                        x.tglLahir = DateTimeHelper.toLocalDate(rs.getDate("tanggal_lahir"));

                        x.golDarah = rs.getString("golongan_darah");
                        if ("X".equals(x.golDarah)) {
                            x.golDarah = null;
                        }

                        x.jenisKelamin = rs.getString("jenis_kelamin");
                        switch (x.jenisKelamin) {
                            case "P":
                                x.jenisKelamin = "L";
                                break;
                            case "W":
                                x.jenisKelamin = "P";
                                break;
                        }

                        x.statusNikah = rs.getString("nama_status_nikah");
                        x.pendidikanTerakhir = rs.getString("nama_pendidikan");
                        x.jurusan = rs.getString("nama_jurusan");
                        x.pekerjaan = rs.getString("nama_pekerjaan");
                        x.bidangUsaha = rs.getString("nama_bidang_usaha");
                        x.namaKerabat = rs.getString("kt_nama");
                        x.alamatKerabat = rs.getString("kt_alamat");
                        x.kotaKerabat = rs.getString("kt_nama_kota");
                        x.kodePosKerabat = rs.getString("kt_kodepos");
                        x.noTelpKerabat = joinNoTelpon(rs, "kt_gsm1", "kt_gsm2", "kt_cdma1", "kt_cdma2", "kt_telepon_rumah", "kt_telepon_kantor");
                        x.namaUpasaka = rs.getString("nama_upasaka_sika");
                        x.penahbis = rs.getString("oleh");
                        x.tglPenahbisan = DateTimeHelper.toLocalDate(rs.getDate("tanggal_upasaka"));
                        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tanggal_daftar"));

                        return x;
                    })
                    .list();

            destConn.useTransaction(destConn2 -> {
                for (Umat x : srcListUmat) {
                    int param = 0;

                    System.out.println(x.nama);
//                    if (1==1)
//                        continue;

                    destConn2.createUpdate("insert into umat(uuid," +
                            "nama, alamat, kota, kode_pos, no_telpon, email," +
                            "tempat_lahir, tgl_lahir," +
                            "gol_darah, jenis_kelamin," +
                            "status_nikah," +
                            "pendidikan_terakhir, jurusan," +
                            "pekerjaan, bidang_usaha," +
                            "nama_kerabat, alamat_kerabat, kota_kerabat, kode_pos_kerabat, no_telp_kerabat," +
                            "nama_upasaka, penahbis, tgl_penahbisan," +
                            "active, id_barcode, tgl_daftar)" +
                            " values(?," +
                            "?, ?, ?, ?, ?, ?," +
                            "?, ?," +
                            "?, ?," +
                            "?," +
                            "?, ?," +
                            "?, ?," +
                            "?, ?, ?, ?, ?," +
                            "?, ?, ?," +
                            "1, ?, ?)")
                            .bind(param++, x.uuid)
                            .bind(param++, x.nama)
                            .bind(param++, x.alamat)
                            .bind(param++, x.kota)
                            .bind(param++, x.kodePos)
                            .bind(param++, x.noTelpon)
                            .bind(param++, x.email)
                            .bind(param++, x.tempatLahir)
                            .bind(param++, x.tglLahir)
                            .bind(param++, x.golDarah)
                            .bind(param++, x.jenisKelamin)
                            .bind(param++, x.statusNikah)
                            .bind(param++, x.pendidikanTerakhir)
                            .bind(param++, x.jurusan)
                            .bind(param++, x.pekerjaan)
                            .bind(param++, x.bidangUsaha)
                            .bind(param++, x.namaKerabat)
                            .bind(param++, x.alamatKerabat)
                            .bind(param++, x.kotaKerabat)
                            .bind(param++, x.kodePosKerabat)
                            .bind(param++, x.noTelpKerabat)
                            .bind(param++, x.namaUpasaka)
                            .bind(param++, x.penahbis)
                            .bind(param++, x.tglPenahbisan)
                            .bind(param++, x.idBarcode)
                            .bind(param++, x.tglDaftar)
                            .execute();
                }
            });
        }
    }

    public static String joinNoTelpon(ResultSet rs, String... kolomNoTelpon) {
        List<String> listNoTelp = ObjectHelper.apply(new ArrayList<>(), x -> {
            try {
                x.add(rs.getString("gsm1"));
                x.add(rs.getString("gsm2"));
                x.add(rs.getString("cdma1"));
                x.add(rs.getString("cdma2"));
                x.add(rs.getString("telepon_rumah"));
                x.add(rs.getString("telepon_kantor"));
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        StringJoiner sjNoTelp = new StringJoiner(", ");
        for (String noTelp : listNoTelp) {
            if (noTelp != null) {
                sjNoTelp.add(noTelp);
            }
        }

        return sjNoTelp.toString();
    }
}
