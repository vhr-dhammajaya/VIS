package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.Leluhur;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LeluhurRowMapper implements RowMapper<Leluhur> {
    @Override
    public Leluhur map(ResultSet rs, StatementContext ctx) throws SQLException {
        Leluhur x = new Leluhur();
        x.setUuid(rs.getString("uuid"));
        x.nama = rs.getString("nama");
        x.tempatLahir = rs.getString("tempat_lahir");
        x.tglLahir = DateTimeHelper.toLocalDate(rs.getDate("tgl_lahir"));
        x.tempatMati = rs.getString("tempat_mati");
        x.tglMati = DateTimeHelper.toLocalDate(rs.getDate("tgl_mati"));
        x.hubunganDgnUmat = rs.getString("hubungan_dgn_umat");
        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));
        x.penanggungJawabId = rs.getString("umat_id");
        x.cellFotoId = rs.getString("cell_papan_id");
        return x;
    }
}
