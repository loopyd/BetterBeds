package com.lupinedream.betterbeds;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WorldHelpers {
    private static Plugin bbPlugin;

    public WorldHelpers(Plugin plugin) {
        bbPlugin = plugin;
    }

    /**
     * Check if a player is AFK
     * Currently this only works with the WhosAFK plugin
     * TODO: Add support for checking with more methods
     * TODO: Add a config option to decide whether AFK players should be counted
     * @param p - The player to check.
     * @return boolean - True if Player is currently AFK
     */
    public boolean isPlayerAFK(Player p) {
        ClassLoader classLoader = BetterBeds.class.getClassLoader();

        // Check if the player is AFK, according to WhosAFK
        try {
            // Load the WhosAFK class and it's playerIsAFK(Player) method
            Class<?> WhosAFK = classLoader.loadClass("whosafk.WhosAFK");
            Method whosafkPlayerIsAFK = WhosAFK.getMethod("playerIsAFK", Player.class);

            // Get the instance of WhosAFK being used by spigot
            @SuppressWarnings({ "unchecked", "rawtypes" })
            JavaPlugin whosafk = JavaPlugin.getPlugin((Class) WhosAFK);

            // Finally, check if WhosAFK thinks the player is AFK
            if (whosafk.isEnabled() && (Boolean) whosafkPlayerIsAFK.invoke(whosafk, p))
                return true;
        } catch (ClassNotFoundException e) {
            // WhosAFK is not installed, no need to panic
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Default to not AFK
        return false;
    }

    /**
     * Returns a list of players that are online in the specified world.
     * @param world - World to look for online players in.
     * @return List of players that are online.
     */
    public List<Player> getPlayers(World world) {
        List<Player> pList = new ArrayList<>();
        for(Player p : bbPlugin.getServer().getOnlinePlayers()) {
            if(world.equals(p.getWorld())) {
                pList.add(p);
            }
        }
        return pList;
    }

    /**
     * Count all the players that matter
     * @param world The world to count in
     * @return int - The count of the players
     */
    public int countQualifyingPlayers(World world) {
        int calculatedPlayers = 0;
        for(Player p : bbPlugin.getServer().getOnlinePlayers())
            if(world.equals(p.getWorld()) && !p.hasPermission("betterbeds.ignore") && p.hasPermission("betterbeds.sleep") && !isPlayerAFK(p))
                calculatedPlayers ++;
        return calculatedPlayers;
    }

    /**
     * Resets the world's climate.
     * TODO: Debug this, it may crash in the nether/the end.
     * @param world - World to set the climate of.
     */
    public void setWorldToMorning(World world)
    {
        world.setTime(23450);
        if(world.hasStorm())
            world.setStorm(false);

        if(world.isThundering())
            world.setThundering(false);

    }


}
