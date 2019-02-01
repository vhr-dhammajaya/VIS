package com.skopware.vdjvis.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlacePhotoRequestParam {
    @JsonProperty public String idMendiang;
    @JsonProperty public String destCellId;

    @JsonProperty public String originCellId;
    @JsonProperty public String destCellExistingIdMendiang;
}
