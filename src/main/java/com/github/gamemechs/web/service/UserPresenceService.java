package com.github.gamemechs.web.service;

import java.util.Set;

import com.github.gamemechs.model.TarsusUser;
import com.github.gamemechs.web.exception.InvalidUserStatusException;

/**
 * The {@code UserPresenceService} interface provides a common contract for services
 * that handle user presence.
 */
public interface UserPresenceService {

    /**
     * Returns all users
     *
     * @return a set of all users
     */
    public Set<TarsusUser> getAllUsers();

    /**
     * Returns users by status
     *
     * @param userStatus target user status
     * @return a set of users with a specific user status
     */
    /**
     * Returns users by status
     *
     * @param userStatus user status
     * @return a set of users with a specific user status
     * @throws InvalidUserStatusException if the user status is invalid
     */
    public Set<TarsusUser> getUsersByStatus(String userStatus) throws InvalidUserStatusException;

    /**
     * Updates the user's status
     *
     * @param user user id to update
     * @return TarsusUser updated user
     */
    public void udpateUserStatus(TarsusUser user);
}
