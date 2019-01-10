package com.skopware.vdjvis.backend.jdbi;

import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.vdjvis.api.User;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface UserDAO extends BaseCrudDAO<User> {
    @SqlQuery("select id, username, nama, tipe from user where id=?")
    @Override
    User get(String id);

    @SqlUpdate("insert into user(id, username, password, nama, tipe) values(:uuid, :username, md5(:password), :nama, :tipe)")
    @Override
    void create(@BindBean User x);

    @SqlUpdate("update user set username=:username, nama=:nama, tipe=:tipe where id=:uuid")
    @Override
    void update(@BindBean User x);

    @SqlUpdate("update user set active = 0 where id = ?")
    @Override
    void delete(String id);

    @SqlQuery("select id, username, nama, tipe from user where username=? and password=md5(?)")
    User get(String username, String password);

    @SqlUpdate("update user set password=md5(?) where id=?")
    void setPassword(String password, String id);
}
