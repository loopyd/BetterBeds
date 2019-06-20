package com.lupinedream.betterbeds;

/*
  Created by Phoenix616 on 28.01.2015.
 */

/**
 * Enum to specify who should get a notification message.
 * NOONE - Don't display the message to anyone
 * PLAYER - Only to players who trigger a BetterBeds action
 * SLEEPING - Only players who lay in a bed
 * WORLD - Every player who is in the same world
 * SERVER - Every player on the server
 */
public enum NotificationType {
        NOONE,
        PLAYER,
        SLEEPING,
        WORLD,
        SERVER
}
