package com.rockbitegames.util;

import com.rockbitegames.domain.MaterialEntity;
import com.rockbitegames.dto.MaterialDto;
import com.rockbitegames.exception.OptionalExceptionHandler;
import com.rockbitegames.service.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class WarehouseServiceUtil {

    private WarehouseServiceUtil() {
        throw new ExceptionInInitializerError("Does not allowed to be created.");
    }

    private static final Logger logger = LoggerFactory.getLogger(WarehouseServiceUtil.class);
    public static void setWarehouseUuidToMaterial(WarehouseService warehouseService, MaterialEntity materialEntity, MaterialDto materialDto) {

        try {
            Optional<String> whUuidOptional = warehouseService.findWarehouseUuidForGivenPlayerWithGivenMaterialUuid(materialDto.getPlayerUuid(), materialEntity.getMaterialUuid());
            String whUuid = GetOptionalValue.getOptional(whUuidOptional);
            materialEntity.setWarehouseUuid(whUuid);
        } catch (OptionalExceptionHandler exceptionHandler) {
            Log.error(logger,"Could not parse Optional Value for Warehouse.");
        }

    }
}
