package com.skopware.vdjvis.backend.jdbi;

import java.sql.*;

public class TestMysqlGeneratedKeys2 {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vis_vdj?user=root&password=T3kn0s!123")) {
            try (PreparedStatement stmt = conn.prepareStatement("insert into acara(nama, no_urut) values('Vesak', 1)", new int[] {1, 2, 3, 4})) {
//            try (PreparedStatement stmt = conn.prepareStatement("insert into acara(nama, no_urut) values('Vesak', 1)", new String[] {"id", "nama", "no_urut", "active"})) {
                stmt.execute();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    while (rs.next()) {
                        /*
                        As of 2018-10-04, MySQL only support returning value for 1 auto increment column
                        It must be fetched using rs.getInt(1)

                        You can't use
                        rs.getInt("id") - even if you use conn.prepareStatement(sql, String[] colNames)
                        rs.getString(2) - even if you use conn.prepareStatement(sql, int[] indexes)
                        They will cause exception
                         */
                        int id = rs.getInt(1);
                        System.out.println(id);
                        String nama = rs.getString(2);
                        int noUrut = rs.getInt(3);
                        boolean active = rs.getBoolean(4);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
