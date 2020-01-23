package com.skopware.vdjvis.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.entities.Pengeluaran;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PengeluaranDAO extends BaseCrudDAO<Pengeluaran> {
    @SqlQuery("select * from v_pengeluaran where id = ?")
    @Override
    Pengeluaran get(String id);

    @SqlUpdate("insert into pengeluaran(id, tgl_trx, penerima, nominal, keterangan, acara_id, user_id)" +
            " values(:uuid, :tglTransaksi, :penerima, :nominal, :keterangan, :acara.uuid, :user.uuid)")
    @Override
    void create(@BindFields Pengeluaran x);

    @SqlUpdate("update pengeluaran set tgl_trx=:tglTransaksi, penerima=:penerima, nominal=:nominal," +
            "keterangan=:keterangan, acara_id=:acara.uuid where id=:uuid")
    @Override
    void update(@BindFields Pengeluaran x);

    @SqlUpdate("update pengeluaran set active = 0 where id = ?")
    @Override
    void delete(String id);
}
