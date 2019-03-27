package com.skopware.vdjvis.backend.jdbi.rowmappers;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.Leluhur;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;

public class DetilPembayaranDanaRutinRowMapper implements RowMapper<DetilPembayaranDanaRutin> {
    @Override
    public DetilPembayaranDanaRutin map(ResultSet rs, StatementContext ctx) throws SQLException {
        DetilPembayaranDanaRutin x = new DetilPembayaranDanaRutin();

        x.uuid = rs.getString("uuid");

        x.parentTrx = new PembayaranDanaRutin();
        x.parentTrx.uuid = rs.getString("trx_id");

        x.jenis = PendaftaranDanaRutin.Type.valueOf(rs.getString("jenis"));

        String danaRutinId = rs.getString("dana_rutin_id");
        if (danaRutinId != null) {
            x.danaRutin = new PendaftaranDanaRutin();
            x.danaRutin.uuid = danaRutinId;
        }

        String leluhurId = rs.getString("leluhur_id");
        if (leluhurId != null) {
            x.leluhurSamanagara = new Leluhur();
            x.leluhurSamanagara.uuid = leluhurId;
            x.leluhurSamanagara.nama = rs.getString("leluhur_nama");
        }

        LocalDate utThnBln = DateTimeHelper.toLocalDate(rs.getDate("ut_thn_bln"));
        x.untukBulan = YearMonth.from(utThnBln);

        x.nominal = rs.getInt("nominal");

        return x;
    }
}
