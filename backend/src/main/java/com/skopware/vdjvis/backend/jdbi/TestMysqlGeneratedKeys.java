package com.skopware.vdjvis.backend.jdbi;

import com.skopware.vdjvis.api.Acara;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class TestMysqlGeneratedKeys {
    public static void main(String[] args) {
        Jdbi jdbi = Jdbi.create("jdbc:mysql://localhost:3306/vis_vdj?user=root&password=T3kn0s!123");
        jdbi.installPlugin(new SqlObjectPlugin());
//        jdbi.registerRowMapper(Acara.class, (rs, ctx) -> {
//            return new Acara(rs.getInt("id"), rs.getString("nama"), rs.getInt("no_urut"), rs.getBoolean("active"));
//        });

        try (Handle handle = jdbi.open()) {
            AcaraDAO dao = handle.attach(AcaraDAO.class);
//            int a = dao.create("Waisak");
//            System.out.println(a);
        }
    }
}
