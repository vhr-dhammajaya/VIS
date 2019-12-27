package com.skopware.vdjvis.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PendaftaranDanaRutinDAO extends BaseCrudDAO<PendaftaranDanaRutin> {
    @SqlQuery("select * from pendaftaran_dana_rutin where id = ?")
    @Override
    PendaftaranDanaRutin get(String id);

    @SqlUpdate("insert into pendaftaran_dana_rutin(id, umat_id, tgl_daftar, nominal, tipe, active) " +
            "values(:uuid, :umat.uuid, :tglDaftar, :nominal, :tipe, 1)")
    @Override
    void create(@BindFields PendaftaranDanaRutin x);

    @SqlUpdate("update pendaftaran_dana_rutin set active = 1 where id = :uuid")
    @Override
    void update(@BindFields PendaftaranDanaRutin x);

    @SqlUpdate("update pendaftaran_dana_rutin set active = 0 where id = ?")
    @Override
    void delete(String id);
}
