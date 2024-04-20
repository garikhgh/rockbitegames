package com.rockbitegames.domain;


import com.rockbitegames.observer.ObserverManger;
import com.rockbitegames.observer.MaterialNotificationSender;
import lombok.*;

import static com.rockbitegames.constants.ConstantValues.*;


@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MaterialEntity implements ObserverInstanceInvoker {




    public MaterialEntity() {
        invokeObserverInstance();
    }

    @Override
    public void invokeObserverInstance() {
        this.observerManger = new ObserverManger(MATERIAL_IS_FULL, MATERIAL_IS_ZERO, MATERIAL_IS_SUBTRACTED);
        subscribe();
    }
    public MaterialEntity backupMaterial() {
        MaterialEntity m = new MaterialEntity();
        m.setMaterialUuid(this.getMaterialUuid());
        m.setMaterialType(this.getMaterialType());
        m.setWarehouseUuid(this.getWarehouseUuid());
        m.setMaterialMaxCapacity(this.getMaterialMaxCapacity());
        m.setMaterialCurrentValue(this.getMaterialCurrentValue());
        m.setName(this.getName());
        m.setDescription(this.getDescription());
        m.setIcon(this.getIcon());
        return m;
    }
    private transient ObserverManger observerManger;

    private String materialUuid;
    private String warehouseUuid;
    private MaterialType materialType;
    private Integer materialMaxCapacity;
    private Integer materialCurrentValue;
    private String name;
    private String description;
    private String icon;

    public void setMaterialCurrentValue(Integer materialCurrentValue, String playerUuid) {

        if (this.materialCurrentValue > materialCurrentValue) {
            this.observerManger.notify(MATERIAL_IS_SUBTRACTED, playerUuid, this.warehouseUuid, this.materialUuid);
        }

        this.materialCurrentValue = materialCurrentValue;
        sendNotification(playerUuid);
    }

    public void subscribe() {
        observerManger.subscribe(MATERIAL_IS_FULL, new MaterialNotificationSender());
        observerManger.subscribe(MATERIAL_IS_ZERO, new MaterialNotificationSender());
        observerManger.subscribe(MATERIAL_IS_SUBTRACTED, new MaterialNotificationSender());
    }

    private void sendNotification(String playerUuid) {

        if (this.materialCurrentValue == 0) {
            this.observerManger.notify(MATERIAL_IS_ZERO,playerUuid, this.warehouseUuid, this.materialUuid);
        } else if (Integer.compare(this.materialCurrentValue, this.materialMaxCapacity) >= 0) {
            this.observerManger.notify(MATERIAL_IS_FULL, playerUuid, this.warehouseUuid, this.materialUuid);
        }


    }
}
