package com.rockbitegames.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rockbitegames.domain.MaterialEntity;
import com.rockbitegames.domain.MaterialType;
import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.data.DataStorage;
import com.rockbitegames.domain.WarehouseEntity;
import com.rockbitegames.exception.NewPlayerCreationException;
import com.rockbitegames.util.Log;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlayerService {

    private final DataStorage dataStorage;
    private final ObjectMapper objectMapper;

    public Optional<PlayerEntity> getPlayerByUuid(@NonNull String playerUuid) {
        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        return Optional.ofNullable(players.get(playerUuid));
    }

    public boolean createPlayer(@NonNull PlayerEntity playerEntity) {
        try {
            ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
            if (!players.containsKey(playerEntity.getPlayerUuid())) {
                players.put(playerEntity.getPlayerUuid(), playerEntity);
                return true;
            } else {
                Log.warn(log, "Player with uuid {} exists", playerEntity.getPlayerUuid());
            }
        } catch (NewPlayerCreationException e) {
            Log.error(log, "Could not create new Player with uuid {}", playerEntity.getPlayerUuid());
        }
        return false;
    }

    public String getAll() {
        // for testing purposes
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(dataStorage.getPlayers());
        Log.info(log, json);
        return json;
    }
    public PlayerStatistics getPlayersStatistics() {
        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        PlayerStatistics playerStatistics = new PlayerStatistics();
        int size = players.size();
        playerStatistics.setPlayers(size);

        List<MaterialEntity> collectionStream = players.values().stream()
                .map(PlayerEntity::getWarehouseEntityList)
                .flatMap((v -> v.stream().map(WarehouseEntity::getMaterial)))
                .map(Map::values)
                .flatMap(Collection::stream)
                .toList();
        Map<MaterialType, Integer> materials = playerStatistics.getMaterials();
        collectionStream.forEach(v -> {
            if (materials.containsKey(v.getMaterialType())) {
                int value = materials.get(v.getMaterialType()) + v.getMaterialCurrentValue();
                materials.put(v.getMaterialType(), value);
            } else {
                materials.put(v.getMaterialType(), 0);
            }
        });
        playerStatistics.setMaterials(materials);
        return playerStatistics;
    }

    public void deleteAll() {
        dataStorage.removeAll();
    }

    @Getter
    @Setter
    public static class PlayerStatistics {
        private int players;
        private Map<MaterialType, Integer> materials = new HashMap<>();
    }

    // todo: other relevant methods could be added if needed

}
