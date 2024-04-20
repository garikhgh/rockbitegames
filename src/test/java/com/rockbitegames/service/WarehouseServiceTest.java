package com.rockbitegames.service;

import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.domain.WarehouseEntity;
import com.rockbitegames.mock.MockData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class WarehouseServiceTest {

    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private PlayerService playerService;

    @Test
    void createWarehouseWithPlayer() {

        String playerUuid = "69fd74b6-772f-41d6-941a-5d74ad06a853";
        PlayerEntity player = MockData.mockPlayer(playerUuid);
        String warehouseUuid = "fc38ca94-ac2a-4c8b-a853-018668ac04f4";
        WarehouseEntity warehouseEntity = MockData.mockWarehouse(warehouseUuid);

        boolean player1 = playerService.createPlayer(player);
        assertTrue(player1);
        boolean warehouse = warehouseService.createWarehouse(playerUuid, 1);
        assertTrue(warehouse);

        Optional<PlayerEntity> playerByUuid = playerService.getPlayerByUuid(playerUuid);
        assertTrue(playerByUuid.isPresent());
        assertEquals(1, playerByUuid.get().getWarehouseEntityList().size());
    }

    // todo: other unit tests for test coverage are omitted:


}