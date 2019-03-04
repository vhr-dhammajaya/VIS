package com.skopware.vdjvis.api.requestparams;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skopware.javautils.db.BaseRecord;

import java.time.LocalDate;

public class RqBayarDanaSosialDanTetap {
    @JsonProperty public LocalDate tglTrans;
    @JsonProperty public int countBulan;
    @JsonProperty public String channel;
    @JsonProperty public String keterangan;

    @JsonProperty public String idPendaftaran;
}
