package com.lupinedream.betterbeds;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

public class BetterBedsListeners implements Listener {
    private Plugin bbPlugin;
    private BedGlobals bedGlobals;
    private BedMessages bedMessages;
    private WorldHelpers worldHelpers;
    private BetterBedHelpers bedHelpers;

    public BetterBedsListeners(Plugin plugin, BedGlobals globals, BedMessages messages, WorldHelpers wh, BetterBedHelpers bh) {
        bbPlugin = plugin;
        bedGlobals = globals;
        bedMessages = messages;
        worldHelpers = wh;
        bedHelpers = bh;
    }

    /**
     * Recalculates the number of sleeping players if a player leaves his bed
     * @param event PlayerBedLeaveEvent
     */
    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        bedHelpers.calculateBedleave(event.getPlayer(), event.getBed().getWorld());
    }

    /**
     * Recalculates the number of sleeping players if a player quits the game between 12500 and 100 time ticks
     * @param event PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!bedHelpers.calculateBedleave(event.getPlayer(), event.getPlayer().getWorld()))
            bedHelpers.checkPlayers(event.getPlayer().getWorld(), true);
    }

    /**
     * Recalculates the number of sleeping players if a player changes from a normal world between 12500 and 100 time ticks
     * @param event PlayerChangedWorldEvent
     */
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if(!bedHelpers.calculateBedleave(event.getPlayer(), event.getFrom()))
            bedHelpers.checkPlayers(event.getFrom(), false);
    }

    /**
     * Calculate if number of sleeping players is enough to fast forward the night.
     * @param event PlayerBedEnterEvent
     */
    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if(event.isCancelled()
                || event.getPlayer().hasPermission("betterbeds.ignore")
                || !event.getPlayer().hasPermission("betterbeds.sleep")
                || bedGlobals.transitionTask != 0)
            return;

        World world = event.getBed().getWorld();
        int calculatedPlayers = 0;
        for(Player p : bbPlugin.getServer().getOnlinePlayers()) {
            if(world.equals(p.getWorld()) && p != event.getPlayer() && !p.isSleeping() && p.hasPermission("betterbeds.ghost") && !p.hasPermission("betterbeds.ghost.buster")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(bedMessages.ghostMessage.getText());
                bbPlugin.getLogger().info("There is a ghost online, players can't sleep now!");
                return;
            }
            if(!p.hasPermission("betterbeds.ignore"))
                calculatedPlayers++;
        }

        HashSet<UUID> playerList = new HashSet<>();

        if(bedGlobals.asleepPlayers.containsKey(world.getUID()))
            playerList = bedGlobals.asleepPlayers.get(world.getUID());

        playerList.add(event.getPlayer().getUniqueId());

        bedGlobals.asleepPlayers.put(world.getUID(), playerList);
        bedGlobals.nameOfLastPlayerToEnterBed.put(world.getUID(), event.getPlayer().getName());

        bbPlugin.getLogger().log(Level.INFO, event.getPlayer().getName() + " sleeps now. " + playerList.size() + "/" + calculatedPlayers + " players are asleep in world " + world.getName());

        if(!bedHelpers.checkPlayers(world, false))
            bedHelpers.notifyPlayers(world, bedMessages.sleepMessage);
    }
}