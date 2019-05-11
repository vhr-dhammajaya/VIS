package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.Acara;
import com.skopware.vdjvis.api.entities.Pengeluaran;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PengeluaranRowMapper implements RowMapper<Pengeluaran> {
    @Override
    public Pengeluaran map(ResultSet rs, StatementContext ctx) throws SQLException {
        Pengeluaran x = new Pengeluaran();
        x.setUuid(rs.getString("id"));

        x.noSeq = rs.getInt("no_seq");
        x.tglTransaksi = DateTimeHelper.toLocalDate(rs.getDate("tgl_trx"));
        x.penerima = rs.getString("penerima");
        x.nominal = rs.getInt("nominal");
        x.keterangan = rs.getString("keterangan");

        String acara_id = rs.getString("acara_id");
        if (acara_id != null) {
            x.acara = new Acara();
            x.acara.uuid = acara_id;
            x.acara.nama = rs.getString("acara_nama");
        }

        return x;
    }
}
