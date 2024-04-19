package com.rockbitegames.observer;

import java.util.*;

public class ObserverManger {
    Map<String, List<MaterialObserver>> listeners = new HashMap<>();

    public ObserverManger(String... operations) {
        for (String operation : operations) {
            this.listeners.put(operation, new ArrayList<>());
        }
    }

    public void subscribe(String eventType, MaterialObserver eventListener) {
        List<MaterialObserver> players = listeners.get(eventType);
        players.add(eventListener);
    }

    public void notify(String eventType, String playerUuid, String warehouseUuid, String materialName) {
        List<MaterialObserver> player = listeners.get(eventType);
        for (MaterialObserver listener: player) {
            listener.sendNotification(eventType, playerUuid, warehouseUuid, materialName);
        }
    }
}
