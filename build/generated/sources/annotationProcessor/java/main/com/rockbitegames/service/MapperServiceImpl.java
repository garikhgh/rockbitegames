package com.rockbitegames.service;

import com.rockbitegames.domain.MaterialEntity;
import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.dto.MaterialDto;
import com.rockbitegames.dto.PlayerDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-21T16:36:43+0400",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.7.jar, environment: Java 17.0.8 (GraalVM Community)"
)
@Component
public class MapperServiceImpl implements MapperService {

    @Override
    public MaterialEntity toMaterialEntity(MaterialDto materialDto) {
        if ( materialDto == null ) {
            return null;
        }

        MaterialEntity materialEntity = new MaterialEntity();

        materialEntity.setMaterialUuid( materialDto.getMaterialUuid() );
        materialEntity.setWarehouseUuid( materialDto.getWarehouseUuid() );
        materialEntity.setMaterialType( materialDto.getMaterialType() );
        materialEntity.setMaterialMaxCapacity( materialDto.getMaterialMaxCapacity() );
        materialEntity.setMaterialCurrentValue( materialDto.getMaterialCurrentValue() );
        materialEntity.setName( materialDto.getName() );
        materialEntity.setDescription( materialDto.getDescription() );
        materialEntity.setIcon( materialDto.getIcon() );

        return materialEntity;
    }

    @Override
    public PlayerEntity toPlayerEntity(PlayerDto playerDto) {
        if ( playerDto == null ) {
            return null;
        }

        PlayerEntity.PlayerEntityBuilder playerEntity = PlayerEntity.builder();

        playerEntity.playerUuid( playerDto.getPlayerUuid() );
        playerEntity.playerName( playerDto.getPlayerName() );
        playerEntity.playerEmail( playerDto.getPlayerEmail() );
        playerEntity.playerLevel( playerDto.getPlayerLevel() );

        return playerEntity.build();
    }
}
