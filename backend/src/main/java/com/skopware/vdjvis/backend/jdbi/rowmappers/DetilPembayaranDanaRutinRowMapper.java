package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.Leluhur;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import com.skopware.vdjvis.api.entities.Umat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;

public class DetilPembayaranDanaRutinRowMapper implements RowMapper<DetilPembayaranDanaRutin> {
    @Override
    public DetilPembayaranDanaRutin map(ResultSet rs, StatementContext ctx) throws SQLException {
        DetilPembayaranDanaRutin x = new DetilPembayaranDanaRutin();

        x.uuid = rs.getString("uuid");

        x.umat = new Umat();
        x.umat.uuid = rs.getString("umat_id");
        x.umat.nama = rs.getString("umat_nama");

        x.jenis = PendaftaranDanaRutin.Type.valueOf(rs.getString("jenis"));

        String idPendaftaranDanaRutin = rs.getString("dana_rutin_id");
        if (idPendaftaranDanaRutin != null) {
            x.danaRutin = new PendaftaranDanaRutin();
            x.danaRutin.uuid = idPendaftaranDanaRutin;
        }

        String idLeluhur = rs.getString("leluhur_id");
        if (idLeluhur != null) {
            x.leluhurSamanagara = new Leluhur();
            x.leluhurSamanagara.uuid = idLeluhur;
            x.leluhurSamanagara.nama = rs.getString("leluhur_nama");
        }

        int utThn = rs.getInt("ut_thn");
        int utBln = rs.getInt("ut_bln");
        x.untukBulan = YearMonth.of(utThn, utBln);

        x.nominal = rs.getInt("nominal");

        x.tglTrans = DateTimeHelper.toLocalDate(rs.getDate("tgl"));

        x.channel = rs.getString("channel");

        x.keterangan = rs.getString("keterangan");

        x.correctionStatus = rs.getBoolean("correction_status");
        x.correctionRequestReason = rs.getString("corr_req_reason");

        return x;
    }
}
