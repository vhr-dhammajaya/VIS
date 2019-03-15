package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

public class User extends BaseRecord<User> {
    //#region table columns
    @JsonProperty public String username;
    @JsonProperty public String password;
    @JsonProperty public String nama;
    @JsonProperty public Type tipe;
    //#endregion

    public enum Type {
        OPERATOR("Operator"), PENGURUS("Pengurus");

        private String label;

        Type(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
