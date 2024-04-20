package com.rockbitegames.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.data.DataStorage;
import com.rockbitegames.exception.NewPlayerCreationException;
import com.rockbitegames.util.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

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

    public void deleteAll() {
        dataStorage.removeAll();
    }

    // todo: other relevant methods could be added if needed

}
