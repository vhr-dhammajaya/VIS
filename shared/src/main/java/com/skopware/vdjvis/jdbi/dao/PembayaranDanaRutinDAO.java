package com.skopware.vdjvis.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PembayaranDanaRutinDAO extends BaseCrudDAO<PembayaranDanaRutin> {
    @SqlQuery("select * from v_pembayaran_samanagara_sosial_tetap where uuid=?")
    @Override
    PembayaranDanaRutin get(String uuid);

    @SqlUpdate
    @Override
    void create(@BindFields PembayaranDanaRutin x);

    @SqlUpdate
    @Override
    void update(@BindFields PembayaranDanaRutin x);

    @SqlUpdate("update pembayaran_samanagara_sosial_tetap set active=0 where uuid=?")
    @Override
    void delete(String uuid);
}
