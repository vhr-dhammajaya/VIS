package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.TarifSamanagara;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TarifSamanagaraRowMapper implements RowMapper<TarifSamanagara> {
    @Override
    public TarifSamanagara map(ResultSet rs, StatementContext ctx) throws SQLException {
        TarifSamanagara x = new TarifSamanagara();
        x.setUuid(rs.getString("id"));
        x.startDate = DateTimeHelper.toLocalDate(rs.getDate("start_date"));
        x.endDate = DateTimeHelper.toLocalDate(rs.getDate("end_date"));
        x.nominal = rs.getInt("nominal");
        return x;
    }
}
