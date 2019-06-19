package com.lupinedream.betterbeds;
import org.bukkit.plugin.Plugin;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class BedGlobals implements ConfigInterface {
    private Plugin bbPlugin;
    public int minPlayers;
    public double sleepPercentage;
    public int nightSpeed;
    public int transitionTask;
    public HashMap<UUID, HashSet<UUID>> asleepPlayers;
    public HashMap<UUID,String> nameOfLastPlayerToEnterBed;

    public BedGlobals(Plugin plugin) {
        bbPlugin = plugin;
        this.setDefaults();
    }

    public final boolean load() {
        minPlayers = bbPlugin.getConfig().getInt("minPlayers");
        try {
            sleepPercentage = Double.parseDouble(Objects.requireNonNull(bbPlugin.getConfig().getString("sleepPercentage")).replaceAll(" ", "").replace("%", ""));
        } catch (NumberFormatException e) {
            bbPlugin.getLogger().log(Level.WARNING,"You have an Error in your config at the sleepPercentage-node! Using the default now: " + sleepPercentage);
            return false;
        }
        if (sleepPercentage > 1) {
            sleepPercentage = sleepPercentage / 100;
        }
        nightSpeed = bbPlugin.getConfig().getInt("nightSpeed");
        return true;
    }

    public final boolean save() {
        throw new NotImplementedException();
    }

    public final void setDefaults() {
        minPlayers = 2;
        sleepPercentage = 0.5;
        nightSpeed = 0;
        transitionTask = 0;
        asleepPlayers = new HashMap<>();
        nameOfLastPlayerToEnterBed = new HashMap<>();
    }
}
