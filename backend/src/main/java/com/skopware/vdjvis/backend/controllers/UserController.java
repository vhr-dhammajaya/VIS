package com.skopware.vdjvis.backend.controllers;

import com.skopware.javautils.dropwizard.BaseCrudController;
import com.skopware.vdjvis.api.entities.User;
import com.skopware.vdjvis.backend.jdbi.dao.UserDAO;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController extends BaseCrudController<User, UserDAO> {
    public UserController(Jdbi jdbi) {
        super(jdbi, "user", User.class, UserDAO.class);
    }

    @POST
    @Path("/login")
    public User login(@NotNull User x) {
        return jdbi.withHandle(h -> {
            UserDAO dao = h.attach(daoClass);
            User u = dao.get(x.username, x.password);
            return u;
        });
    }

    @POST
    @Path("/set_password")
    public boolean changePassword(@NotNull User x) {
        jdbi.useHandle(h -> {
            UserDAO dao = h.attach(daoClass);
            dao.setPassword(x.password, x.uuid);
        });
        return true;
    }
}
