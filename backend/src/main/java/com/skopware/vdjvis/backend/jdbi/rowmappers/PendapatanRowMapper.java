package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.Acara;
import com.skopware.vdjvis.api.Pendapatan;
import com.skopware.vdjvis.api.Umat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PendapatanRowMapper implements RowMapper<Pendapatan> {
    @Override
    public Pendapatan map(ResultSet rs, StatementContext ctx) throws SQLException {
        Pendapatan x = new Pendapatan();
        x.setUuid(rs.getString("id"));

        x.umatId = rs.getString("umat_id");
        x.umat = new Umat();
        x.umat.nama = rs.getString("umat_nama");

        x.tglTransaksi = DateTimeHelper.toLocalDate(rs.getDate("tgl_trx"));
        x.nominal = rs.getInt("nominal");
        x.channel = rs.getString("channel");
        x.jenisDana = Pendapatan.JenisDana.valueOf(rs.getString("jenis_dana"));
        x.keterangan = rs.getString("keterangan");

        x.acaraId = rs.getString("acara_id");
        x.acara = new Acara();
        x.acara.nama = rs.getString("acara_nama");

        return x;
    }
}
