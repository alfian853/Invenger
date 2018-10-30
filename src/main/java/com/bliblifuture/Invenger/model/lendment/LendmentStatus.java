package com.bliblifuture.Invenger.model.lendment;

public enum LendmentStatus {

    WaitingForPickUp("Waiting for pickup"),
    InLending("In lending"),
    Returned("Returned"),
    PartiallyReturned("Partially returned");

    private String desc;

    LendmentStatus(String s) {
        this.desc = s;
    }

    public String getDesc() {
        return desc;
    }

    public static LendmentStatus toEnum(String s){
        switch (s){
            case "Waiting for pickup":
                return LendmentStatus.WaitingForPickUp;
            case "In lending":
                return LendmentStatus.InLending;
            case "Returned":
                return LendmentStatus.Returned;
            case "Partially returned":
                return LendmentStatus.PartiallyReturned;
        }
        return null;
    }


}

