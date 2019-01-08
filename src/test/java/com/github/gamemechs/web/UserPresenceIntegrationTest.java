package com.github.gamemechs.web;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.MultipleFailuresError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.model.UserStatus;
import com.google.gson.Gson;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserPresenceIntegrationTest {

   private static final Long ONLINE_USER_ID = new Long(1L);

   private static final Long OFFLINE_USER_ID = new Long(7L);

   private static final Long IDLE_USER_ID = new Long(2L);

    private static final String USER_PRESENCE_ENDPOINT = "/ws/user/update";

    private static final String SUBSCRIBE_TOPIC = "/topic/user/update";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String webSocketURL;

    private String tarsusUsersUrl;

    private CompletableFuture<TarsusUser> completableFuture;

    private Gson gson = new Gson();

    @BeforeEach
    public void setup() {
        webSocketURL = "ws://localhost:" + port + "/user/update";
        tarsusUsersUrl = "http://localhost:" + port;

        completableFuture = new CompletableFuture<>();
    }

    @Test
    public void getAllUsers() throws JSONException {
        ResponseEntity<String> response = restTemplate.getForEntity(tarsusUsersUrl + "/users", String.class);
        JSONArray offlineUsers = new JSONArray(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(6, offlineUsers.length());
    }

    @Test
    public void broadcastOnlineUser() throws InterruptedException, ExecutionException, TimeoutException {

       StompSession stompSession = subscribeToTopic();

       stompSession.subscribe(SUBSCRIBE_TOPIC, new DefaultStompFrameHandler());

       broadcastUser(stompSession, ONLINE_USER_ID, UserStatus.ONLINE);

       assertUserStatusBroadcast(ONLINE_USER_ID, UserStatus.ONLINE);

       assertOnlineUsers();
    }

    @Test
    public void broadcastOfflineUser() throws InterruptedException, ExecutionException, TimeoutException, JSONException {

       StompSession stompSession = subscribeToTopic();

       broadcastUser(stompSession, OFFLINE_USER_ID, UserStatus.OFFLINE);

       assertUserStatusBroadcast(OFFLINE_USER_ID, UserStatus.OFFLINE);

       assertOfflineUsers();
    }

    @Test
    public void broadcastIdleUser() throws InterruptedException, ExecutionException, TimeoutException {

       StompSession stompSession = subscribeToTopic();

       broadcastUser(stompSession, IDLE_USER_ID, UserStatus.IDLE);

       assertUserStatusBroadcast(IDLE_USER_ID, UserStatus.IDLE);

       assertIdleUsers();
    }

    @Test
    public void healthCheck() {
        ResponseEntity<String> response = restTemplate.getForEntity(tarsusUsersUrl + "/", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private StompSession subscribeToTopic() throws InterruptedException, ExecutionException, TimeoutException {
        SockJsClient client = new SockJsClient(createTransportClient());

        WebSocketStompClient stompClient =  new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient
                .connect(webSocketURL, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        stompSession.subscribe(SUBSCRIBE_TOPIC, new DefaultStompFrameHandler());

        return stompSession;
    }

    private void broadcastUser(StompSession stompSession, Long userId, UserStatus status) {
        TarsusUser messageToSend = new TarsusUser(userId, status);

        stompSession.send(USER_PRESENCE_ENDPOINT, messageToSend);
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        return transports;
    }

    private void assertUserStatusBroadcast(Long userId, UserStatus status)
            throws InterruptedException, ExecutionException, TimeoutException, MultipleFailuresError {
        TarsusUser user = completableFuture.get(5, TimeUnit.SECONDS);

        Assertions.assertNotNull(user);
        Assertions.assertAll("user",
                () -> assertEquals(userId, user.getUserId()),
                () -> assertEquals(status, user.getStatus()));
    }

    private void assertOnlineUsers() {
        Set<TarsusUser> expectedOnlineUsers = new HashSet<>(Arrays.asList(new TarsusUser(ONLINE_USER_ID, UserStatus.ONLINE)));

        ResponseEntity<String> response = restTemplate.getForEntity(tarsusUsersUrl + "/users/online", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gson.toJson(expectedOnlineUsers), response.getBody());
    }

    private void assertOfflineUsers() throws JSONException {
        ResponseEntity<String> response = restTemplate.getForEntity(tarsusUsersUrl + "/users/offline", String.class);
        JSONArray offlineUsers = new JSONArray(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(7, offlineUsers.length());
    }

    private void assertIdleUsers() {
        Set<TarsusUser> expectedIdleUsers = new HashSet<>(Arrays.asList(new TarsusUser(IDLE_USER_ID, UserStatus.IDLE)));

        ResponseEntity<String> response = restTemplate.getForEntity(tarsusUsersUrl + "/users/idle", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), gson.toJson(expectedIdleUsers));
    }

    private class DefaultStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {

            return TarsusUser.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            completableFuture.complete((TarsusUser) payload);
        }
    }
}