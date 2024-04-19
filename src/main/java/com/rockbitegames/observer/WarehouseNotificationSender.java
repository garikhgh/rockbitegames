package com.rockbitegames.observer;

import com.rockbitegames.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarehouseNotificationSender implements MaterialObserver {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseNotificationSender.class);

    @Override
    public void sendNotification(String eventType, String playerUuid, String warehouseUuid, String materialName) {
        // here could be sent an email or something else.
        Log.info(logger, "######################################################################################");
        Log.info(logger, "---------------------WAREHOUSE NOTIFICATION: SENDING....------------------------------");
        Log.info(logger, "Event, Player {} warehouse {}, material {}:  event={} ", playerUuid, warehouseUuid, materialName, eventType);
        Log.info(logger, "######################################################################################");
    }
}
