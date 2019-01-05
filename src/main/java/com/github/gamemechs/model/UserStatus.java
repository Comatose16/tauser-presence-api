package com.github.gamemechs.model;

public enum UserStatus {
    ONLINE ("online"),
    OFFLINE ("offline"),
    IDLE ("idle");

    private String userStatus;

    UserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserStatus() {
        return userStatus;
    }
}
