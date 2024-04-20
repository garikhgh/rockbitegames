package com.rockbitegames.validator;

import com.rockbitegames.domain.MaterialEntity;
import com.rockbitegames.domain.MaterialType;
import com.rockbitegames.dto.MaterialDto;
import com.rockbitegames.dto.MaterialState;
import com.rockbitegames.mock.MockData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialValidateTest {

    @Test
    void validateMaterial() {
        MaterialEntity materialEntity = MockData.mockMaterial("123", MaterialType.IRON, -1, -1);
        MaterialDto materialDto = new MaterialDto();
        materialDto.setMaterialType(MaterialType.IRON);
        materialDto.setMaterialUuid("");
        materialDto.setMaterialState(MaterialState.ADD);
        materialDto.setMaterialCurrentValue(-1);
        materialDto.setMaterialMaxCapacity(-1);
        MaterialValidate.MaterialValidator materialValidator = MaterialValidate.validateMaterial(materialDto);
        assertNotNull(materialValidator);
        assertEquals(2, materialValidator.getErrorCounter());
    }
}