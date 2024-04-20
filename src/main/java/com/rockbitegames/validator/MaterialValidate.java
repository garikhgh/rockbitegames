package com.rockbitegames.validator;

import com.rockbitegames.domain.MaterialType;
import com.rockbitegames.dto.MaterialDto;
import com.rockbitegames.dto.MaterialState;
import com.rockbitegames.exception.WrongMaterialStateException;
import com.rockbitegames.util.Log;
import com.rockbitegames.util.WarehouseServiceUtil;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaterialValidate {
    private MaterialValidate() {throw new ExceptionInInitializerError("Not allowed to create object.");}

    private static final Logger logger = LoggerFactory.getLogger(WarehouseServiceUtil.class);

    public static MaterialValidator validateMaterial(MaterialDto materialDto) {
        MaterialValidator materialValidator = new MaterialValidator();
        MaterialState materialState = materialDto.getMaterialState();
        materialStateValidator(materialValidator, materialDto);
        materialTypeValidator(materialValidator, materialDto);
        materialCapacityValidator(materialValidator ,materialDto);

        return materialValidator;
    }

    private static void materialStateValidator(MaterialValidator materialValidator, MaterialDto materialDto) {
        MaterialState materialState = materialDto.getMaterialState();
        String name = materialState.name();
        try {
            MaterialState materialState1 = MaterialState.valueOf(name);
        } catch (WrongMaterialStateException e) {
            String value = String.format("Wrong Material State: It should be one of the following %s", Arrays.stream(MaterialState.values()).toArray());
            Log.error(logger, "Wrong MaterialState: It should be one of the following {}", Arrays.stream(MaterialState.values()).toArray());
            materialValidator.setError(value);
        }
    }

    private static void materialTypeValidator(MaterialValidator materialValidator, MaterialDto materialDto) {
        MaterialType materialType = materialDto.getMaterialType();
        String name = materialType.name();
        try {
            MaterialType materialState1 = MaterialType.valueOf(name);
        } catch (WrongMaterialStateException e) {
            String value = String.format("Wrong Material MaterialType: It should be one of the following %s", Arrays.stream(MaterialType.values()).toArray());
            Log.error(logger, "Wrong MaterialType: It should be one of the following {}", Arrays.stream(MaterialType.values()).toArray());
            materialValidator.setError(value);
        }
    }
    private static void materialCapacityValidator(MaterialValidator materialValidator, MaterialDto materialDto) {
        if (materialDto.getMaterialMaxCapacity() < 0) {
            materialValidator.setError("MaterialMaxCapacity Could not be Negative.");
        }
        if (materialDto.getMaterialCurrentValue() < 0) {
            materialValidator.setError("MaterialCurrentValue Could not be Negative.");
        }

        if (materialDto.getMaterialCurrentValue() > materialDto.getMaterialMaxCapacity()) {
            materialValidator.setError("MaterialCurrentValue Could not be More than MaterialMaxCapacity.");
        }
    }

    // todo: cold be added other validators


    @Getter
    @NoArgsConstructor
    public static class MaterialValidator {
        private int errorCounter;
        private final List<String> error = new ArrayList<>();

        public void setError(String error) {
            this.error.add(error);
            this.errorCounter ++;
        }
    }
}
