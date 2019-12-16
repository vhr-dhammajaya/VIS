package com.skopware.vdjvis.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.entities.Leluhur;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface LeluhurDAO extends BaseCrudDAO<Leluhur> {
    @SqlQuery("select * from v_leluhur where uuid=?")
    @Override
    Leluhur get(String uuid);

    @SqlUpdate("insert into leluhur_smngr(uuid, nama," +
            "tempat_lahir, tgl_lahir," +
            "tempat_mati, tgl_mati," +
            "hubungan_dgn_umat, tgl_daftar," +
            "umat_id) " +
            "values(:uuid, :nama," +
            ":tempatLahir, :tglLahir, :tempatMati, :tglMati," +
            ":hubunganDgnUmat, :tglDaftar, :penanggungJawab.uuid)")
    @Override
    void create(@BindFields Leluhur x);

    @SqlUpdate("update leluhur_smngr set nama=:nama," +
            "tempat_lahir=:tempatLahir, tgl_lahir=:tglLahir," +
            "tempat_mati=:tempatMati, tgl_mati=:tglMati," +
            "hubungan_dgn_umat=:hubunganDgnUmat" +
            " where uuid=:uuid")
    @Override
    void update(@BindFields Leluhur x);

    @SqlUpdate("update leluhur_smngr set active = 0 where uuid = ?")
    @Override
    void delete(String uuid);
}
