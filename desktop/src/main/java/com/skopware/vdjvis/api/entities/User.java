package com.skopware.vdjvis.api.entities;

import com.skopware.javautils.db.BaseRecord;

public class User extends BaseRecord<User> {
    //#region table columns
    public String username;
    public String password;
    public String nama;
    public Type tipe;
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
