package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.Siswa;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SiswaRowMapper implements RowMapper<Siswa> {
    @Override
    public Siswa map(ResultSet rs, StatementContext ctx) throws SQLException {
        Siswa x = new Siswa();

        x.uuid = rs.getString("uuid");

        x.nama = rs.getString("nama");

        x.namaAyah = rs.getString("nama_ayah");
        x.namaIbu = rs.getString("nama_ibu");

        x.alamat = rs.getString("alamat");
        x.kota = rs.getString("kota");

        x.noTelpon = rs.getString("no_telpon");

        x.tempatLahir = rs.getString("tempat_lahir");
        x.tglLahir = DateTimeHelper.toLocalDate(rs.getDate("tgl_lahir"));

        x.sekolah = rs.getString("sekolah");
        x.alamatSekolah = rs.getString("alamat_sekolah");

        x.idBarcode = rs.getString("id_barcode");
        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));

        return x;
    }
}
