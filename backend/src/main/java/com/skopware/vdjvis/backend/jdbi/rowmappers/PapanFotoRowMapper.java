package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.vdjvis.api.PapanFoto;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PapanFotoRowMapper implements RowMapper<PapanFoto> {
    @Override
    public PapanFoto map(ResultSet rs, StatementContext ctx) throws SQLException {
        PapanFoto x = new PapanFoto();
        x.setUuid(rs.getString("id"));
        x.nama = rs.getString("nama");
        x.width = rs.getInt("width");
        x.height = rs.getInt("height");
        return x;
    }
}
