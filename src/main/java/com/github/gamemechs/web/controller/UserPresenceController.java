package com.github.gamemechs.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.model.UserStatus;
import com.github.gamemechs.web.service.UserPresenceService;

/**
 * The {@code UserPresenceController} handles both RESTful and websocket requests.
 */
@RestController
public class UserPresenceController {

    @Autowired
    private UserPresenceService tarsusUserPresenceService;

    @GetMapping("/users/online")
    public List<TarsusUser> getOnlineUsers() {
        return tarsusUserPresenceService.getUserByStatus(UserStatus.ONLINE);
    }

    @GetMapping("/users/offline")
    public List<TarsusUser> getOfflineUsers() {
        return tarsusUserPresenceService.getUserByStatus(UserStatus.OFFLINE);

    }

    @GetMapping("/users/idle")
    public List<TarsusUser> getIdleUsers() {
        return tarsusUserPresenceService.getUserByStatus(UserStatus.IDLE);

    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck() {
        //For health checks
    }

    @MessageMapping("/users.update")
    public TarsusUser broadcastUser(@Payload Long user) {
        return null;
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/errors", broadcast = false)
    public String handleException(Exception e) {
        return null;
    }
}
