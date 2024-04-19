package com.rockbitegames.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockbitegames.domain.MaterialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaterialDto implements Serializable {

    @JsonProperty("materialState")
    private MaterialState materialState;

    @JsonProperty("warehouseUuidToHostMaterial")
    private String warehouseUuidToHostMaterial;

    @JsonProperty("playerUuid")
    private String playerUuid;

    @JsonProperty("materialUuid")
    private String materialUuid;

    @JsonProperty("warehouseUuid")
    private String warehouseUuid;

    @JsonProperty("materialType")
    private MaterialType materialType;

    @JsonProperty("materialMaxCapacity")
    private Integer materialMaxCapacity;

    @JsonProperty("materialCurrentValue")
    private Integer materialCurrentValue;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("icon")
    private String icon;

}
