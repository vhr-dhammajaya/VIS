package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.CellFoto;
import com.skopware.vdjvis.api.entities.Leluhur;
import com.skopware.vdjvis.api.entities.Umat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LeluhurRowMapper implements RowMapper<Leluhur> {
    @Override
    public Leluhur map(ResultSet rs, StatementContext ctx) throws SQLException {
        Leluhur x = new Leluhur();
        x.setUuid(rs.getString("uuid"));
        x.nama = rs.getString("nama");
        x.tempatLahir = rs.getString("tempat_lahir");
        x.tglLahir = DateTimeHelper.toLocalDate(rs.getDate("tgl_lahir"));
        x.tempatMati = rs.getString("tempat_mati");
        x.tglMati = DateTimeHelper.toLocalDate(rs.getDate("tgl_mati"));
        x.hubunganDgnUmat = rs.getString("hubungan_dgn_umat");
        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));

        String umat_id = rs.getString("umat_id");
        if (umat_id != null) {
            x.penanggungJawab = new Umat();
            x.penanggungJawab.uuid = umat_id;
            x.penanggungJawab.nama = rs.getString("umat_nama");
        }

        String cell_papan_id = rs.getString("cell_papan_id");
        if (cell_papan_id != null) {
            x.cellFoto = new CellFoto();
            x.cellFoto.uuid = cell_papan_id;
        }
        return x;
    }
}
