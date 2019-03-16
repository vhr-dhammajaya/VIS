package com.skopware.vdjvis.backend;

import com.skopware.javautils.dropwizard.BaseApp;
import com.skopware.vdjvis.api.entities.*;
import com.skopware.vdjvis.backend.config.Config;
import com.skopware.vdjvis.backend.controllers.*;
import com.skopware.vdjvis.backend.jdbi.rowmappers.*;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;

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
        jdbi.registerRowMapper(Siswa.class, new SiswaRowMapper());
        jdbi.registerRowMapper(User.class, new UserRowMapper());

        jdbi.registerRowMapper(Leluhur.class, new LeluhurRowMapper());
        jdbi.registerRowMapper(TarifSamanagara.class, new TarifSamanagaraRowMapper());

        jdbi.registerRowMapper(PendaftaranDanaRutin.class, new PendaftaranDanaRutinRowMapper());
        jdbi.registerRowMapper(DetilPembayaranDanaRutin.class, new DetilPembayaranDanaRutinRowMapper());
        jdbi.registerRowMapper(Pendapatan.class, new PendapatanRowMapper());
        jdbi.registerRowMapper(Pengeluaran.class, new PengeluaranRowMapper());
    }

    @Override
    protected void registerControllers(Config config, Environment env) {
        JerseyEnvironment jersey = env.jersey();

        jersey.register(new AcaraController(jdbi));
        jersey.register(new UmatController(jdbi));
        jersey.register(new SiswaController(jdbi));
        jersey.register(new UserController(jdbi));

        jersey.register(new LeluhurController(jdbi));
        jersey.register(new TarifSamanagaraController(jdbi));
        jersey.register(new LokasiFotoController(jdbi));

        jersey.register(new PendaftaranDanaRutinController(jdbi));
        jersey.register(new DetilPembayaranDanaRutinController(jdbi));
        jersey.register(new PendapatanController(jdbi));
        jersey.register(new PengeluaranController(jdbi));

        jersey.register(new AbsensiUmatController(jdbi));
        jersey.register(new AbsensiSiswaController(jdbi));

        jersey.register(new LaporanController(jdbi));
    }
}
