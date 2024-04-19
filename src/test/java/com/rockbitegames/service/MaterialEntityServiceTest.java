package com.rockbitegames.service;

import com.rockbitegames.domain.MaterialEntity;
import com.rockbitegames.domain.MaterialType;
import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.domain.WarehouseEntity;
import com.rockbitegames.exception.OptionalExceptionHandler;
import com.rockbitegames.mock.MockData;
import com.rockbitegames.util.GetOptionalValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class MaterialEntityServiceTest {

    private final String playerUuid1 = "79fd74b6-772f-41d6-941a-5d74ad06a853";
    private final String playerUuid2 = "79fd74b6-772f-41d6-941a-5d74ad06a854";
    private final String playerUuid3 = "79fd74b6-772f-41d6-941a-5d74ad06a855";
    private final String playerUuid4 = "79fd74b6-772f-41d6-941a-5d74ad06a856";
    private final String materialUuid = "fc38ca94-ac2a-4c8b-a853-018668ac0455";
    private final String materialUuid1 = "fc38ca94-ac2a-4c8b-a853-018668ac0456";

    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private MaterialService materialService;

    @Test
    void createMaterial() {
        PlayerEntity player1 = MockData.mockPlayer(playerUuid1);
        boolean p1 = playerService.createPlayer(player1);
        assertTrue(p1);
    }

    @Test
    void addMaterial() {

        PlayerEntity player2 = MockData.mockPlayer(playerUuid2);

        MaterialEntity materialEntity1 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 30);
        MaterialEntity materialEntity2 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 50);
        MaterialEntity materialEntity3 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 30);
        MaterialEntity materialEntity4 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 100);

        boolean p1 = playerService.createPlayer(player2);
        assertTrue(p1);

        boolean warehouse = warehouseService.createWarehouse(player2.getPlayerUuid(), 2);
        assertTrue(warehouse);

        boolean m1 = materialService.addMaterialToWarehouse(player2.getPlayerUuid(), materialEntity1);
        boolean m2 = materialService.addMaterialToWarehouse(player2.getPlayerUuid(), materialEntity2);
        boolean m3 = materialService.addMaterialToWarehouse(player2.getPlayerUuid(), materialEntity3);
        boolean m4 = materialService.addMaterialToWarehouse(player2.getPlayerUuid(), materialEntity4);

        assertTrue(m1);
        assertTrue(m2);
        assertTrue(m3);
        assertFalse(m4);

        materialEntity1 = MockData.mockMaterial(materialUuid, MaterialType.BOLT, MaterialType.BOLT.getMaxCapacity(), 80);
        materialEntity2 = MockData.mockMaterial(materialUuid, MaterialType.BOLT, MaterialType.BOLT.getMaxCapacity(), 50);
        materialEntity3 = MockData.mockMaterial(materialUuid, MaterialType.BOLT, MaterialType.BOLT.getMaxCapacity(), 30);
        materialEntity4 = MockData.mockMaterial(materialUuid, MaterialType.BOLT, MaterialType.BOLT.getMaxCapacity(), 100);

        m1 = materialService.addMaterialToWarehouse(player2.getPlayerUuid(), materialEntity1);
        m2 = materialService.addMaterialToWarehouse(player2.getPlayerUuid(), materialEntity2);
        m3 = materialService.addMaterialToWarehouse(player2.getPlayerUuid(), materialEntity3);
        m4 = materialService.addMaterialToWarehouse(player2.getPlayerUuid(), materialEntity4);

        assertTrue(m1);
        assertTrue(m2);
        assertTrue(m3);
        assertFalse(m4);

    }

    @Test
    void removeMaterial() {
        MaterialEntity materialEntity = new MaterialEntity();
        PlayerEntity player3 = MockData.mockPlayer(playerUuid3);
        boolean p1 = playerService.createPlayer(player3);

        boolean warehouse = warehouseService.createWarehouse(player3.getPlayerUuid(), 2);
        assertTrue(warehouse);

        MaterialEntity materialEntity1 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 100);
        MaterialEntity materialEntity2 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 100);

        boolean m1 = materialService.addMaterialToWarehouse(player3.getPlayerUuid(), materialEntity1);
        boolean m2 = materialService.addMaterialToWarehouse(player3.getPlayerUuid(), materialEntity2);

        Optional<PlayerEntity> playerByUuid = playerService.getPlayerByUuid(playerUuid1);
        if (playerByUuid.isPresent()){
            PlayerEntity playerEntity = playerByUuid.get();
            CopyOnWriteArrayList<WarehouseEntity> warehouseEntityList = playerEntity.getWarehouseEntityList();
            Optional<WarehouseEntity> first = warehouseEntityList.stream().findFirst();
            WarehouseEntity warehouseEntity = first.get();
            ConcurrentMap<MaterialType, MaterialEntity> m = warehouseEntity.getMaterial();
            materialEntity = m.get(MaterialType.IRON);
        }
        boolean s = materialService.removeMaterial(playerUuid1, materialEntity);
    }

    @Test
    void moveMaterial() throws OptionalExceptionHandler {

        PlayerEntity player4 = MockData.mockPlayer(playerUuid4);
        boolean p4 = playerService.createPlayer(player4);
        assertTrue(p4);
        boolean warehouse = warehouseService.createWarehouse(playerUuid4, 2);
        assertTrue(warehouse);

        MaterialEntity materialEntity1 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 100);

        boolean b1 = materialService.addMaterialToWarehouse(playerUuid4, materialEntity1);
        assertTrue(b1);


        Optional<String> whUuidOptional = warehouseService.findWarehouseUuidForGivenPlayerWithGivenMaterialUuid(playerUuid4, materialUuid);
        String whUuid = GetOptionalValue.getOptional(whUuidOptional);
        materialEntity1.setWarehouseUuid(whUuid);

        Optional<String> whWithoutGivenMaterialOptional = warehouseService.findWarehouseUuidForGivenPlayerWithEmptyMaterial(playerUuid4);
        String whDoseNotHaveMaterialUuid = GetOptionalValue.getOptional(whWithoutGivenMaterialOptional);
        boolean b2 = materialService.moveMaterial(playerUuid4,  whDoseNotHaveMaterialUuid, materialEntity1);
        assertTrue(b2);
    }

    // todo: other unit tests for test coverage are omitted:

}