package com.rockbitegames.controller;


import com.rockbitegames.dto.MaterialDto;
import com.rockbitegames.exception.KafkaMessageNotSentException;
import com.rockbitegames.messaging.KafkaProducer;
import com.rockbitegames.validator.MaterialValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MaterialController {

    private final KafkaProducer kafkaProducer;

    @PostMapping("/material/create")
    ResponseEntity<?> addMaterial(@RequestBody MaterialDto materialDto) throws KafkaMessageNotSentException {
        try {
            MaterialValidate.MaterialValidator materialValidator = MaterialValidate.validateMaterial(materialDto);
            if (materialValidator.getErrorCounter() > 0) {
                return ResponseEntity.ok(materialValidator);
            }
            kafkaProducer.sendMaterial(materialDto);
            return ResponseEntity.ok("Material is added to Player.");
        } catch (KafkaMessageNotSentException e) {
            throw new KafkaMessageNotSentException("Kafka Producer Exception");
        }

    }

    @PostMapping("/material/move")
    ResponseEntity<?> moveMaterial(@RequestBody MaterialDto materialDto) throws KafkaMessageNotSentException {
        try {
            kafkaProducer.sendMaterial(materialDto);
            MaterialValidate.MaterialValidator materialValidator = MaterialValidate.validateMaterial(materialDto);
            if (materialValidator.getErrorCounter() > 0) {
                return ResponseEntity.ok(materialValidator);
            }
            return ResponseEntity.ok("Material is moved.");
        } catch (KafkaMessageNotSentException e) {
            throw new KafkaMessageNotSentException("Kafka Producer Exception");
        }

    }

    @PostMapping("/material/remove")
    ResponseEntity<?> removeMaterial(@RequestBody MaterialDto materialDto) throws KafkaMessageNotSentException {
        try {
            kafkaProducer.sendMaterial(materialDto);
            MaterialValidate.MaterialValidator materialValidator = MaterialValidate.validateMaterial(materialDto);
            if (materialValidator.getErrorCounter() > 0) {
                return ResponseEntity.ok(materialValidator);
            }
            return ResponseEntity.ok("Player is removed.");
        } catch (KafkaMessageNotSentException e) {
            throw new KafkaMessageNotSentException("Kafka Producer Exception");
        }

    }
}
