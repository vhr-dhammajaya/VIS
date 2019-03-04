package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.Umat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UmatRowMapper implements RowMapper<Umat> {
    @Override
    public Umat map(ResultSet rs, StatementContext ctx) throws SQLException {
        Umat x = new Umat();
        x.setUuid(rs.getString("uuid"));
        x.nama = (rs.getString("nama"));
        x.alamat = (rs.getString("alamat"));
        x.kota = (rs.getString("kota"));
        x.kodePos = (rs.getString("kode_pos"));
        x.noTelpon = (rs.getString("no_telpon"));
        x.email = (rs.getString("email"));
        x.tempatLahir = (rs.getString("tempat_lahir"));
        x.tglLahir = (DateTimeHelper.toLocalDate(rs.getDate("tgl_lahir")));
        x.golDarah = (rs.getString("gol_darah"));
        x.jenisKelamin = (rs.getString("jenis_kelamin"));
        x.statusNikah = (rs.getString("status_nikah"));

        x.pendidikanTerakhir = (rs.getString("pendidikan_terakhir"));
        x.jurusan = (rs.getString("jurusan"));

        x.pekerjaan = (rs.getString("pekerjaan"));
        x.bidangUsaha = (rs.getString("bidang_usaha"));

        x.namaKerabat = (rs.getString("nama_kerabat"));
        x.alamatKerabat = (rs.getString("alamat_kerabat"));
        x.kotaKerabat = (rs.getString("kota_kerabat"));
        x.kodePosKerabat = (rs.getString("kode_pos_kerabat"));
        x.noTelpKerabat = (rs.getString("no_telp_kerabat"));

        x.namaUpasaka = (rs.getString("nama_upasaka"));
        x.penahbis = (rs.getString("penahbis"));
        x.tglPenahbisan = (DateTimeHelper.toLocalDate(rs.getDate("tgl_penahbisan")));

        return x;
    }
}
