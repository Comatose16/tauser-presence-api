package com.github.gamemechs.web.controller;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.model.UserStatus;
import com.github.gamemechs.web.TestMessageChannel;
import com.github.gamemechs.web.service.UserPresenceService;


public class UserPresenceWebsocketControllerUnitTest {

    private static final Long USER_ID = new Long(12345L);

    private static final String DESTINATION_PREFIX = "/ws";

    private static final String TOPIC_DESTINATION = "/topic/user/update";

    private static final String PRESENCE_DESTINATION = "/ws/user/update";

    private TestAnnotationMethodHandler annotationMethodHandler;

    @Mock
    private TestMessageChannel mockInBoundChannel;

    @Mock
    private TestMessageChannel mockOutBoundChannel;

    @Mock
    private SimpMessagingTemplate mockBrokerTemplate;

    @Mock
    private UserPresenceService userPresenceService;

    @InjectMocks
    private UserPresenceController controller = new UserPresenceController();

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        this.annotationMethodHandler = new TestAnnotationMethodHandler(mockInBoundChannel, mockOutBoundChannel,
                mockBrokerTemplate);

        this.annotationMethodHandler.registerHandler(controller);
        this.annotationMethodHandler.setDestinationPrefixes(Arrays.asList(DESTINATION_PREFIX));
        this.annotationMethodHandler.setMessageConverter(new MappingJackson2MessageConverter());
        this.annotationMethodHandler.setApplicationContext(new StaticApplicationContext());
        this.annotationMethodHandler.afterPropertiesSet();
    }

    @Test
    public void broadcastUser() throws JsonProcessingException {
        TarsusUser testUser =  new TarsusUser(USER_ID, UserStatus.ONLINE);
        byte[] payload = new ObjectMapper().writeValueAsBytes(testUser);

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setDestination(PRESENCE_DESTINATION);
        headers.setSessionId("sessionID");
        headers.setSessionAttributes(new HashMap<>());

        Message<byte[]> message =  MessageBuilder
                .withPayload(payload)
                .setHeaders(headers)
                .build();

        this.annotationMethodHandler.handleMessage(message);

        Mockito.verify(userPresenceService, Mockito.times(1)).udpateUserStatus(Mockito.any(TarsusUser.class));

        Mockito.verify(mockBrokerTemplate, Mockito.times(1)).convertAndSend(Mockito.eq(TOPIC_DESTINATION),
                Mockito.any(TarsusUser.class), Mockito.anyMap());
    }

    /**
     * Exposes a public method for manually registering a controller.
     */
    private static class TestAnnotationMethodHandler extends SimpAnnotationMethodMessageHandler{

        public TestAnnotationMethodHandler(SubscribableChannel clientInboundChannel,
                MessageChannel clientOutboundChannel, SimpMessageSendingOperations brokerTemplate) {

            super(clientInboundChannel, clientOutboundChannel, brokerTemplate);
        }

        public void registerHandler(Object handler) {
            super.detectHandlerMethods(handler);
        }
    }
}
