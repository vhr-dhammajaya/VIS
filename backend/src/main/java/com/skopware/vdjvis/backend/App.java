package com.skopware.vdjvis.backend;

import com.skopware.javautils.dropwizard.BaseApp;
import com.skopware.vdjvis.api.Acara;
import com.skopware.vdjvis.api.Leluhur;
import com.skopware.vdjvis.api.Umat;
import com.skopware.vdjvis.api.User;
import com.skopware.vdjvis.backend.config.Config;
import com.skopware.vdjvis.backend.controllers.AcaraController;
import com.skopware.vdjvis.backend.controllers.LeluhurController;
import com.skopware.vdjvis.backend.controllers.UmatController;
import com.skopware.vdjvis.backend.controllers.UserController;
import com.skopware.vdjvis.backend.jdbi.rowmappers.AcaraRowMapper;
import com.skopware.vdjvis.backend.jdbi.rowmappers.LeluhurRowMapper;
import com.skopware.vdjvis.backend.jdbi.rowmappers.UmatRowMapper;
import com.skopware.vdjvis.backend.jdbi.rowmappers.UserRowMapper;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.sql.Types;

public class App extends BaseApp<Config> {
    public static void main(String[] args) {
        try {
            new App().run(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void registerRowMappers(Config config, Environment env) {
        jdbi.registerRowMapper(Acara.class, new AcaraRowMapper());
        jdbi.registerRowMapper(Umat.class, new UmatRowMapper());
        jdbi.registerRowMapper(User.class, new UserRowMapper());
        jdbi.registerRowMapper(Leluhur.class, new LeluhurRowMapper());
    }

    @Override
    protected void registerControllers(Config config, Environment env) {
        JerseyEnvironment jersey = env.jersey();
        jersey.register(new AcaraController(jdbi));
        jersey.register(new UmatController(jdbi));
        jersey.register(new UserController(jdbi));
        jersey.register(new LeluhurController(jdbi));
    }
}
