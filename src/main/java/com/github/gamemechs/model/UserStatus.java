package com.github.gamemechs.model;

public enum UserStatus {
    ONLINE {
        @Override
        public boolean isOnline() {
            return true;
        }
    },
    OFFLINE {
        @Override
        public boolean isOffline() {
            return true;
        }
    },
    IDLE {
        @Override
        public boolean isIdle() {
            return true;
        }
    };

    public boolean isOnline() {
        return false;
    }

    public boolean isOffline() {
        return false;
    }

    public boolean isIdle() {
        return false;
    }
}
