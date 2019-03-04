package com.skopware.vdjvis.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class DtoPembayaranSamanagara {
    @JsonProperty public String umatId;
    @JsonProperty public List<DtoStatusBayarLeluhur> listLeluhur;
    @JsonProperty public LocalDate tglTrans;
    @JsonProperty public String channel;
    @JsonProperty public String keterangan;
}
