package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.vdjvis.api.Leluhur;
import com.skopware.vdjvis.api.Umat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LeluhurRowMapper2 implements RowMapper<Leluhur> {
    public LeluhurRowMapper mapper1;

    @Override
    public Leluhur map(ResultSet rs, StatementContext ctx) throws SQLException {
        Leluhur x = mapper1.map(rs, ctx);
        x.penanggungJawab = new Umat();
        x.penanggungJawab.nama = rs.getString("umat_nama");
        return x;
    }
}
