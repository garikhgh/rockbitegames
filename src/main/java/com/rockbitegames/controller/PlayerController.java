package com.rockbitegames.controller;

import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.dto.PlayerDto;
import com.rockbitegames.exception.OptionalExceptionHandler;
import com.rockbitegames.messaging.KafkaProducer;
import com.rockbitegames.service.PlayerService;
import com.rockbitegames.util.GetOptionalValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PlayerController {


    private final PlayerService playerService;
    private final KafkaProducer kafkaProducer;


    @GetMapping("/player/all")
    ResponseEntity<ConcurrentMap<String, PlayerEntity>> getAllPlayers() {
        ConcurrentMap<String, PlayerEntity> allPlayers = playerService.getAll();
        return ResponseEntity.ok(allPlayers);
    }

    @DeleteMapping("/player/all")
    ResponseEntity<String> deleteAllPlayers() {
        playerService.deleteAll();
        return ResponseEntity.ok("All Players are deleted.");
    }

    @PostMapping("/player/create")
    ResponseEntity<String> createPlayer(@RequestBody PlayerDto playerDto) {
        kafkaProducer.sendPlayer(playerDto);
        return ResponseEntity.ok("Player is created.");
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
