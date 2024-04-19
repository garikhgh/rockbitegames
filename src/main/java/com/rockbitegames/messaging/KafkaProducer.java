package com.rockbitegames.messaging;


import com.rockbitegames.dto.MaterialDto;
import com.rockbitegames.dto.PlayerDto;
import com.rockbitegames.util.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private static final String NOTIFICATION_TOPIC_PLAYER = "player";
    private static final String NOTIFICATION_TOPIC_MATERIAL = "material";

    private final KafkaTemplate<String, Serializable> kafkaTemplate;

    public void sendPlayer(PlayerDto message) {
        kafkaTemplate.send(NOTIFICATION_TOPIC_PLAYER, message);
        Log.info(log, "New Player {} is sent.", message.getPlayerUuid());
    }

    public void sendMaterial(MaterialDto message) {
        kafkaTemplate.send(NOTIFICATION_TOPIC_MATERIAL, message);
        Log.info(log, "Player {}, Warehouse {}, Material {} is sent.", message.getPlayerUuid(), message.getWarehouseUuid(), message.getMaterialUuid());
    }
}
