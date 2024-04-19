package com.rockbitegames.service;

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
            }
        } catch (NewPlayerCreationException e) {
            Log.error(log, "Could not create new Player with uuid {}", playerEntity.getPlayerUuid());
        }

        return false;
    }

    public ConcurrentMap<String, PlayerEntity> getAll() {
        return dataStorage.getPlayers();
    }

    public void deleteAll() {
        dataStorage.removeAll();
    }

    // other relevant methods could be added if needed

}
