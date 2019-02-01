package com.skopware.vdjvis.backend.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.Pendapatan;
import com.skopware.vdjvis.api.Pengeluaran;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PengeluaranDAO extends BaseCrudDAO<Pengeluaran> {
    @SqlQuery("select * from v_pengeluaran where id = ?")
    @Override
    Pengeluaran get(String id);

    @SqlUpdate("insert into pengeluaran(id, tgl_trx, nominal, keterangan, acara_id)" +
            " values(:uuid, :tglTransaksi, :nominal, :keterangan, :acaraId)")
    @Override
    void create(@BindBean Pengeluaran x);

    @SqlUpdate("update pengeluaran set tgl_trx=:tglTransaksi, nominal=:nominal," +
            "keterangan=:keterangan, acara_id=:acaraId where id=:uuid")
    @Override
    void update(@BindBean Pengeluaran x);

    @SqlUpdate("update pengeluaran set active = 0 where id = ?")
    @Override
    void delete(String id);
}
