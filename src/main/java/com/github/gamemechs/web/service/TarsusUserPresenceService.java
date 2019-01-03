package com.github.gamemechs.web.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.model.UserStatus;

@Service
public class TarsusUserPresenceService implements UserPresenceService {

    @Override
    public Map<UserStatus, TarsusUser> getAllUsers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TarsusUser> getUserByStatus(UserStatus userStatus) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TarsusUser udpateUserStatus(Long userId) {
        // TODO Auto-generated method stub
        return null;
    }

}
