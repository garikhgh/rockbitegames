package com.rockbitegames.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rockbitegames.observer.ObserverManger;
import com.rockbitegames.observer.WarehouseNotificationSender;
import lombok.*;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.rockbitegames.constants.ConstantValues.WAREHOUSE_MATERIAL_ADD;
import static com.rockbitegames.constants.ConstantValues.WAREHOUSE_MATERIAL_REMOVAL;


@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class WarehouseEntity implements ObserverInstanceInvoker {

        public WarehouseEntity() {
                invokeObserverInstance();
        }

        @Override
        public void invokeObserverInstance() {
                this.observerManger = new ObserverManger(WAREHOUSE_MATERIAL_ADD, WAREHOUSE_MATERIAL_REMOVAL);
                subscribe();
        }

        private transient ObserverManger observerManger;
        private String warehouseUuid;
        private ConcurrentMap<MaterialType, MaterialEntity> material = new ConcurrentHashMap<>();

        public void setMaterial(String playerUuid, MaterialEntity materialEntity, ConcurrentMap<MaterialType, MaterialEntity> m) {
                this.material = m;
                this.observerManger.notify(WAREHOUSE_MATERIAL_ADD, playerUuid, materialEntity.getMaterialUuid(), materialEntity.getName());

        }

        public Optional<MaterialEntity> removeMaterialByMaterialByMaterialUuid(String playerUuid, MaterialEntity materialEntity) {
                if (this.material.containsKey(materialEntity.getMaterialType())) {
                        MaterialEntity rm = this.material.remove(materialEntity.getMaterialType());
                        this.observerManger.notify(WAREHOUSE_MATERIAL_REMOVAL, playerUuid, materialEntity.getMaterialUuid(), materialEntity.getName());
                        return Optional.ofNullable(rm);
                }
                return Optional.empty();
        }

        public void subscribe() {
               this.observerManger.subscribe(WAREHOUSE_MATERIAL_ADD, new WarehouseNotificationSender());
               this.observerManger.subscribe(WAREHOUSE_MATERIAL_REMOVAL, new WarehouseNotificationSender());
        }


}
