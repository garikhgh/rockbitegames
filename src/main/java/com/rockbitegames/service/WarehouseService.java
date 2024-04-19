package com.rockbitegames.service;

import com.rockbitegames.data.DataStorage;
import com.rockbitegames.domain.MaterialEntity;
import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.domain.WarehouseEntity;
import com.rockbitegames.exception.OptionalExceptionHandler;
import com.rockbitegames.util.GetOptionalValue;
import com.rockbitegames.util.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final DataStorage dataStorage;
    private final PlayerService playerService;

    public boolean createWarehouse(@NonNull String playerUuid, int numberOfWarehouses) {
        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        Optional<PlayerEntity> playerByUuid = playerService.getPlayerByUuid(playerUuid);
        CopyOnWriteArrayList<WarehouseEntity> warehouseList = createWarehouse(numberOfWarehouses);

        try {
            PlayerEntity playerEntity = GetOptionalValue.getOptional(playerByUuid);
            playerEntity.setWarehouse(warehouseList);
            return true;
        }
        catch (OptionalExceptionHandler e) {
            Log.warn(log, "Player with uuid {} does not exist", playerUuid);
            return false;
        }
    }

    private CopyOnWriteArrayList<WarehouseEntity> createWarehouse(int numberOfWarehouse) {
        CopyOnWriteArrayList<WarehouseEntity> warehouseEntityList = new CopyOnWriteArrayList<>();
        for (int i = 0; i < numberOfWarehouse; i++) {
            WarehouseEntity wh = new  WarehouseEntity();
                    wh.setWarehouseUuid(UUID.randomUUID()
                            .toString());
            warehouseEntityList.add(wh);
        }
        return warehouseEntityList;
    }

    public Optional<WarehouseEntity> getWarehouseById(@NonNull String playerUuid, @NonNull String warehouseId1) {
        Optional<WarehouseEntity> first = Optional.empty();
        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        if (players.containsKey(playerUuid)) {
            PlayerEntity playerEntity = players.get(playerUuid);
            first = playerEntity.getWarehouseEntityList().stream()
                    .filter(wh -> wh.getWarehouseUuid().equals(warehouseId1))
                    .findFirst();
        }
        return first;
    }
    public Optional<String> findWarehouseUuidForGivenPlayerWithEmptyMaterial(@NonNull String playerUuid) {
        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        if (players.containsKey(playerUuid)) {
            PlayerEntity playerEntity = players.get(playerUuid);
            return playerEntity.getWarehouseEntityList().stream()
                    .filter(w -> {
                        Optional<MaterialEntity> first = w.getMaterial().values().stream().filter(Objects::nonNull).findFirst();
                        return first.isEmpty();
                    })
                    .map(WarehouseEntity::getWarehouseUuid)
                    .findFirst();
        }
        return Optional.empty();
    }

    public Optional<String> findWarehouseUuidForGivenPlayerWithGivenMaterialUuid(@NonNull String playerUuid, @NonNull String materialUuid) {
        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        if (players.containsKey(playerUuid)) {
            PlayerEntity playerEntity = players.get(playerUuid);
            return playerEntity.getWarehouseEntityList().stream()
                    .map(w -> {
                        Optional<String> first = w.getMaterial().values().stream()
                                .filter(material -> material.getMaterialUuid().equals(materialUuid))
                                .map(MaterialEntity::getWarehouseUuid)
                                .findFirst();
                        return first.orElse("");
                    }).findFirst();
        }
        return Optional.empty();
    }
}
