package com.bliblifuture.invenger.entity.inventory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum InventoryType {
    Stockable,
    Consumable;

    public static List<InventoryType> getAllType(){
        return Arrays.asList(InventoryType.values());
    }

    public static List<String> getAllTypeAsString(){
        return Arrays.stream(InventoryType.values()).map(Objects::toString).collect(Collectors.toList());
    }
}
