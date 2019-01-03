package com.github.gamemechs.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.model.UserStatus;
import com.github.gamemechs.web.service.TarsusUserPresenceService;
import com.google.gson.Gson;

/**
 * The {@code UserPresenceControllerUnitTest} tests the functionality of the
 * {@link UserPresenceController}.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserPresenceController.class)
class UserPresenceControllerUnitTest {

    private static final Long[] onlineUserIds = new Long[] { 1000L, 1100L, 1110L, 1111L };

    private static final Long[] offlineUserIds = new Long[] { 2000L, 2200L, 2220L, 2222L };

    private static final Long[] idleUserIds = new Long[] { 3000L, 3300L, 3330L, 3333L };

    private static List<TarsusUser> onlineUsers;

    private static List<TarsusUser> offlineUsers;

    private static List<TarsusUser> idleUsers;

    private static Map<UserStatus, List<TarsusUser>> allUsers;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TarsusUserPresenceService tarsusUserPresenceService;

    @BeforeAll
    public static void setUp() {
        onlineUsers = getTestUsers(UserStatus.ONLINE, onlineUserIds);
        offlineUsers = getTestUsers(UserStatus.OFFLINE, offlineUserIds);
        idleUsers = getTestUsers(UserStatus.IDLE, idleUserIds);

        allUsers = new HashMap<>();
        allUsers.put(UserStatus.ONLINE, onlineUsers);
        allUsers.put(UserStatus.OFFLINE, offlineUsers);
        allUsers.put(UserStatus.IDLE, idleUsers);
    }

    @Test
    public void getOnlineUsers() throws Exception {

        when(tarsusUserPresenceService.getUserByStatus(UserStatus.ONLINE)).thenReturn(onlineUsers);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/online")
                .accept(MediaType.APPLICATION_JSON);

       mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().string(convertListToJson(onlineUsers)));
    }

    private static List<TarsusUser> getTestUsers(UserStatus userStatus, Long... userIds) {

        List<TarsusUser> users = new ArrayList<>();

        for(Long userId: userIds) {
          users.add(new TarsusUser(userId, userStatus));
        }

        return users;
    }

    private String convertListToJson(List<TarsusUser> users) {
       return new Gson().toJson(users);
    }
}
