package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.Acara;
import com.skopware.vdjvis.api.entities.Pendapatan;
import com.skopware.vdjvis.api.entities.Umat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PendapatanRowMapper implements RowMapper<Pendapatan> {
    @Override
    public Pendapatan map(ResultSet rs, StatementContext ctx) throws SQLException {
        Pendapatan x = new Pendapatan();
        x.setUuid(rs.getString("id"));

        String umat_id = rs.getString("umat_id");
        if (umat_id != null) {
            x.umat = new Umat();
            x.umat.uuid = umat_id;
            x.umat.nama = rs.getString("umat_nama");
        }

        x.tglTransaksi = DateTimeHelper.toLocalDate(rs.getDate("tgl_trx"));
        x.nominal = rs.getInt("nominal");
        x.channel = rs.getString("channel");
        x.jenisDana = Pendapatan.JenisDana.valueOf(rs.getString("jenis_dana"));
        x.keterangan = rs.getString("keterangan");

        String acara_id = rs.getString("acara_id");
        if (acara_id != null) {
            x.acara = new Acara();
            x.acara.uuid = acara_id;
            x.acara.nama = rs.getString("acara_nama");
        }

        x.correctionStatus = rs.getBoolean("correction_status");
        x.correctionRequestReason = rs.getString("corr_req_reason");

        return x;
    }
}
