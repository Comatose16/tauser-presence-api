package com.github.gamemechs.web.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.model.UserStatus;
import com.github.gamemechs.web.exception.InvalidUserStatusException;

/**
 * The {@code TarsusUserPresenceService} is the Tarsus implementation of the {@link UserPresenceService}
 */
@Service
public class TarsusUserPresenceService implements UserPresenceService {

    private final static Logger logger = LoggerFactory.getLogger(TarsusUserPresenceService.class);

    private Map<Long, TarsusUser> usersMap;

    @Override
    public  Set<TarsusUser> getAllUsers() {
        return usersMap.values()
                .stream()
                .collect(Collectors.toSet());
    }

    @Override
    public Set<TarsusUser> getUsersByStatus(String userStatus) throws InvalidUserStatusException{
        try {
            UserStatus userStatusEnumValue = UserStatus.valueOf(userStatus.toUpperCase());

            return usersMap.values()
                    .stream()
                    .filter(user -> user.getStatus().equals(userStatusEnumValue))
                    .collect(Collectors.toSet());

        } catch (IllegalArgumentException ex) {
            logger.error("Invalid user status: " + userStatus);
            throw new InvalidUserStatusException("Invalid user status", ex);
        }
    }

    @Override
    public void udpateUserStatus(TarsusUser user) {
        usersMap.put(user.getUserId(), user);
    }

    @Autowired
    public void setUsersMap(Map<Long, TarsusUser> usersMap) {
        this.usersMap = usersMap;
    }
}
