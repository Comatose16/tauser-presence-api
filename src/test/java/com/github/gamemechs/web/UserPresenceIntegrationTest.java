package com.github.gamemechs.web;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.model.UserStatus;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserPresenceIntegrationTest {

   private static final Long USER_ID = new Long(1111L);

    private static final String USER_PRESENCE_ENDPOINT = "/ws/user/update";

    private static final String SUBSCRIBE_TOPIC = "/topic/user/update";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String webSocketURL;

    private CompletableFuture<TarsusUser> completableFuture;

    @BeforeEach
    public void setup() {
        webSocketURL = "ws://localhost:" + port + "/user/update";
        completableFuture = new CompletableFuture<>();
    }

    @Test
    public void broadcastOnlineUser() throws InterruptedException, ExecutionException, TimeoutException {

       StompSession stompSession = subscribeToTopic();

       stompSession.subscribe(SUBSCRIBE_TOPIC, new DefaultStompFrameHandler());

       broadcastUser(stompSession, UserStatus.ONLINE);

       TarsusUser user = completableFuture.get(5,TimeUnit.SECONDS);

       //TarsusUser otherUser = restTemplate.getForObject("/users/online", TarsusUser.class);

       Assertions.assertNotNull(user);
       Assertions.assertAll("user",
               () -> assertEquals(USER_ID, user.getUserId()),
               () -> assertEquals(UserStatus.ONLINE, user.getStatus())
       );
    }

    @Test
    public void broadcastOfflineUser() throws InterruptedException, ExecutionException, TimeoutException {

       StompSession stompSession = subscribeToTopic();

       broadcastUser(stompSession, UserStatus.OFFLINE);

       TarsusUser user = completableFuture.get(5,TimeUnit.SECONDS);

       //restTemplate.getForObject("/users/offline", TarsusUser.class);

       Assertions.assertNotNull(user);
       Assertions.assertAll("user",
               () -> assertEquals(USER_ID, user.getUserId()),
               () -> assertEquals(UserStatus.OFFLINE, user.getStatus())
       );
    }

    @Test
    public void broadcastIdleUser() throws InterruptedException, ExecutionException, TimeoutException {

       StompSession stompSession = subscribeToTopic();

       broadcastUser(stompSession, UserStatus.IDLE);

       TarsusUser user = completableFuture.get(5,TimeUnit.SECONDS);

       //restTemplate.getForObject("/users/offline", TarsusUser.class);

       Assertions.assertNotNull(user);
       Assertions.assertAll("user",
               () -> assertEquals(USER_ID, user.getUserId()),
               () -> assertEquals(UserStatus.IDLE, user.getStatus())
       );
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

    private void broadcastUser(StompSession stompSession, UserStatus status) {
        TarsusUser messageToSend = new TarsusUser(USER_ID, status);

        stompSession.send(USER_PRESENCE_ENDPOINT, messageToSend);
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        return transports;
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
