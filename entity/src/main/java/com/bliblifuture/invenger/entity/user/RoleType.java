package com.bliblifuture.invenger.entity.user;

public enum RoleType {
    ROLE_ADMIN,ROLE_USER;

    public static boolean isEqual(RoleType role, String r){
        return role.toString().equals(r);
    }
}
