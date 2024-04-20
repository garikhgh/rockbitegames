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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MaterialEntityServiceTest {

    private final String playerUuid1 = "79fd74b6-772f-41d6-941a-5d74ad06a853";
    private final String playerUuid2 = "79fd74b6-772f-41d6-941a-5d74ad06a854";
    private final String playerUuid3 = "79fd74b6-772f-41d6-941a-5d74ad06a855";
    private final String playerUuid6 = "79fd74b6-772f-41d6-941a-5d74ad06a856";
    private final String playerUuid7 = "99fd74b6-772f-41d6-941a-5d74ad06a856";
    private final String playerUuid8 = "89fd74b6-772f-41d6-941a-5d74ad06a856";
    private final String playerUuid5 = "79fd74b6-772f-41d6-941a-5d74ad06a899";
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
    void removeMaterial() throws OptionalExceptionHandler {
        MaterialEntity materialEntity = new MaterialEntity();
        PlayerEntity player3 = MockData.mockPlayer(playerUuid3);
        boolean p3 = playerService.createPlayer(player3);
        assertTrue(p3);

        boolean warehouse = warehouseService.createWarehouse(player3.getPlayerUuid(), 2);
        assertTrue(warehouse);

        MaterialEntity materialEntity1 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 100);
        MaterialEntity materialEntity2 = MockData.mockMaterial(materialUuid1, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 100);

        boolean m1 = materialService.addMaterialToWarehouse(player3.getPlayerUuid(), materialEntity1);
        boolean m2 = materialService.addMaterialToWarehouse(player3.getPlayerUuid(), materialEntity2);

        Optional<String> wareHouseOptionalUuid = warehouseService.findWarehouseUuidForGivenPlayerWithGivenMaterialUuid(playerUuid3, materialEntity2.getMaterialUuid());

        String warehouseUuid = GetOptionalValue.getOptional(wareHouseOptionalUuid);

        materialEntity2.setWarehouseUuid(warehouseUuid);
        boolean s = materialService.removeMaterial(playerUuid3, materialEntity2);
        assertTrue(s);
    }


    @Test
    void moveMaterialIfThereAreWarehouseWithHalfMaterial() throws OptionalExceptionHandler {
        // in this unit test checking if we want to move 20 points of material then there should be 10 surplus
        PlayerEntity player5 = MockData.mockPlayer(playerUuid5);
        boolean p5 = playerService.createPlayer(player5);
        assertTrue(p5);
        boolean warehouse = warehouseService.createWarehouse(playerUuid5, 2);
        assertTrue(warehouse);

        MaterialEntity materialEntity1 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 30);
        MaterialEntity materialEntity2 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 20);

        boolean b1 = materialService.addMaterialToWarehouse(playerUuid5, materialEntity1);
        assertTrue(b1);

        Optional<MaterialEntity> materialToBeMovedOptional = materialService.findPlayerMaterialByMaterialUuid(playerUuid5, materialEntity1.getMaterialUuid());
        MaterialEntity materialToBeMoved = GetOptionalValue.getOptional(materialToBeMovedOptional);

        Optional<String> whHostUuidOptional = warehouseService.findWareHouseUuidForGivenPlayerToHostMaterial(playerUuid5, materialEntity1);
        String whUuidHost = GetOptionalValue.getOptional(whHostUuidOptional);


        materialEntity2.setWarehouseUuid(materialToBeMoved.getWarehouseUuid());

        boolean b2 = materialService.moveMaterial(playerUuid5, whUuidHost, materialEntity2);
        assertTrue(b2);

        // in this unit test checking if we want to move 20 points of material then there should be 10 surplus
        Optional<WarehouseEntity> warehouseByIdOptional = warehouseService.findWarehouseById(playerUuid5, whUuidHost);
        assertTrue(warehouseByIdOptional.isPresent());
        WarehouseEntity wh = GetOptionalValue.getOptional(warehouseByIdOptional);
        assertNotNull(wh);
        ConcurrentMap<MaterialType, MaterialEntity> material = wh.getMaterial();
        assertNotNull(material);
        MaterialEntity materialEntity = material.get(materialToBeMoved.getMaterialType());
        assertNotNull(materialEntity);
        assertEquals(20, materialEntity.getMaterialCurrentValue());



        // in this unit test checking if we want to move 20 points of material then there should be 10 surplus
        warehouseByIdOptional = warehouseService.findWarehouseById(playerUuid5, materialToBeMoved.getWarehouseUuid());
        assertTrue(warehouseByIdOptional.isPresent());
        wh = GetOptionalValue.getOptional(warehouseByIdOptional);
        assertNotNull(wh);
         material = wh.getMaterial();
        assertNotNull(material);
        materialEntity = material.get(materialToBeMoved.getMaterialType());
        assertNotNull(materialEntity);
        assertEquals(10, materialEntity.getMaterialCurrentValue());

        // test if the warehouse material has 10 points

    }

    @Test
    void moveMaterialIfThereAreWarehouseWithMoreMaterial() throws OptionalExceptionHandler {

        // in this unit test checking if we want to move 50 points of material then there should be 0 surplus
        PlayerEntity player5 = MockData.mockPlayer(playerUuid6);
        boolean p5 = playerService.createPlayer(player5);
        assertTrue(p5);
        boolean warehouse = warehouseService.createWarehouse(playerUuid6, 2);
        assertTrue(warehouse);

        MaterialEntity materialEntity1 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 30);
        MaterialEntity materialToMove = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 50);

        boolean b1 = materialService.addMaterialToWarehouse(playerUuid6, materialEntity1);
        assertTrue(b1);

        Optional<MaterialEntity> materialToBeMovedOptional = materialService.findPlayerMaterialByMaterialUuid(playerUuid6, materialEntity1.getMaterialUuid());
        MaterialEntity materialToBeMoved = GetOptionalValue.getOptional(materialToBeMovedOptional);

        Optional<String> whHostUuidOptional = warehouseService.findWareHouseUuidForGivenPlayerToHostMaterial(playerUuid6, materialEntity1);
        String whUuidHost = GetOptionalValue.getOptional(whHostUuidOptional);

        materialToMove.setWarehouseUuid(materialToBeMoved.getWarehouseUuid());

        boolean b2 = materialService.moveMaterial(playerUuid6, whUuidHost, materialToMove);
        assertTrue(b2);

        // test the moved material current value. it should be 30 as we are trying to move 50 from warehouse1 but it has 30
        // then it is allowed to move that 30
        Optional<WarehouseEntity> warehouseByIdOptional = warehouseService.findWarehouseById(playerUuid6, whUuidHost);
        assertTrue(warehouseByIdOptional.isPresent());
        WarehouseEntity wh = GetOptionalValue.getOptional(warehouseByIdOptional);
        assertNotNull(wh);
        ConcurrentMap<MaterialType, MaterialEntity> material = wh.getMaterial();
        assertNotNull(material);
        MaterialEntity materialEntity = material.get(materialToMove.getMaterialType());
        assertNotNull(materialEntity);
        assertEquals(30, materialEntity.getMaterialCurrentValue());


        // test if the warehouse material is removed
        warehouseByIdOptional = warehouseService.findWarehouseById(playerUuid6, materialToBeMoved.getWarehouseUuid());
        assertTrue(warehouseByIdOptional.isPresent());
        wh = GetOptionalValue.getOptional(warehouseByIdOptional);
        assertNotNull(wh);
        material = wh.getMaterial();
        assertEquals(0, material.size());

    }
    @Test
    void moveMaterialIfThereAreWarehouseWithTwoMaterial() throws OptionalExceptionHandler {

        PlayerEntity player7 = MockData.mockPlayer(playerUuid7);
        boolean p7 = playerService.createPlayer(player7);
        assertTrue(p7);
        boolean warehouse = warehouseService.createWarehouse(playerUuid7, 2);
        assertTrue(warehouse);

        MaterialEntity materialEntity1 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 80);
        MaterialEntity materialEntity2 = MockData.mockMaterial(materialUuid1, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 70);
        MaterialEntity materialToMove = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 50);

        boolean b1 = materialService.addMaterialToWarehouse(playerUuid7, materialEntity1);
        assertTrue(b1);

        boolean b2 = materialService.addMaterialToWarehouse(playerUuid7, materialEntity2);
        assertTrue(b2);

        Optional<MaterialEntity> materialToBeMovedOptional = materialService.findPlayerMaterialByMaterialUuid(playerUuid7, materialEntity1.getMaterialUuid());
        MaterialEntity materialToBeMoved = GetOptionalValue.getOptional(materialToBeMovedOptional);

        Optional<String> whHostUuidOptional = warehouseService.findWareHouseUuidForGivenPlayerToHostMaterial(playerUuid7, materialEntity1);
        String whUuidHost = GetOptionalValue.getOptional(whHostUuidOptional);

        materialToMove.setWarehouseUuid(materialToBeMoved.getWarehouseUuid());

        boolean b3 = materialService.moveMaterial(playerUuid7, whUuidHost, materialToMove);
        assertTrue(b3);

        Optional<WarehouseEntity> warehouseByIdOptional = warehouseService.findWarehouseById(playerUuid7, whUuidHost);
        assertTrue(warehouseByIdOptional.isPresent());
        WarehouseEntity wh = GetOptionalValue.getOptional(warehouseByIdOptional);
        assertNotNull(wh);
        ConcurrentMap<MaterialType, MaterialEntity> material = wh.getMaterial();
        assertNotNull(material);
        MaterialEntity materialEntity = material.get(materialToMove.getMaterialType());
        assertNotNull(materialEntity);
        assertEquals(100, materialEntity.getMaterialCurrentValue());


        // test if the warehouse material is removed
        warehouseByIdOptional = warehouseService.findWarehouseById(playerUuid7, materialToBeMoved.getWarehouseUuid());
        assertTrue(warehouseByIdOptional.isPresent());
        wh = GetOptionalValue.getOptional(warehouseByIdOptional);
        assertNotNull(wh);
        material = wh.getMaterial();
        assertEquals(1, material.size());

        materialEntity = material.get(materialToMove.getMaterialType());
        assertNotNull(materialEntity);
        assertEquals(50, materialEntity.getMaterialCurrentValue());

    }

    @Test
    void moveMaterialWithRollBack() throws OptionalExceptionHandler {

        PlayerEntity player8 = MockData.mockPlayer(playerUuid8);
        boolean p8 = playerService.createPlayer(player8);
        assertTrue(p8);
        boolean warehouse = warehouseService.createWarehouse(playerUuid8, 2);
        assertTrue(warehouse);

        MaterialEntity materialEntity1 = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 80);
        MaterialEntity materialEntity2 = MockData.mockMaterial(materialUuid1, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 70);
        MaterialEntity materialToMove = MockData.mockMaterial(materialUuid, MaterialType.IRON, MaterialType.IRON.getMaxCapacity(), 70);

        boolean b1 = materialService.addMaterialToWarehouse(playerUuid8, materialEntity1);
        assertTrue(b1);

        boolean b2 = materialService.addMaterialToWarehouse(playerUuid8, materialEntity2);
        assertTrue(b2);

        Optional<MaterialEntity> materialToBeMovedOptional = materialService.findPlayerMaterialByMaterialUuid(playerUuid8, materialEntity1.getMaterialUuid());
        MaterialEntity materialToBeMoved = GetOptionalValue.getOptional(materialToBeMovedOptional);

        Optional<String> whHostUuidOptional = warehouseService.findWareHouseUuidForGivenPlayerToHostMaterial(playerUuid8, materialEntity1);
        String whUuidHost = GetOptionalValue.getOptional(whHostUuidOptional);

        materialToMove.setWarehouseUuid(materialToBeMoved.getWarehouseUuid());

        boolean b3 = materialService.moveMaterial(playerUuid8, whUuidHost, materialToMove);
        assertFalse(b3);


        Optional<WarehouseEntity> warehouseByIdOptional = warehouseService.findWarehouseById(playerUuid8, whUuidHost);
        assertTrue(warehouseByIdOptional.isPresent());
        WarehouseEntity wh = GetOptionalValue.getOptional(warehouseByIdOptional);
        assertNotNull(wh);
        ConcurrentMap<MaterialType, MaterialEntity> material = wh.getMaterial();
        assertNotNull(material);
        MaterialEntity materialEntity = material.get(materialToMove.getMaterialType());
        assertNotNull(materialEntity);
        assertEquals(50, materialEntity.getMaterialCurrentValue());


        // test if the warehouse material is removed
        warehouseByIdOptional = warehouseService.findWarehouseById(playerUuid8, materialToBeMoved.getWarehouseUuid());
        assertTrue(warehouseByIdOptional.isPresent());
        wh = GetOptionalValue.getOptional(warehouseByIdOptional);
        assertNotNull(wh);
        material = wh.getMaterial();
        assertEquals(1, material.size());

        materialEntity = material.get(materialToMove.getMaterialType());
        assertNotNull(materialEntity);
        assertEquals(100, materialEntity.getMaterialCurrentValue());

    }
    // todo: other unit tests for test coverage are omitted:

}