package com.skopware.vdjvis.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.api.entities.User;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PembayaranDanaRutinRowMapper implements RowMapper<PembayaranDanaRutin> {
    @Override
    public PembayaranDanaRutin map(ResultSet rs, StatementContext ctx) throws SQLException {
        PembayaranDanaRutin x = new PembayaranDanaRutin();

        x.uuid = rs.getString("uuid");
        x.umat = new Umat();
        x.umat.uuid = rs.getString("umat_id");
        x.umat.nama = rs.getString("umat_nama");
        x.user = new User();
        x.user.uuid = rs.getString("user_id");
        x.user.nama = rs.getString("user_nama");
        x.tipe = PembayaranDanaRutin.Type.valueOf(rs.getString("tipe"));
        x.tgl = DateTimeHelper.toLocalDate(rs.getDate("tgl"));
        x.noSeq = rs.getInt("no_seq");
        x.totalNominal = rs.getInt("total_nominal");
        x.channel = rs.getString("channel");
        x.keterangan = rs.getString("keterangan");
        x.correctionStatus = rs.getBoolean("correction_status");
        x.correctionRequestReason = rs.getString("corr_req_reason");

        return x;
    }
}
