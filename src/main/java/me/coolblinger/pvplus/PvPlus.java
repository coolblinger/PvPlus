package me.coolblinger.pvplus;

import me.coolblinger.pvplus.commands.GroupsCommand;
import me.coolblinger.pvplus.commands.OutpostsCommand;
import me.coolblinger.pvplus.commands.PvPlusCommand;
import me.coolblinger.pvplus.components.groups.GroupManager;
import me.coolblinger.pvplus.components.outposts.OutpostManager;
import me.coolblinger.pvplus.listeners.PvPlusEntityListener;
import me.coolblinger.pvplus.listeners.PvPlusPlayerListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Handler;

public class PvPlus extends JavaPlugin {
	public static GroupManager gm = new GroupManager();
	public static OutpostManager om = new OutpostManager();

	//TODO: Debug
	private Handler handler = new PvPlusHandler();

	public void onDisable() {
		gm.save();
		om.save();
		PvPlusUtils.log.info("PvPlus has been disabled!");

		//TODO: Debug
		getServer().getLogger().removeHandler(handler);
	}

	public void onEnable() {
		//TODO: Debug
		getServer().getLogger().addHandler(handler);

		PluginDescriptionFile pdf = getDescription();
		PluginManager pm = getServer().getPluginManager();
		getCommand("pvplus").setExecutor(new PvPlusCommand());
		getCommand("groups").setExecutor(new GroupsCommand());
		getCommand("outposts").setExecutor(new OutpostsCommand());
		pm.registerEvent(Event.Type.PLAYER_CHAT, new PvPlusPlayerListener(), Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, new PvPlusPlayerListener(), Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, new PvPlusEntityListener(), Event.Priority.Normal, this);
		PvPlusUtils.log.info("PvPlus version " + pdf.getVersion() + " has been enabled!");
	}
}
