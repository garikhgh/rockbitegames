package com.rockbitegames.observer;

public interface MaterialObserver {
    void sendNotification(String eventType, String playerUuid, String warehouseUuid, String materialName);
}
