package com.rockbitegames.data;

import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.util.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class DataStorage {

    private final ConcurrentMap<String, PlayerEntity> players = new ConcurrentHashMap<>();

    public synchronized ConcurrentMap<String, PlayerEntity> getPlayers() {
        return this.players;
    }

    public synchronized void removeAll() {
        this.players.clear();
        Log.warn(log, "Data Storage is deleted.....");
    }

}
