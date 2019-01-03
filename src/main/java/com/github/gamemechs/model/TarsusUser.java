package com.github.gamemechs.model;

/**
 * {@code TarsusUser} is the model for the user presence api.
 */
public class TarsusUser {

    private Long userId;
    private UserStatus status;

    public TarsusUser(Long userId, UserStatus status) {
        this.setUserId(userId);
        this.setStatus(status);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isUserOnline() {
        return this.status.isOnline();
    }

    public boolean isUserOffline() {
        return this.status.isOffline();
    }

    public boolean isUserIdle() {
        return this.status.isIdle();
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
