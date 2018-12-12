package com.bliblifuture.invenger.entity.lendment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum LendmentStatus {
    WaitingForApproval("Waiting for approval"),
    WaitingForPickUp("Waiting for pickup"),
    InLending("In lending"),
    Finished("Finished");

    private String desc;

    LendmentStatus(String s) {
        this.desc = s;
    }

    public String getDesc() {
        return desc;
    }

    public static LendmentStatus toEnum(String s) throws Exception {
        switch (s){
            case "Waiting for approval":
                return LendmentStatus.WaitingForApproval;
            case "Waiting for pickup":
                return LendmentStatus.WaitingForPickUp;
            case "In lending":
                return LendmentStatus.InLending;
            case "Finished":
                return LendmentStatus.Finished;
        }
        throw new Exception();
    }

    public static Boolean isValid(String s){
        try {
            toEnum(s);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public static HashMap<String,String> getMap(){
        HashMap<String,String> map = new HashMap<>();
        for(LendmentStatus s: LendmentStatus.values()){
            map.put(s.toString(),s.getDesc());
        }
        return map;
    }

    public static List<String> getAll(){
        return Arrays.asList(
                WaitingForApproval.desc,
                WaitingForPickUp.desc,
                InLending.desc,
                Finished.desc
        );
    }


}

