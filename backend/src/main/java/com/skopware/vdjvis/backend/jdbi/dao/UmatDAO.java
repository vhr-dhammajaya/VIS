package com.skopware.vdjvis.backend.jdbi.dao;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.Umat;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface UmatDAO extends BaseCrudDAO<Umat> {
    @SqlQuery("select * from umat where uuid=?")
    @Override
    Umat get(String uuid);

    @SqlUpdate("insert into umat(uuid, nama," +
            "alamat, kota, kode_pos," +
            "no_telpon, email," +
            "tempat_lahir, tgl_lahir," +
            "gol_darah, jenis_kelamin, status_nikah," +
            "pendidikan_terakhir, jurusan," +
            "pekerjaan, bidang_usaha," +
            "nama_kerabat, alamat_kerabat, kota_kerabat, kode_pos_kerabat, no_telp_kerabat," +
            "nama_upasaka, penahbis, tgl_penahbisan) " +
            "values(:uuid, :nama," +
            ":alamat, :kota, :kodePos," +
            ":noTelpon, :email," +
            ":tempatLahir, :tglLahir," +
            ":golDarah, :jenisKelamin, :statusNikah," +
            ":pendidikanTerakhir, :jurusan," +
            ":pekerjaan, :bidangUsaha," +
            ":namaKerabat, :alamatKerabat, :kotaKerabat, :kodePosKerabat, :noTelpKerabat," +
            ":namaUpasaka, :penahbis, :tglPenahbisan" +
            ")")
    @Override
    void create(@BindBean Umat x);

    @SqlUpdate("update umat set nama=:nama, alamat=:alamat, kota=:kota, kode_pos=:kodePos, no_telpon=:noTelpon," +
            "email=:email, tempat_lahir=:tempatLahir, tgl_lahir=:tglLahir, gol_darah=:golDarah," +
            "jenis_kelamin=:jenisKelamin, status_nikah=:statusNikah, pendidikan_terakhir=:pendidikanTerakhir," +
            "jurusan=:jurusan, pekerjaan=:pekerjaan, bidang_usaha=:bidangUsaha, nama_kerabat=:namaKerabat," +
            "alamat_kerabat=:alamatKerabat, kota_kerabat=:kotaKerabat, kode_pos_kerabat=:kodePosKerabat," +
            "no_telp_kerabat=:noTelpKerabat, nama_upasaka=:namaUpasaka, penahbis=:penahbis," +
            "tgl_penahbisan=:tglPenahbisan" +
            " where uuid=:uuid")
    @Override
    void update(@BindBean Umat x);

    @SqlUpdate("update umat set active = 0 where uuid = ?")
    @Override
    void delete(String uuid);
}
