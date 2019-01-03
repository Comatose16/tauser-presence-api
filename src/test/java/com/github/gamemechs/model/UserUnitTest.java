package com.github.gamemechs.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserUnitTest {

    private static final long USER_ID = 12345;

    @Test
    public void testIsUserOnline() {
        TarsusUser user = new TarsusUser(USER_ID, UserStatus.ONLINE);
        Assertions.assertTrue(user.isUserOnline());
    }

    @Test
    public void testIsUserOffline() {
        TarsusUser user = new TarsusUser(USER_ID, UserStatus.OFFLINE);
        Assertions.assertTrue(user.isUserOffline());
    }

    @Test
    public void testIsUserIdle() {
        TarsusUser user = new TarsusUser(USER_ID, UserStatus.IDLE);
        Assertions.assertTrue(user.isUserIdle());
    }
}
