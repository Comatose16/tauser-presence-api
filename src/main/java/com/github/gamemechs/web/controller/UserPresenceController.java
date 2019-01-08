package com.github.gamemechs.web.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.web.exception.InvalidUserStatusException;
import com.github.gamemechs.web.service.UserPresenceService;

/**
 * The {@code UserPresenceController} handles both RESTful and websocket requests for
 * the Tarsus user presence.
 */
@RestController
public class UserPresenceController {

    @Autowired
    private UserPresenceService tarsusUserPresenceService;

    @GetMapping("/users")
    public Set<TarsusUser> getAllUsers() {
        return tarsusUserPresenceService.getAllUsers();
    }

    @GetMapping("/users/{status}")
    public Set<TarsusUser> getUsers(@PathVariable String status) {
        try {
            return tarsusUserPresenceService.getUsersByStatus(status);

        } catch (InvalidUserStatusException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid User Status", ex);
        }
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck() {
        //For health checks
    }

    @MessageMapping("/user/update")
    @SendTo("/topic/user/update")
    public TarsusUser broadcastUser(@Payload TarsusUser user) throws Exception {

        tarsusUserPresenceService.udpateUserStatus(user);

        return user;
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/errors", broadcast = false)
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}
