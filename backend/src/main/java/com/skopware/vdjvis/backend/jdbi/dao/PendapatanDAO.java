package com.skopware.vdjvis.backend.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.Pendapatan;
import com.skopware.vdjvis.api.Umat;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PendapatanDAO extends BaseCrudDAO<Pendapatan> {
    @SqlQuery("select * from v_pendapatan where id = ?")
    @Override
    Pendapatan get(String id);

    @SqlUpdate("insert into pendapatan(id, umat_id, tgl_trx, nominal, channel, jenis_dana, keterangan, acara_id)" +
            " values(:uuid, :umatId, :tglTransaksi, :nominal, :channel, :jenisDana, :keterangan, :acaraId)")
    @Override
    void create(@BindBean Pendapatan x);

    @SqlUpdate("update pendapatan set umat_id=:umatId, tgl_trx=:tglTransaksi, nominal=:nominal, channel=:channel," +
            "jenis_dana=:jenisDana, keterangan=:keterangan, acara_id=:acaraId where id=:uuid")
    @Override
    void update(@BindBean Pendapatan x);

    @SqlUpdate("update pendapatan set active = 0 where id = ?")
    @Override
    void delete(String id);
}
