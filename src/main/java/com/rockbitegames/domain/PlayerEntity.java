package com.rockbitegames.domain;

import com.rockbitegames.observer.ObserverManger;
import com.rockbitegames.observer.PlayerNotificationSender;
import lombok.*;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.rockbitegames.constants.ConstantValues.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PlayerEntity implements ObserverInstanceInvoker {

    public PlayerEntity() {
        this.invokeObserverInstance();
    }

    @Override
    public void invokeObserverInstance() {
        this.observerManger = new ObserverManger(WAREHOUSE_IS_CREATED);
        subscribe();
    }

    private transient ObserverManger observerManger;
    private String playerUuid;
    private String playerName;
    private String playerEmail;
    private String playerLevel;
    private int numberOfWarehouses;

    @Builder.Default
    CopyOnWriteArrayList<WarehouseEntity> warehouseEntityList = new CopyOnWriteArrayList<>();

    public void setWarehouse(CopyOnWriteArrayList<WarehouseEntity> warehouseList) {
        this.warehouseEntityList = warehouseList;
        this.observerManger.notify(WAREHOUSE_IS_CREATED, this.getPlayerUuid(), this.warehouseToString(), "" );
    }

    public void subscribe() {
        observerManger.subscribe(WAREHOUSE_IS_CREATED, new PlayerNotificationSender());
    }

    private String warehouseToString() {
        return warehouseEntityList.stream().map(WarehouseEntity::getWarehouseUuid).collect(Collectors.joining(","));
    }
}
