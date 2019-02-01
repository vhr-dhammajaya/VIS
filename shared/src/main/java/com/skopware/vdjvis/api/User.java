package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

public class User extends BaseRecord<User> {
    @JsonProperty
    public String username;

    @JsonProperty
    public String password;

    @JsonProperty
    public String nama;

    @JsonProperty
    public Type tipe;

    @JsonProperty
    public boolean active;

    public enum Type {
        OPERATOR("Operator"), PENGURUS("Pengurus"), UMAT("Umat");

        private String label;

        Type(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public String toUiString() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Type getTipe() {
        return tipe;
    }

    public void setTipe(Type tipe) {
        this.tipe = tipe;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
