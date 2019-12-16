package com.skopware.vdjvis.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface DetilPembayaranDanaRutinDAO extends BaseCrudDAO<DetilPembayaranDanaRutin> {
    @SqlQuery
    @Override
    DetilPembayaranDanaRutin get(String uuid);

    @SqlUpdate
    @Override
    void create(@BindFields DetilPembayaranDanaRutin x);

    @SqlUpdate
    @Override
    void update(@BindFields DetilPembayaranDanaRutin x);

    @SqlUpdate
    @Override
    void delete(String uuid);
}
