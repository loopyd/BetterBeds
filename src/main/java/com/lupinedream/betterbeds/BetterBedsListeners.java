package com.lupinedream.betterbeds;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
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
     * BetterBeds event - fired when actually in bed.
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
            if(!p.hasPermission("betterbeds.ignore"))
                calculatedPlayers++;
        }

        HashSet<UUID> playerList = new HashSet<>();

        if(bedGlobals.asleepPlayers.containsKey(world.getUID()))
            playerList = bedGlobals.asleepPlayers.get(world.getUID());

        playerList.add(event.getPlayer().getUniqueId());

        bedGlobals.asleepPlayers.put(world.getUID(), playerList);
        bedGlobals.lastPlayerToEnterBed = event.getPlayer().getUniqueId();

        bbPlugin.getLogger().log(Level.INFO, event.getPlayer().getName() + " sleeps now. " + playerList.size() + "/" + calculatedPlayers + " players are asleep in world " + world.getName());

        if(!bedHelpers.checkPlayers(world, false))
            bedHelpers.notifyPlayers(world, bedMessages.sleepMessage);
    }

    /**
     * Custom Bed handler - overrides vanilla bed.  New paper-api - No more bed packets !
     * @param event
     */
    @EventHandler
    public void onPlayerCustomBedEnter (PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().hasPermission("betterbeds.ignore") ) {

            String blockMaterial = event.getClickedBlock().getBlockData().getAsString(true);
            Player playerWhoSlept = event.getPlayer();
            Location blockLocation = event.getClickedBlock().getLocation();

            if (blockMaterial.toLowerCase().contains("_bed")) {

                /* First deal with the conditions in which there are mobs nearby */
                if (!blockLocation.getNearbyEntitiesByType(Monster.class, 10D, 4D).isEmpty() &&
                        !blockLocation.getNearbyEntitiesByType(Mob.class, 10D, 4D).isEmpty()) {
                    if (!event.getPlayer().hasPermission("betterbeds.mobs")) {
                        event.setCancelled(true);
                        bbPlugin.getLogger().log(Level.INFO, playerWhoSlept.getName() + "was denied entry to bed at ( " + blockLocation.getX() + " " + blockLocation.getY() + " " + blockLocation.getZ() + ")");
                        bedHelpers.notifyPlayers(playerWhoSlept.getWorld(), bedMessages.mobMessage, playerWhoSlept.getUniqueId());
                        return;
                    } else {
                        event.setCancelled(true);
                        bbPlugin.getLogger().log(Level.INFO, playerWhoSlept.getName() + "was allowed entry to bed at ( " + blockLocation.getX() + " " + blockLocation.getY() + " " + blockLocation.getZ() + " ) via betterbeds.mobs permission.");
                        playerWhoSlept.sleep(blockLocation, true);
                        return;
                    }
                }
                /* Now we are going to deal with the normal conditions */
                else {
                    long dayTicks = playerWhoSlept.getWorld().getTime();
                    if ( dayTicks >= bedGlobals.nightTicks &&
                            dayTicks - bedGlobals.nightTicks >= bedGlobals.dayTicks ) {
                        event.setCancelled(true);
                        playerWhoSlept.sleep(blockLocation, true);
                    } else {
                        event.setCancelled(true);
                        bedHelpers.notifyPlayers(playerWhoSlept.getWorld(), bedMessages.noSleepMessage, playerWhoSlept.getUniqueId());
                    }
                }
            }
        }
    }
}
