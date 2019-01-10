package com.skopware.vdjvis.backend.jdbi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateSqliteCreateTableScript {
    private static final String DB_SCHEMA = "vis_vdj";


    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vis_vdj?user=root&password=T3kn0s!123")) {
            List<String> tableNames = new ArrayList<>();

            try (Statement stmtGetTableNames = conn.createStatement()) {
                try (ResultSet rs = stmtGetTableNames.executeQuery("select table_name from information_schema.tables where table_schema = '" + DB_SCHEMA + "'")) {
                    while (rs.next()) {
                        tableNames.add(rs.getString("table_name"));
                    }
                }
            }

            try (PreparedStatement stmtGetColumns = conn.prepareStatement("select column_name, data_type from information_schema.columns where table_schema = ? and table_name = ?")) {
                stmtGetColumns.setString(1, DB_SCHEMA);

                for (String table : tableNames) {
                    stmtGetColumns.setString(2, table);

                    try (ResultSet rs = stmtGetColumns.executeQuery()) {
                        String columnName = rs.getString("column_name");
                        String dataType = rs.getString("data_type");

                        String sqliteType;
                        switch (dataType) {
                            case "int":
                            case "tinyint":
                                sqliteType = "integer";
                                break;
                            case "varchar":
                            case "date":
                            case "decimal":
                            case "enum":
                            case "json":
                            case "timestamp":
                                sqliteType = "text";
                                break;
                            default:
                                throw new RuntimeException("Unsuported data type " + dataType);
                        }
                    }
                }

            }
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

            throw new RuntimeException(e);
        }
    }
}
