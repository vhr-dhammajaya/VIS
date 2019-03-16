package com.skopware.vdjvis.backend.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface DetilPembayaranDanaRutinDAO extends BaseCrudDAO<DetilPembayaranDanaRutin> {
    @SqlQuery("select * from v_pembayaran_dana_rutin where uuid=?")
    @Override
    DetilPembayaranDanaRutin get(String uuid);

    @SqlUpdate()
    @Override
    void create(DetilPembayaranDanaRutin x);

    @SqlUpdate("update pembayaran_dana_rutin set nominal=:nominal, tgl=:tglTrans, channel=:channel," +
            "keterangan=:keterangan, correction_status=:correctionStatus, corr_req_reason=:correctionRequestReason" +
            " where uuid=:uuid")
    @Override
    void update(@BindFields DetilPembayaranDanaRutin x);

    @SqlUpdate("update pembayaran_dana_rutin set active=0 where uuid=?")
    @Override
    void delete(String uuid);
}
