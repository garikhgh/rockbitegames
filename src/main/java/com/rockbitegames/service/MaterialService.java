package com.rockbitegames.service;


import com.rockbitegames.domain.MaterialEntity;
import com.rockbitegames.domain.MaterialType;
import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.domain.WarehouseEntity;
import com.rockbitegames.data.DataStorage;
import com.rockbitegames.exception.OptionalExceptionHandler;
import com.rockbitegames.util.GetOptionalValue;
import com.rockbitegames.util.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class MaterialService {

    private final DataStorage dataStorage;
    private final WarehouseService warehouseService;

    public boolean addMaterialToWarehouse(@NonNull String playerUuid, @NonNull MaterialEntity materialEntity) {
        MaterialEntity remainingMaterialData = new MaterialEntity();
        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        boolean flag = false;
        if (players.containsKey(playerUuid)) {
            PlayerEntity aPlayer = players.get(playerUuid);
            CopyOnWriteArrayList<WarehouseEntity> warehouseEntityList = aPlayer.getWarehouseEntityList();
            if (warehouseEntityList == null) {
                Log.warn(log, "Player with uuid {} does not have assigned warehouses.", playerUuid);
                return false;
            } else {
                for (WarehouseEntity wh : warehouseEntityList) {
                    ConcurrentMap<MaterialType, MaterialEntity> material = wh.getMaterial();
                    if (material == null || !material.containsKey(materialEntity.getMaterialType())) {

                        MaterialEntity newMaterial = composeNewMaterial(materialEntity, wh.getWarehouseUuid());
                        newMaterial.setWarehouseUuid(wh.getWarehouseUuid());
                        remainingMaterialData = updateMaterialValue(playerUuid, wh.getWarehouseUuid(), newMaterial, materialEntity);
                        ConcurrentMap<MaterialType, MaterialEntity> newMaterialToAdd = new ConcurrentHashMap<>();
                        newMaterialToAdd.put(newMaterial.getMaterialType(), newMaterial);

                        wh.setMaterial(playerUuid, materialEntity, newMaterialToAdd);

                        flag = true;
                    } else {
                        MaterialEntity mE1 = material.get(materialEntity.getMaterialType());
                        remainingMaterialData = updateMaterialValue(playerUuid, wh.getWarehouseUuid(), mE1, materialEntity);
                        flag = true;
                    }
                    if (remainingMaterialData.getMaterialCurrentValue() == 0) {
                        return flag;
                    }
                }
            }
        } else {
            Log.warn(log, "Player with uuid {} does not exist.", playerUuid);
        }
        if (materialEntity.getMaterialCurrentValue() > 0) {
            Log.warn(log, "Player {} does not have enough warehouse to store materials. lost points {} .", playerUuid, materialEntity.getMaterialCurrentValue());
            flag = false;
        }
        return flag;
    }


    public boolean moveMaterial(@NonNull String playerUuid, @NonNull String warehouseUuid, @NonNull MaterialEntity materialEntity) {

        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        if (players.containsKey(playerUuid)) {
            PlayerEntity playerEntity = players.get(playerUuid);
            CopyOnWriteArrayList<WarehouseEntity> warehouseEntityList = playerEntity.getWarehouseEntityList();
            if (warehouseEntityList != null) {

                Optional<WarehouseEntity> wh1 = warehouseService.getWarehouseById(playerUuid, warehouseUuid);
                try {
                    WarehouseEntity host = GetOptionalValue.getOptional(wh1);

                    ConcurrentMap<MaterialType, MaterialEntity> material = host.getMaterial();
                    if (material != null) {

                        if (material.containsKey(materialEntity.getMaterialType())) {
                            MaterialEntity hostMaterial = material.get(materialEntity.getMaterialType());
                            MaterialEntity surplusOfMaterial = updateMaterialValue(playerUuid, host.getWarehouseUuid(), hostMaterial, materialEntity);
                            removeMaterial(playerUuid, materialEntity);
                            Log.info(log, "Player {}, Material is moved, remaining material {}", playerUuid, surplusOfMaterial.getMaterialCurrentValue());
                        } else {
                            ConcurrentMap<MaterialType, MaterialEntity> newMaterialToAdd =
                                    composeWarehouseForGivenPlayerAndMaterial(playerUuid, materialEntity, host);
                            host.setMaterial(playerUuid, materialEntity, newMaterialToAdd);
                            removeMaterial(playerUuid, materialEntity);
                        }

                    } else {
                        ConcurrentMap<MaterialType, MaterialEntity> m = new ConcurrentHashMap<>();
                        materialEntity.setWarehouseUuid(host.getWarehouseUuid());
                        m.put(materialEntity.getMaterialType(), materialEntity);
                        host.setMaterial(playerUuid, materialEntity, m);
                        removeMaterial(playerUuid, materialEntity);
                    }
                    return true;
                } catch (OptionalExceptionHandler e) {

                    Log.warn(log, "Exception is related to Player {} warehouse {}", playerUuid, warehouseUuid);
                    return false;
                }
            } else {
                Log.warn(log, "Warehouse does not exist.");
                return false;
            }
        } else {
            Log.warn(log, "Warehouse does not exist.");
            return false;
        }
    }


    public boolean removeMaterial(@NonNull String playerUuid, @NonNull MaterialEntity materialEntity) {

        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        if (players.containsKey(playerUuid)) {
            PlayerEntity playerEntity = players.get(playerUuid);
            CopyOnWriteArrayList<WarehouseEntity> warehouseEntityList = playerEntity.getWarehouseEntityList();
            Optional<WarehouseEntity> optionalWarehouse = warehouseEntityList.stream()
                    .filter(wh -> wh.getWarehouseUuid().equals(materialEntity.getWarehouseUuid()))
                    .findFirst();
            try {
                WarehouseEntity warehouse = GetOptionalValue.getOptional(optionalWarehouse);
                return removeMaterial(playerUuid, warehouse, materialEntity);
            } catch (OptionalExceptionHandler e) {
                Log.warn(log, "Player {}, warehouse {}, material with uuid {} is not found,", playerUuid,
                        materialEntity.getWarehouseUuid(),
                        materialEntity.getMaterialUuid());
                return false;
            }
        }
        Log.warn(log, "Player {} is not found", playerUuid);
        return false;
    }

    private boolean removeMaterial(@NonNull String playerUuid, @NonNull WarehouseEntity warehouse, @NonNull MaterialEntity material) {

        try {
            Optional<MaterialEntity> removedMaterial = warehouse.removeMaterialByMaterialByMaterialUuid(playerUuid, material);
            MaterialEntity optional = GetOptionalValue.getOptional(removedMaterial);
            Log.info(log, "Material {} is removed from warehouse {}", warehouse.getWarehouseUuid(), material.getMaterialUuid());
            return true;
        } catch (OptionalExceptionHandler e) {
            Log.warn(log, "Could not get optional value for warehouse {}, material {}", warehouse.getWarehouseUuid(), material.getMaterialUuid());
            return false;
        }

    }

    private MaterialEntity updateMaterialValue(@NonNull String playerUuid, @NonNull String warehouseUuid, @NonNull MaterialEntity aMaterial, @NonNull MaterialEntity materialEntity) {

        int neededMaterial = aMaterial.getMaterialMaxCapacity() - aMaterial.getMaterialCurrentValue();
        Log.info(log, "Player {}, warehouse {}, material {} could take {} points.", playerUuid, warehouseUuid, aMaterial.getMaterialType(), neededMaterial);
        Integer materialCurrentValue = aMaterial.getMaterialCurrentValue();
        Integer materialCurrentValueToBeAdded = materialEntity.getMaterialCurrentValue();
        if (neededMaterial > materialCurrentValueToBeAdded) {
            aMaterial.setMaterialCurrentValue(materialCurrentValue + materialCurrentValueToBeAdded, playerUuid);
            Log.info(log, "Player {}, warehouse {}, material {} capacity is {} points.", playerUuid, warehouseUuid, aMaterial.getMaterialType(), aMaterial.getMaterialCurrentValue());
            materialEntity.setMaterialCurrentValue(0);
        } else {
            aMaterial.setMaterialCurrentValue(aMaterial.getMaterialMaxCapacity(), playerUuid);
            int surplus = materialCurrentValueToBeAdded - neededMaterial;
            materialEntity.setMaterialCurrentValue(surplus);
        }
        return materialEntity;
    }

    private MaterialEntity composeNewMaterial(@NonNull MaterialEntity material, @NonNull String warehouseUuid) {

        MaterialEntity m = new MaterialEntity();
        m.setMaterialUuid(material.getMaterialUuid());
        m.setWarehouseUuid(warehouseUuid);
        m.setMaterialType(material.getMaterialType());
        m.setMaterialMaxCapacity(material.getMaterialMaxCapacity());
        m.setMaterialCurrentValue(0);
        m.setName(material.getName());
        m.setDescription(material.getDescription());
        m.setIcon(material.getIcon());
        return m;
    }

    private ConcurrentMap<MaterialType, MaterialEntity> composeWarehouseForGivenPlayerAndMaterial(@NonNull String playerUuid, @NonNull MaterialEntity materialEntity, @NonNull WarehouseEntity warehouse) {
        MaterialEntity newMaterial = composeNewMaterial(materialEntity, warehouse.getWarehouseUuid());
        MaterialEntity remainingMaterialData = updateMaterialValue(playerUuid, warehouse.getWarehouseUuid(), newMaterial, materialEntity);
        ConcurrentMap<MaterialType, MaterialEntity> newMaterialToAdd = new ConcurrentHashMap<>();
        newMaterialToAdd.put(newMaterial.getMaterialType(), newMaterial);
        return newMaterialToAdd;
    }


}
