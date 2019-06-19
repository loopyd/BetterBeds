package com.lupinedream.betterbeds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterBeds extends JavaPlugin {
	private static Plugin bbPlugin;
	private static BedGlobals bedGlobals;
	private static BedMessages bedMessages;
	private static WorldHelpers worldHelpers;
	private static BetterBedHelpers bedHelpers;
	private static BetterBedsListeners bedsListeners;

	/**
	 * helper which returns the instance of this plugin (after loading it)
	 * @return the instance of this plugin
	 */
	public static Plugin GetPlugin() { return bbPlugin; }

	public void onEnable() {
		bbPlugin = this;
		this.saveDefaultConfig();
		this.loadConfig();
		worldHelpers = new WorldHelpers(bbPlugin);
		bedHelpers = new BetterBedHelpers(bbPlugin, bedGlobals, bedMessages, worldHelpers);
		bedsListeners = new BetterBedsListeners(bbPlugin, bedGlobals, bedMessages, worldHelpers, bedHelpers);
		bbPlugin.getServer().getPluginManager().registerEvents(bedsListeners, bbPlugin);
	}

	/**
	 * Reload command's method
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws NumberFormatException {
		if(cmd.getName().equalsIgnoreCase("betterbedsreload") || cmd.getName().equalsIgnoreCase("bbreload")) {
			sender.sendMessage("[BetterBeds] Reloading Config");
			this.loadConfig();
			return true;
		}
		return false;		
	}
	
	/**
	 * Loads the options from the config file into the plugins variables
	 */
	private void loadConfig() {
		this.reloadConfig();
		bedGlobals = new BedGlobals(bbPlugin);
		bedMessages = new BedMessages(bbPlugin);
		bedGlobals.load();
		bedMessages.load();
	}

}
