package com.skopware.vdjvis.jdbi.rowmappers;

import com.skopware.vdjvis.api.entities.User;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User map(ResultSet rs, StatementContext ctx) throws SQLException {
        User x = new User();
        x.setUuid(rs.getString("id"));
        x.username = rs.getString("username");
        x.nama = rs.getString("nama");
        x.tipe = User.Type.valueOf(rs.getString("tipe"));
        return x;
    }
}
