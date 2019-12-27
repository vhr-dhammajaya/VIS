package com.skopware.vdjvis.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.entities.Pendapatan;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PendapatanDAO extends BaseCrudDAO<Pendapatan> {
    @SqlQuery("select * from v_pendapatan where id = ?")
    @Override
    Pendapatan get(String id);

    @SqlUpdate("insert into pendapatan(id, umat_id, tgl_trx, nominal, channel, jenis_dana, keterangan, acara_id)" +
            " values(:uuid, :umat.uuid, :tglTransaksi, :nominal, :channel, :jenisDana, :keterangan, :acara.uuid)")
    @Override
    void create(@BindFields Pendapatan x);

    @SqlUpdate("update pendapatan set umat_id=:umat.uuid, tgl_trx=:tglTransaksi, nominal=:nominal, channel=:channel," +
            "jenis_dana=:jenisDana, keterangan=:keterangan, acara_id=:acara.uuid, correction_status=:correctionStatus," +
            "corr_req_reason=:correctionRequestReason where id=:uuid")
    @Override
    void update(@BindFields Pendapatan x);

    @SqlUpdate("update pendapatan set active = 0 where id = ?")
    @Override
    void delete(String id);
}
