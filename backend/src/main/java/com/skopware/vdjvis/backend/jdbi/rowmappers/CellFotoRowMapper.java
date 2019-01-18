package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.ObjectHelper;
import com.skopware.vdjvis.api.CellFoto;
import com.skopware.vdjvis.api.Leluhur;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CellFotoRowMapper implements RowMapper<CellFoto> {
    @Override
    public CellFoto map(ResultSet rs, StatementContext ctx) throws SQLException {
        CellFoto x = new CellFoto();
        x.setUuid(rs.getString("id"));
        x.row = rs.getInt("row");
        x.col = rs.getInt("col");

        String leluhurId = rs.getString("leluhur_smngr_id");
        if (leluhurId == null) {
            x.leluhur = null;
        }
        else {
            x.leluhur = new Leluhur();
            x.leluhur.uuid = leluhurId;
            x.leluhur.nama = rs.getString("leluhur_nama");
            x.leluhur.cellFotoId = x.uuid;
        }

        x.papanId = rs.getString("papan_smngr_id");
        return x;
    }
}
