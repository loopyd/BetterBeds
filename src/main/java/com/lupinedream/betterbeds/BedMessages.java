package com.lupinedream.betterbeds;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Objects;
import java.util.logging.Level;

public class BedMessages implements ConfigInterface {
    private Plugin bbPlugin;

    public NotificationMessage mobMessage;
    public NotificationMessage noSleepMessage;
    public NotificationMessage sleepMessage;
    public NotificationMessage leaveMessage;
    public NotificationMessage wakeMessage;
    public NotificationMessage notifyMessage;
    public NotificationMessage notifyOnSingleMessage;

    public BedMessages(Plugin plugin) {
        bbPlugin = plugin;
    }

    public final boolean load() {
        boolean result;
        mobMessage = loadNotificationMessage("msg.mobs.type","msg.mobs.text");
        noSleepMessage = loadNotificationMessage("msg.noSleep.type", "msg.noSleep.text");
        sleepMessage = loadNotificationMessage("msg.sleep.type","msg.sleep.text");
        leaveMessage = loadNotificationMessage("msg.leave.type","msg.leave.text");
        wakeMessage = loadNotificationMessage("msg.wake.type","msg.wake.text");
        notifyMessage = loadNotificationMessage("msg.notify.type","msg.notify.text");
        notifyOnSingleMessage = loadNotificationMessage("msg.notifyOnSingle.type","msg.notifyOnSingle.text");
        result = mobMessage != null & sleepMessage != null & leaveMessage != null & wakeMessage != null & notifyMessage != null & notifyOnSingleMessage != null;
        return result;
    }

    /**
     * Loads a notification from the config file using a specified type and text (queries as a string)
     * TODO: Impolement default notification objects so that the user can specify how many they'd like to.
     * @param notificationType - qualified type of the notification
     * @param notificationText - text of the notification
     * @return NotificationMessage object or null if failure.
     */
    private NotificationMessage loadNotificationMessage(String notificationType, String notificationText) {
        NotificationMessage result;
        try {
            if (notificationType == null || notificationText == null) throw new IllegalArgumentException();
            result = new NotificationMessage(
                    NotificationType.valueOf(bbPlugin.getConfig().getString(notificationType)),
                    ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(bbPlugin.getConfig().getString(notificationText)))
            );
            return result;
        }
        catch (Exception e) {
            bbPlugin.getLogger().log(Level.WARNING, "Problem loading notification config: " + notificationType + ", there is a typo in your config.yml file." );
        }
        return null;
    }

    public final boolean save() {
        throw new NotImplementedException();
    }

    public final void setDefaults() {
        mobMessage = null;
        noSleepMessage = null;
        sleepMessage = null;
        leaveMessage = null;
        wakeMessage = null;
        notifyMessage = null;
        notifyOnSingleMessage = null;
    }
}
