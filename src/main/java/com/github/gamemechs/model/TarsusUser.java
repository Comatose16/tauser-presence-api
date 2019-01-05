package com.github.gamemechs.model;

import java.io.Serializable;

/**
 * {@code TarsusUser} is the model for the user presence api.
 */
public class TarsusUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private UserStatus status;

    public TarsusUser() {

    }

    public TarsusUser(Long userId, UserStatus status) {
        this.setUserId(userId);
        this.setStatus(status);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
