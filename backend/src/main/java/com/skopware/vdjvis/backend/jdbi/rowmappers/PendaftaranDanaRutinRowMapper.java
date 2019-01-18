package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.PendaftaranDanaRutin;
import com.skopware.vdjvis.api.Umat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PendaftaranDanaRutinRowMapper implements RowMapper<PendaftaranDanaRutin> {
    @Override
    public PendaftaranDanaRutin map(ResultSet rs, StatementContext ctx) throws SQLException {
        PendaftaranDanaRutin x = new PendaftaranDanaRutin();
        x.uuid = rs.getString("id");
        x.umat = new Umat();
        x.umatId = x.umat.uuid = rs.getString("umat_id");
        x.tipe = rs.getString("tipe");
        x.nominal = rs.getInt("nominal");
        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));
        return x;
    }
}