package com.github.gamemechs.web.service;

import java.util.List;
import java.util.Map;

import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.model.UserStatus;

public interface UserPresenceService {

    public Map<UserStatus, TarsusUser> getAllUsers();

    public List<TarsusUser> getUserByStatus(UserStatus userStatus);

    public TarsusUser udpateUserStatus(Long userId);
}
