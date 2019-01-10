package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;
import com.skopware.javautils.db.DbRecord;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

public class Acara extends BaseRecord<Acara> {
    @JsonProperty
    @NotEmpty
    @Size(min = 1, max = 45) // error message is "<fieldName> size must be between <min> and <max>"
    public String nama;

    @JsonProperty
    public int noUrut;

    @JsonProperty
    public boolean active;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getNoUrut() {
        return noUrut;
    }

    public void setNoUrut(int noUrut) {
        this.noUrut = noUrut;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
