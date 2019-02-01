package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.Acara;
import com.skopware.vdjvis.api.Pendapatan;
import com.skopware.vdjvis.api.Pengeluaran;
import com.skopware.vdjvis.api.Umat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PengeluaranRowMapper implements RowMapper<Pengeluaran> {
    @Override
    public Pengeluaran map(ResultSet rs, StatementContext ctx) throws SQLException {
        Pengeluaran x = new Pengeluaran();
        x.setUuid(rs.getString("id"));

        x.tglTransaksi = DateTimeHelper.toLocalDate(rs.getDate("tgl_trx"));
        x.nominal = rs.getInt("nominal");
        x.keterangan = rs.getString("keterangan");

        x.acaraId = rs.getString("acara_id");
        x.acara = new Acara();
        x.acara.nama = rs.getString("acara_nama");

        return x;
    }
}
