package com.skopware.vdjvis.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.entities.Siswa;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface SiswaDAO extends BaseCrudDAO<Siswa> {
    @SqlQuery("select * from siswa where uuid=?")
    @Override
    Siswa get(String uuid);

    @SqlUpdate("insert into siswa(uuid, nama, nama_ayah, nama_ibu, alamat, kota, no_telpon, tempat_lahir, tgl_lahir," +
            "sekolah, alamat_sekolah, id_barcode, tgl_daftar) values(:uuid, :nama, :namaAyah, :namaIbu, :alamat, :kota, :noTelpon, :tempatLahir," +
            ":tglLahir, :sekolah, :alamatSekolah, :idBarcode, :tglDaftar)")
    @Override
    void create(@BindFields Siswa x);

    @SqlUpdate("update siswa set nama=:nama, nama_ayah=:namaAyah, nama_ibu=:namaIbu, alamat=:alamat, kota=:kota," +
            "no_telpon=:noTelpon, tempat_lahir=:tempatLahir, tgl_lahir=:tglLahir, sekolah=:sekolah," +
            "alamat_sekolah=:alamatSekolah where uuid=:uuid")
    @Override
    void update(@BindFields Siswa x);

    @SqlUpdate("update siswa set active=0 where uuid=?")
    @Override
    void delete(String uuid);
}
