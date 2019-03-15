package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class Pendapatan extends BaseRecord<Pendapatan> {
    public static int CORRECTION_STATUS_NORMAL = 0;
    public static int CORRECTION_STATUS_NEEDS_CORRECTION = 1;

    // table columns
    @JsonProperty public LocalDate tglTransaksi;
    @JsonProperty public int nominal;
    @JsonProperty public String channel;
    @JsonProperty public JenisDana jenisDana;
    @JsonProperty public String keterangan;
    @JsonProperty public int correctionStatus;
    @JsonProperty public String correctionRequestReason;

    // relationships
    @JsonProperty public Umat umat;
    @JsonProperty public Acara acara;

    public enum JenisDana {
        //IURAN_SAMANAGARA("Iuran samanagara"), DANA_SOSIAL("Dana sosial"), DANA_TETAP("Dana tetap"),
        DANA_SUKARELA("Dana sukarela"), DANA_PARAMITTA("Dana paramitta"), HAPPY_SUNDAY("Happy Sunday"), KOTAK_DANA("Kotak dana"), LAIN_2("Lain-2");

        public String label;

        JenisDana(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
