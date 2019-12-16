package com.skopware.vdjvis.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import com.skopware.vdjvis.api.entities.Umat;
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
        x.umat.uuid = rs.getString("umat_id");
        x.tipe = DetilPembayaranDanaRutin.Type.valueOf(rs.getString("tipe"));
        x.nominal = rs.getInt("nominal");
        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));
        return x;
    }
}
