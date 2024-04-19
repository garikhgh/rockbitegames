package com.rockbitegames.controller;


import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.exception.OptionalExceptionHandler;
import com.rockbitegames.service.WarehouseService;
import com.rockbitegames.util.GetOptionalValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/warehouse/{playerUuid}/empty-material")
    ResponseEntity<?> getPlayerById(@PathVariable String playerUuid) {
        Optional<String> emptyWarehouseOptional = warehouseService.findWarehouseUuidForGivenPlayerWithEmptyMaterial(playerUuid);
        try {
            String uuid = GetOptionalValue.getOptional(emptyWarehouseOptional);
            return ResponseEntity.ok(uuid);
        } catch (OptionalExceptionHandler e) {
            return ResponseEntity.ok("Player does not exist.");
        }
    }
}
