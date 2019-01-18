package com.skopware.vdjvis.backend.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.Acara;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface AcaraDAO extends BaseCrudDAO<Acara> {
    @SqlQuery("select * from acara where id=?")
    @Override
    Acara get(String id);

    @SqlUpdate("insert into acara(id, nama, no_urut) values(:uuid, :nama, 1)")
    @Override
    void create(@BindBean Acara x);

    @SqlUpdate("update acara set nama=:nama where id=:uuid")
    @Override
    void update(@BindBean Acara x);

    @SqlUpdate("update acara set active=0 where id=?")
    @Override
    void delete(String id);
}
