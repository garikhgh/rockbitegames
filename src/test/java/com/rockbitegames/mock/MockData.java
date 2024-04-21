package com.rockbitegames.mock;

import com.rockbitegames.domain.MaterialEntity;
import com.rockbitegames.domain.MaterialType;
import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.domain.WarehouseEntity;

public class MockData {

    private MockData() {throw new ExceptionInInitializerError("Object creation is not allowed.");}

    public static PlayerEntity mockPlayer(String playerUuid) {
        PlayerEntity player = new PlayerEntity();
        player.setPlayerUuid(playerUuid);
        player.setPlayerLevel("1");
        player.setPlayerEmail("test@email.com");
        player.setPlayerName("testName");
        return player;
    }
    public static WarehouseEntity mockWarehouse(String warehouseUuid) {
        WarehouseEntity wh = new WarehouseEntity();
                wh.setWarehouseUuid(warehouseUuid);
                return wh;
    }
    public static MaterialEntity mockMaterial(String uuid, MaterialType materialType, Integer capacity, Integer currentValue) {
        MaterialEntity m = new  MaterialEntity();
        m.setMaterialUuid(uuid);
        m.setMaterialType(materialType);
        m.setMaterialMaxCapacity(capacity);
        m.setMaterialCurrentValue(currentValue);
        m.setName(materialType.name());
        m.setDescription("To made shields.");
        m.setIcon("Iron " + materialType.name());
        return m;
    }
}
