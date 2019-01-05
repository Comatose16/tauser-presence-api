package com.github.gamemechs.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
import com.github.gamemechs.web.exception.InvalidUserStatusException;
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

    private static Set<TarsusUser> onlineUsers;

    private static Set<TarsusUser> offlineUsers;

    private static Set<TarsusUser> idleUsers;

    private static Set<TarsusUser> allUsers;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TarsusUserPresenceService tarsusUserPresenceService;

    @BeforeAll
    public static void setUp() {
        onlineUsers = getTestUsers(UserStatus.ONLINE, onlineUserIds);
        offlineUsers = getTestUsers(UserStatus.OFFLINE, offlineUserIds);
        idleUsers = getTestUsers(UserStatus.IDLE, idleUserIds);

        allUsers = new HashSet<>();
        allUsers.addAll(onlineUsers);
        allUsers.addAll(offlineUsers);
        allUsers.addAll(idleUsers);
    }

    @Test
    @DisplayName("Tests the all users endpoint")
    public void getAllUsers() throws Exception {
        String allUsersEndpoint = "/users";

        when(tarsusUserPresenceService.getAllUsers()).thenReturn(allUsers);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(allUsersEndpoint)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().string(convertSetToJson(allUsers)));
    }

    @Test
    @DisplayName("Tests the online users endpoint")
    public void getOnlineUsers() throws Exception {
        String onlineEndpoint = "/users/online";

        RequestBuilder requestBuilder = getRequestBulider("online", onlineEndpoint, onlineUsers);

        mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().string(convertSetToJson(onlineUsers)));
    }

    @Test
    @DisplayName("Tests the offline users endpoint")
    public void getOfflineUsers() throws Exception {
        String offlineEndpoint = "/users/offline";

        RequestBuilder requestBuilder = getRequestBulider("offline", offlineEndpoint, offlineUsers);

        mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().string(convertSetToJson(offlineUsers)));
    }

    @Test
    @DisplayName("Tests the idle users endpoint")
    public void getIdleUsers() throws Exception {
        String idleEndpoint = "/users/idle";

        RequestBuilder requestBuilder = getRequestBulider("idle", idleEndpoint, idleUsers);

        mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().string(convertSetToJson(idleUsers)));
    }

    @Test
    @DisplayName("Tests the users endpoint with an invalid user status")
    public void getUsersWithInvalidUserStatus() throws Exception {
        String invalidEndpoint = "/users/invalid";

        RequestBuilder requestBuilder = getRequestBuliderWithException("invalid", invalidEndpoint,
                new InvalidUserStatusException(""));

        mockMvc.perform(requestBuilder)
        .andExpect(status().is4xxClientError());
    }

    private RequestBuilder getRequestBulider(String status, String endpoint, Set<TarsusUser> expectedUsers) throws Exception {
        when(tarsusUserPresenceService.getUsersByStatus(status)).thenReturn(expectedUsers);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(endpoint)
                .accept(MediaType.APPLICATION_JSON);

        return requestBuilder;
    }

    private RequestBuilder getRequestBuliderWithException(String status, String endpoint, Exception ex) throws Exception {
        when(tarsusUserPresenceService.getUsersByStatus(status)).thenThrow(ex);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(endpoint)
                .accept(MediaType.APPLICATION_JSON);

        return requestBuilder;
    }

    private static Set<TarsusUser> getTestUsers(UserStatus userStatus, Long... userIds) {

        Set<TarsusUser> users = new HashSet<>();

        for(Long userId: userIds) {
          users.add(new TarsusUser(userId, userStatus));
        }

        return users;
    }

    private String convertSetToJson(Set<TarsusUser> users) {
       return new Gson().toJson(users);
    }
}
