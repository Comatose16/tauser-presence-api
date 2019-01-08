package com.github.gamemechs.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.model.UserStatus;

/**
 * The {@code UsersMapConfig} is a temporary configuration class to pre populate
 * the map of tarsus users.
 */
@Configuration
public class UsersMapConfig {

    @Bean
    public Map<Long, TarsusUser> usersMap() {
        Map<Long, TarsusUser> popluatedUsersMap = new ConcurrentHashMap<>();
        popluatedUsersMap.put(1L, new TarsusUser(1L, UserStatus.OFFLINE));
        popluatedUsersMap.put(2L, new TarsusUser(2L, UserStatus.OFFLINE));
        popluatedUsersMap.put(3L, new TarsusUser(3L, UserStatus.OFFLINE));
        popluatedUsersMap.put(4L, new TarsusUser(4L, UserStatus.OFFLINE));
        popluatedUsersMap.put(5L, new TarsusUser(5L, UserStatus.OFFLINE));
        popluatedUsersMap.put(6L, new TarsusUser(6L, UserStatus.OFFLINE));

        return popluatedUsersMap;
    }
}
