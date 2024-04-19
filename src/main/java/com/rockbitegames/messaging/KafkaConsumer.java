package com.rockbitegames.messaging;


import com.rockbitegames.domain.MaterialEntity;
import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.dto.MaterialDto;
import com.rockbitegames.dto.MaterialState;
import com.rockbitegames.dto.PlayerDto;
import com.rockbitegames.exception.NewPlayerCreationException;
import com.rockbitegames.service.MapperService;
import com.rockbitegames.service.MaterialService;
import com.rockbitegames.service.PlayerService;
import com.rockbitegames.service.WarehouseService;
import com.rockbitegames.util.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class KafkaConsumer {

    private final PlayerService playerService;
    private final MaterialService materialService;
    private final MapperService mapperService;
    private final WarehouseService warehouseService;


    @KafkaListener(topics = "material", containerFactory = "kafkaListenerContainerFactoryForMaterial", groupId = "materialGroupId")
    public void materialListener(MaterialDto materialDto) {
        MaterialEntity materialEntity = mapperService.toMaterialEntity(materialDto);

        switch (materialDto.getMaterialState()) {
            case REMOVE: {
                materialEntity.invokeObserverInstance();
                boolean status = materialService.removeMaterial(materialDto.getPlayerUuid(), materialEntity);
                Log.info(log, "Player {} receiving new material. Is new Material {} added {}", materialDto.getPlayerUuid(), materialDto.getMaterialUuid(), status);
                break;
            }
            case ADD: {
                materialEntity.invokeObserverInstance();
                boolean status = materialService.addMaterialToWarehouse(materialDto.getPlayerUuid(), materialEntity);
                Log.info(log, "Player {} receiving new material. Is new Material {} added {}", materialDto.getPlayerUuid(), materialDto.getMaterialUuid(), status);
                break;
            }
            case MOVE: {
                materialEntity.invokeObserverInstance();
                boolean status = materialService.moveMaterial(materialDto.getPlayerUuid(), materialDto.getWarehouseUuidToHostMaterial(), materialEntity);
                Log.info(log, "Player {} receiving new material. Is new Material {} added {}", materialDto.getPlayerUuid(), materialDto.getMaterialUuid(), status);
                break;
            }
            default:
                Log.info(log, "Could not identify material State");
        }
    }

    @KafkaListener(topics = "player", containerFactory = "kafkaListenerContainerFactoryForPlayer", groupId = "playerGroupId")
    public void addPlayer(PlayerDto playerDto) {
        try {
            PlayerEntity playerEntity = mapperService.toPlayerEntity(playerDto);
            playerEntity.invokeObserverInstance();
            boolean playerCreationStatus = playerService.createPlayer(playerEntity);
            boolean warehouse = warehouseService.createWarehouse(playerDto.getPlayerUuid(), playerDto.getNumberOfWarehouses());
            log.info("Created new Player {}, the player has {} warehouses: player created {}, warehouse created {}.", playerDto.getPlayerUuid(), playerDto.getNumberOfWarehouses(), playerCreationStatus, warehouse);
        } catch (NewPlayerCreationException e) {
            log.warn("New Player creation exception happened {}", e.getMessage());
        }
    }
}