package com.rockbitegames.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PlayerDto implements Serializable {

    @JsonProperty("playerUuid")
    private String playerUuid;

    @JsonProperty("playerName")
    private String playerName;

    @JsonProperty("playerEmail")
    private String playerEmail;

    @JsonProperty("playerLevel")
    private String playerLevel;

    @JsonProperty("numberOfWarehouses")
    private int numberOfWarehouses;
}
