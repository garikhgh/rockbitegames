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
            if (warehouseEntityList.isEmpty()) {
                Log.warn(log, "Player with uuid {} does not have assigned warehouses.", playerUuid);
                return false;
            } else {
                for (WarehouseEntity wh : warehouseEntityList) {
                    ConcurrentMap<MaterialType, MaterialEntity> material = wh.getMaterial();
                    if (material.isEmpty() || !material.containsKey(materialEntity.getMaterialType())) {

                        MaterialEntity newMaterial = composeNewMaterial(materialEntity, wh.getWarehouseUuid());
                        newMaterial.setWarehouseUuid(wh.getWarehouseUuid());
                        remainingMaterialData = updateMaterialValue(playerUuid, wh.getWarehouseUuid(), newMaterial, materialEntity);
//                        ConcurrentMap<MaterialType, MaterialEntity> newMaterialToAdd = new ConcurrentHashMap<>();
                        wh.setMaterial(playerUuid, materialEntity, newMaterial);

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


    public boolean moveMaterial(@NonNull String playerUuid, @NonNull String hostWarehouse, @NonNull MaterialEntity materialEntity) {
        MaterialEntity backupMaterial = materialEntity.backupMaterial();
        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        if (players.containsKey(playerUuid)) {
            PlayerEntity playerEntity = players.get(playerUuid);

            Optional<WarehouseEntity> warehouseByIdOptional = warehouseService.findWarehouseById(playerUuid, materialEntity.getWarehouseUuid());
            try {
                WarehouseEntity warehouseToSubtractMaterial = GetOptionalValue.getOptional(warehouseByIdOptional);
                ConcurrentMap<MaterialType, MaterialEntity> material = warehouseToSubtractMaterial.getMaterial();
                MaterialEntity m = material.get(materialEntity.getMaterialType());
                // should be validation on the material value max capacity
                int newValue = m.getMaterialCurrentValue();
                int toBeMovedValue = materialEntity.getMaterialCurrentValue();
                if ((newValue - toBeMovedValue) <= 0) {
                    int newMaterialValue = toBeMovedValue - newValue;
                    materialEntity.setMaterialCurrentValue(toBeMovedValue-newMaterialValue);
                    m.setMaterialCurrentValue(0);
                    // the empty material should be removed from the warehouse
                    removeMaterial(playerUuid, warehouseToSubtractMaterial, m);
                } else {
                    newValue -= toBeMovedValue;
                    m.setMaterialCurrentValue(newValue, playerUuid);
                }

                Optional<WarehouseEntity> hostWarehouseOptional = warehouseService.findWarehouseById(playerUuid, hostWarehouse);

                WarehouseEntity host = GetOptionalValue.getOptional(hostWarehouseOptional);
                ConcurrentMap<MaterialType, MaterialEntity> hostMaterial = host.getMaterial();
                if (hostMaterial.containsKey(materialEntity.getMaterialType())) {
                    MaterialEntity hm = hostMaterial.get(materialEntity.getMaterialType());
                    if (hm == null) {
                        hm = composeNewMaterial(materialEntity, hostWarehouse);
                        hostMaterial.put(hm.getMaterialType(), hm);
                    }
                    // should be validation on the material value max capacity
                    MaterialEntity remainingMaterial = updateMaterialValue(playerUuid, hostWarehouse, hm, materialEntity);
                    if (remainingMaterial.getMaterialCurrentValue() > 0) {
                        rollBack(playerUuid, m, hm, backupMaterial, materialEntity);
                        return false;
                    }
                    return true;
                } else {
//                    ConcurrentMap<MaterialType, MaterialEntity> newMaterialToAdd =
//                            composeWarehouseForGivenPlayerAndMaterial(playerUuid, materialEntity, host);
                    MaterialEntity put = host.getMaterial().put(materialEntity.getMaterialType(), materialEntity);
                    host.setMaterial(playerUuid, materialEntity, materialEntity);
                    return true;
                }
            } catch (OptionalExceptionHandler e) {
                Log.warn(log, "Exception is related to Player {} warehouse {}", playerUuid, hostWarehouse);
                return false;
            }
        } else {
            Log.warn(log, "Player does not exist.");
            return false;
        }
    }

    private void rollBack(String playerUuid, MaterialEntity m, MaterialEntity hm, MaterialEntity backup, MaterialEntity materialEntity) {
        int backupMaterialValue = backup.getMaterialCurrentValue();
        int surplusMaterial = materialEntity.getMaterialCurrentValue();
        hm.setMaterialCurrentValue(hm.getMaterialCurrentValue() - backupMaterialValue + surplusMaterial);
        m.setMaterialCurrentValue(m.getMaterialCurrentValue() + backupMaterialValue, playerUuid);
    }

    public Optional<MaterialEntity> findPlayerMaterialByMaterialUuid(String playerUuid, String materialUuid) {
        ConcurrentMap<String, PlayerEntity> players = dataStorage.getPlayers();
        if (players.containsKey(playerUuid)) {
            PlayerEntity playerEntity = players.get(playerUuid);
            return playerEntity.getWarehouseEntityList().stream()
                    .filter(m-> {
                        Optional<MaterialEntity> any = m.getMaterial().values()
                                .stream().filter(mUuid -> mUuid.getMaterialUuid().equals(materialUuid))
                                .findAny();
                        return any.isPresent();
                    })
                    .map(m-> m.getMaterial().values().stream().findFirst())
                    .map(Optional::get)
                    .findAny();
        }
        return Optional.empty();
    }



// here is a question the entire material should be removed or some points from the material.
// in this case the entire material is being removed.
// also could be added api in order to subtract material points.
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
