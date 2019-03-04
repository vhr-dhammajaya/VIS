package com.skopware.vdjvis.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class DtoBayarDanaSosialDanTetap {
    @JsonProperty public LocalDate tglTrans;
    @JsonProperty public int countBulan;
    @JsonProperty public String channel;
    @JsonProperty public String keterangan;

    @JsonProperty public String idPendaftaran;
}
