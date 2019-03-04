package com.skopware.vdjvis.api.requestparams;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RqPlacePhoto {
    @JsonProperty public String idMendiang;
    @JsonProperty public String destCellId;

    @JsonProperty public String mendiangOriginCellId;
    @JsonProperty public String existingIdMendiangInDestCell;
}
