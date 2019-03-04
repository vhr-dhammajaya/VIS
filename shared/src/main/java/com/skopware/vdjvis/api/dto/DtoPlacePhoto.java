package com.skopware.vdjvis.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoPlacePhoto {
    @JsonProperty public String idMendiang;
    @JsonProperty public String destCellId;

    @JsonProperty public String mendiangOriginCellId;
    @JsonProperty public String existingIdMendiangInDestCell;
}
