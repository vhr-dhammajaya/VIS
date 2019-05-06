package com.skopware.vdjvis.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class Pendapatan extends BaseRecord<Pendapatan> {
    // table columns
    @JsonProperty public LocalDate tglTransaksi;
    @JsonProperty public int noSeq;
    @JsonProperty public int nominal;
    @JsonProperty public String channel;
    @JsonProperty public JenisDana jenisDana;
    @JsonProperty public String keterangan;
    @JsonProperty public boolean correctionStatus;
    @JsonProperty public String correctionRequestReason;

    // relationships
    @JsonProperty public Umat umat;
    @JsonProperty public Acara acara;

    @JsonIgnore
    public String getIdTransaksi() {
        String sb = String.format("M/Lain-2/%d/%02d/%06d", tglTransaksi.getYear(), tglTransaksi.getMonthValue(), noSeq);
        return sb;
    }

    @JsonIgnore
    public String getKeperluanDana() {
        StringBuilder sb = new StringBuilder();
        sb.append("- " + jenisDana.label + "\n");

        if (acara != null) {
            sb.append("- Untuk acara: " + acara.nama);
        }

        return sb.toString();
    }

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
