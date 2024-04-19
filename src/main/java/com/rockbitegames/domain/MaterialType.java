package com.rockbitegames.domain;

import com.rockbitegames.constants.ConstantValues;

public enum MaterialType {

    IRON(ConstantValues.IRON_MAX_CAPACITY),
    COOPER(ConstantValues.COOPER_MAX_CAPACITY),
    BOLT(ConstantValues.BOLT_MAX_CAPACITY);

    private final int maxCapacity;
    MaterialType(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    public int getMaxCapacity() {
        return this.maxCapacity;
    }
}
