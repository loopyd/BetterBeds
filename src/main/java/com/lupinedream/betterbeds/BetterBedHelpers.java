package com.lupinedream.betterbeds;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class BetterBedHelpers {

    private Plugin bbPlugin;
    private BedGlobals bedGlobals;
    private BedMessages bedMessages;
    private WorldHelpers worldHelpers;

    public BetterBedHelpers(Plugin plugin, BedGlobals globals, BedMessages messages, WorldHelpers wh) {
        bbPlugin = plugin;
        bedGlobals = globals;
        bedMessages = messages;
        worldHelpers = wh;
    }

    /**
     * Check if enough players are asleep.
     * @param world The world to calculate with
     * @param playerQuit Set if the player has quit
     */
    public boolean isPlayerLimitSatisfied(World world, boolean playerQuit) {
        if(!bedGlobals.asleepPlayers.containsKey(world.getUID()) || bedGlobals.asleepPlayers.get(world.getUID()).size() == 0)
            return false;

        int calculatedPlayers = (playerQuit) ? -1 : 0;
        for(Player p : bbPlugin.getServer().getOnlinePlayers()) {
            if(world.equals(p.getWorld())) {
                if (!p.isSleeping())
                    return false;
                if (p.hasPermission("betterbeds.sleep") && !p.hasPermission("betterbeds.ignore") && !worldHelpers.isPlayerAFK(p))
                    calculatedPlayers++;
            }
        }
        HashSet<UUID> playerList = bedGlobals.asleepPlayers.get(world.getUID());
        return (playerList.size() >= bedGlobals.minPlayers && playerList.size() >= calculatedPlayers * bedGlobals.sleepPercentage)
                || (playerList.size() < bedGlobals.minPlayers && playerList.size() >= calculatedPlayers);
    }

    /**
     * Check if enough players are asleep and fast forward if so.
     * @param world The world to calculate with
     * @param playerQuit Has the player requested to quit?
     */
    public boolean checkPlayers(final World world, boolean playerQuit) {
        if (isPlayerLimitSatisfied(world, playerQuit)) {
            if (bedGlobals.nightSpeed == 0) {
                bbPlugin.getLogger().log(Level.INFO, "Set time to dawn in world " + world.getName());
                notifyPlayers(world, (bedGlobals.asleepPlayers.get(world.getUID()).size() > 1) ? bedMessages.notifyMessage : bedMessages.notifyOnSingleMessage);
                worldHelpers.setWorldToMorning(world);
                notifyPlayers(world, bedMessages.wakeMessage);
                bedGlobals.asleepPlayers.get(world.getUID()).clear();
            } else {
                if (bedGlobals.transitionTask != 0)
                    return false;

                notifyPlayers(world, (bedGlobals.asleepPlayers.get(world.getUID()).size() > 1) ? bedMessages.notifyMessage : bedMessages.notifyOnSingleMessage);

                bbPlugin.getLogger().log(Level.INFO, "Timelapsing " + bedGlobals.nightSpeed + "x until dawn in world " + world.getName());
                bedGlobals.transitionTask = bbPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(bbPlugin, () -> {
                    if (!isPlayerLimitSatisfied(world, false)) {
                        bbPlugin.getServer().getScheduler().cancelTask(bedGlobals.transitionTask);
                        bedGlobals.transitionTask = 0;
                        return;
                    }
                    long currentTime = world.getTime();
                    long newTime = currentTime + bedGlobals.nightSpeed;
                    if (newTime >= 23450) {
                        bbPlugin.getServer().getScheduler().cancelTask(bedGlobals.transitionTask);
                        bedGlobals.transitionTask = 0;
                        worldHelpers.setWorldToMorning(world);
                        notifyPlayers(world, bedMessages.wakeMessage);
                        bedGlobals.asleepPlayers.get(world.getUID()).clear();
                    } else {
                        world.setTime(currentTime + bedGlobals.nightSpeed);
                    }
                }, 1L, 1L);
            }
            return true;
        }
        return false;
    }

    /**
     * Notifies all the players within a set scope of skipping the night.
     * @param world The world to send the notification in
     * @param notifymsg The NotificationMessage object to parse/send.
     * @param mentionedPlayerUUID The UUID of the player being mentioned in the message
     */
     public void notifyPlayers(World world, NotificationMessage notifymsg, UUID mentionedPlayerUUID) {
        if(notifymsg.getType() != NotificationType.NOONE) {
            HashSet<UUID> playerList = bedGlobals.asleepPlayers.get(world.getUID());
            String msg = buildMsg(notifymsg.getText(),
                    worldHelpers.getPlayerNameFromUUID(mentionedPlayerUUID),
                    playerList != null ? playerList.size() : 0,
                    worldHelpers.countQualifyingPlayers(world));

            /* Build the player list to send the notification based on
               the value of type. */
            List<Player> pl = new ArrayList<>();
            if (notifymsg.getType() == NotificationType.WORLD)
                pl = worldHelpers.getPlayers(world);
            else if (notifymsg.getType() == NotificationType.SERVER)
                pl = new ArrayList<>(bbPlugin.getServer().getOnlinePlayers());
            else if (notifymsg.getType() == NotificationType.SLEEPING) {
                for (Player p : bbPlugin.getServer().getOnlinePlayers())
                    if (playerList.contains(p.getUniqueId()))
                        pl.add(p);
            }
            else if (notifymsg.getType() == NotificationType.PLAYER)
                pl.add(bbPlugin.getServer().getPlayer(mentionedPlayerUUID));
            for (Player p : pl) {
                p.sendMessage(ChatColor.GOLD + msg);
            }
        }
    }

    /**
     * Notifies all the players within a world of skipping the night
     * @param world The world to send the notification in.
     * @param notifymsg The NotificationMessage object to send.
     */
    public void notifyPlayers(World world, NotificationMessage notifymsg) {
        notifyPlayers(world, notifymsg, bedGlobals.lastPlayerToEnterBed);
    }

    /**
     * Calculates what happens when a player leaves the bed.
     * @param player The player who left the bed
     * @param world The world the bed was in (because it's possible the player isn't there anymore when he existed it)
     * @return boolean - True if we don't need to check the players anymore, False if didn't get checked if we should fast forward
     */
    public boolean calculateBedleave(Player player, World world) {

        if(world.getEnvironment() == World.Environment.NORMAL && world.getTime() >= 12500 && world.getTime() <= 100)
            return true;
        if(!bedGlobals.asleepPlayers.containsKey(world.getUID()))
            return true;

        if(bedGlobals.asleepPlayers.get(world.getUID()).contains(player.getUniqueId())) {
            int calculatedPlayers = worldHelpers.countQualifyingPlayers(world);

            bedGlobals.asleepPlayers.get(world.getUID()).remove(player.getUniqueId());

            HashSet<UUID> playerList = bedGlobals.asleepPlayers.get(world.getUID());

            bbPlugin.getLogger().log(Level.INFO, player.getName() + " is not sleeping anymore. " + playerList.size() + "/" + calculatedPlayers + " players are asleep in world " + world.getName());

            notifyPlayers(world, bedMessages.leaveMessage, player.getUniqueId());

            this.checkPlayers(world, false);
            return true;
        }
        return false;
    }

    /**
     * Converts eventual parameters in a message into its real values.
     * TODO: Make it so that not every parameter is required!
     * @param msg String of the message to convert
     * @param playername String of the playername to insert in the message
     * @param sleeping Integer of sleeping players
     * @param online Integer of online players in world
     * @return String of the converted message
     */
    public String buildMsg(String msg, String playername, int sleeping, int online) {
        if (playername != null)
            msg = msg.replace("{player}", playername);
        msg = msg.replace("{sleeping}", sleeping + "");
        msg = msg.replace("{online}", online + "");
        float percentage = (float) Math.round(((double) sleeping / online * 100 * 100) / 100 );
        msg = msg.replace("{percentage}", String.format("%.2f", percentage));
        int more = (int) (Math.ceil(online * bedGlobals.sleepPercentage) - sleeping);
        msg = msg.replace("{more}", Integer.toString(more));
        return msg;
    }
}
