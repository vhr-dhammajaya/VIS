package com.skopware.vdjvis.backend.jdbi;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.Leluhur;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface LeluhurDAO extends BaseCrudDAO<Leluhur> {
    @SqlQuery("select * from leluhur_smngr where uuid=?")
    @Override
    Leluhur get(String uuid);

    @SqlUpdate("insert into leluhur_smngr(uuid, nama," +
            "tempat_lahir, tgl_lahir," +
            "tempat_mati, tgl_mati," +
            "hubungan_dgn_umat, tgl_daftar," +
            "umat_id) " +
            "values(:uuid, :nama," +
            ":tempatLahir, :tglLahir, :tempatMati, :tglMati," +
            ":hubunganDgnUmat, :tglDaftar, :umatId)")
    @Override
    void create(@BindBean Leluhur x);

    @SqlUpdate("update leluhur_smngr set nama=:nama," +
            "tempat_lahir=:tempatLahir, tgl_lahir=:tglLahir," +
            "tempat_mati=:tempatMati, tgl_mati=:tglMati," +
            "hubungan_dgn_umat=:hubunganDgnUmat" +
            " where uuid=:uuid")
    @Override
    void update(@BindBean Leluhur x);

    @SqlUpdate("update leluhur_smngr set active = 0 where uuid = ?")
    @Override
    void delete(String uuid);
}
