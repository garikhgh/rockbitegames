package com.rockbitegames.controller;

import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.dto.PlayerDto;
import com.rockbitegames.exception.KafkaMessageNotSentException;
import com.rockbitegames.exception.OptionalExceptionHandler;
import com.rockbitegames.messaging.KafkaProducer;
import com.rockbitegames.service.PlayerService;
import com.rockbitegames.util.GetOptionalValue;
import com.rockbitegames.validator.PayerValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PlayerController {


    private final PlayerService playerService;
    private final KafkaProducer kafkaProducer;

    @GetMapping("/player/statistics")
    ResponseEntity<PlayerService.PlayerStatistics> getPlayerStatistics() {
        PlayerService.PlayerStatistics playersStatistics = playerService.getPlayersStatistics();
        return ResponseEntity.ok(playersStatistics);
    }

    @GetMapping("/player/all")
    ResponseEntity<String> getAllPlayers() {
        String allPlayers = playerService.getAll();
        return ResponseEntity.ok(allPlayers);
    }

    @DeleteMapping("/player/all")
    ResponseEntity<String> deleteAllPlayers() {
        playerService.deleteAll();
        return ResponseEntity.ok("All Players are deleted.");
    }

    @PostMapping("/player/create")
    ResponseEntity<?> createPlayer(@RequestBody PlayerDto playerDto) throws KafkaMessageNotSentException {
        try {
            PayerValidate.PlayerValidator validate = PayerValidate.validate(playerDto);
            if (validate.getErrorCounter()>0) {
                return ResponseEntity.ok(validate);
            }
            Serializable serializable = kafkaProducer.sendPlayer(playerDto);
            return ResponseEntity.ok(serializable);
        } catch (KafkaMessageNotSentException e) {
            throw  new KafkaMessageNotSentException("Kafka Producer exception");
        }
    }

    @GetMapping("/player/{playerUuid}")
    ResponseEntity<?> getPlayerById(@PathVariable String playerUuid) {
        Optional<PlayerEntity> playerByUuid = playerService.getPlayerByUuid(playerUuid);
        try {
            PlayerEntity player = GetOptionalValue.getOptional(playerByUuid);
            return ResponseEntity.ok(player);
        } catch (OptionalExceptionHandler e) {
            return ResponseEntity.ok("Player does not exist.");
        }
    }

}
