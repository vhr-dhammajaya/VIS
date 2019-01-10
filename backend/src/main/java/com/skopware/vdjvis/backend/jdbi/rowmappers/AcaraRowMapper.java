package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.vdjvis.api.Acara;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AcaraRowMapper implements RowMapper<Acara> {
    @Override
    public Acara map(ResultSet rs, StatementContext ctx) throws SQLException {
        Acara x = new Acara();
        x.setUuid(rs.getString("id"));
        x.nama = (rs.getString("nama"));
        x.noUrut = (rs.getInt("no_urut"));
        x.active = (rs.getBoolean("active"));
        return x;
    }
}
