package com.rockbitegames.messaging;


import com.rockbitegames.dto.MaterialDto;
import com.rockbitegames.dto.PlayerDto;
import com.rockbitegames.exception.KafkaMessageNotSentException;
import com.rockbitegames.util.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private static final String NOTIFICATION_TOPIC_PLAYER = "player";
    private static final String NOTIFICATION_TOPIC_MATERIAL = "material";

    private final KafkaTemplate<String, Serializable> kafkaTemplate;

    public Serializable sendPlayer(PlayerDto message) throws KafkaMessageNotSentException {
        try {
            CompletableFuture<SendResult<String, Serializable>> send = kafkaTemplate.send(NOTIFICATION_TOPIC_PLAYER, message);
            SendResult<String, Serializable> stringSerializableSendResult = send.get();
            ProducerRecord<String, Serializable> producerRecord = stringSerializableSendResult.getProducerRecord();
            Serializable value = producerRecord.value();
            Log.info(log, "New Player {} is sent via Kafka.", value.toString());
            return value;
        } catch (InterruptedException | ExecutionException e) {
            String s = String.format("Could not send data to Kafka Producer for Player %s", message.getPlayerUuid());
            Log.error(log, s);
            throw new KafkaMessageNotSentException(s);
        }
    }

    public Serializable sendMaterial(MaterialDto message) throws KafkaMessageNotSentException {
        try {
            CompletableFuture<SendResult<String, Serializable>> send = kafkaTemplate.send(NOTIFICATION_TOPIC_MATERIAL, message);
            SendResult<String, Serializable> stringSerializableSendResult = send.get();
            ProducerRecord<String, Serializable> producerRecord = stringSerializableSendResult.getProducerRecord();
            Serializable value = producerRecord.value();
            Log.info(log, "Player {}, Warehouse {}, Material {} is sent.", message.getPlayerUuid(), message.getWarehouseUuid(), message.getMaterialUuid());
            return value;
        } catch (InterruptedException | ExecutionException e) {
            String s = String.format("Could not send data to Kafka Producer for Material %s", message.getMaterialUuid());
            Log.error(log, s);
            throw new KafkaMessageNotSentException(s);
        }



    }
}
