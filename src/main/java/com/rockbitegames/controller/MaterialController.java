package com.rockbitegames.controller;


import com.rockbitegames.dto.MaterialDto;
import com.rockbitegames.messaging.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MaterialController {

    private final KafkaProducer kafkaProducer;

    @PostMapping("/material/create")
    ResponseEntity<?> addMaterial(@RequestBody MaterialDto materialDto) {
        kafkaProducer.sendMaterial(materialDto);
        return ResponseEntity.ok("Player.");
    }

    @PostMapping("/material/move")
    ResponseEntity<?> moveMaterial(@RequestBody MaterialDto materialDto) {
        kafkaProducer.sendMaterial(materialDto);
        return ResponseEntity.ok("Player is created.");
    }

    @PostMapping("/material/remove")
    ResponseEntity<?> removeMaterial(@RequestBody MaterialDto materialDto) {
        kafkaProducer.sendMaterial(materialDto);
        return ResponseEntity.ok("Player is created.");
    }
}
