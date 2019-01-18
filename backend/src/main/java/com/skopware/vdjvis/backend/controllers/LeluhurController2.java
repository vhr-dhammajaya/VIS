package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.db.DbHelper;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.Leluhur;
import com.skopware.vdjvis.backend.jdbi.rowmappers.LeluhurRowMapper;
import com.skopware.vdjvis.backend.jdbi.rowmappers.LeluhurRowMapper2;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/leluhur2")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LeluhurController2 {
    private Jdbi jdbi;

    public LeluhurController2(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GET
    public PageData<Leluhur> getList(@NotNull @Valid GridConfig gridConfig) {
        return jdbi.withHandle(h -> {
            return DbHelper.fetchPageData(h, "v_leluhur", gridConfig, query -> {
                LeluhurRowMapper2 mapper2 = new LeluhurRowMapper2();
                mapper2.mapper1 = new LeluhurRowMapper();
                return query.map(mapper2);
            }, true);
        });
    }
}
