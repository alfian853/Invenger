package com.bliblifuture.invenger.model.inventory;

import java.util.Arrays;
import java.util.List;

public enum InventoryType {
    Stockable,
    Consumable;

    public static List<InventoryType> getAllType(){
        return Arrays.asList(InventoryType.values());
    }
}
