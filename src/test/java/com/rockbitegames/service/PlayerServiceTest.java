package com.rockbitegames.service;

import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.mock.MockData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("dev")
class PlayerServiceTest {

    private final String playerUuid1 = "19fd74b6-772f-41d6-941a-5d74ad06a853";
    private final String playerUuid2 = "29fd74b6-772f-41d6-941a-5d74ad06a854";
    private final String playerUuid3 = "39fd74b6-772f-41d6-941a-5d74ad06a855";

    @Autowired
    private PlayerService playerService;

    @Test
    void getPlayerByUuid() {
        PlayerEntity player1 = MockData.mockPlayer(playerUuid1);
        boolean p1 = playerService.createPlayer(player1);
        assertTrue(p1);
        Optional<PlayerEntity> playerByUuid = playerService.getPlayerByUuid(playerUuid1);
        assertTrue(playerByUuid.isPresent());
    }

    @Test
    void createPlayer() {
        PlayerEntity player1 = MockData.mockPlayer(playerUuid3);
        boolean p1 = playerService.createPlayer(player1);
        assertTrue(p1);
    }

    // todo: other unit tests for test coverage are omitted:
}