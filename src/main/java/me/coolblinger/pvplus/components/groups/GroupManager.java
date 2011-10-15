package me.coolblinger.pvplus.components.groups;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class GroupManager {
	private HashMap<String, Group> groups = new HashMap<String, Group>();

	public GroupManager() {
		load();
	}

	public int join(String group, String player) {
		if (getGroup(player) == null) {
			if (groups.containsKey(group)) {
				groups.get(group).players.add(player);
				return 2;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	public Set<String> list() {
		return groups.keySet();
	}

	public boolean leave(String player) {
		String group = getGroup(player);
		if (group != null) {
			groups.get(group).players.remove(player);
			return true;
		} else {
			return false;
		}
	}

	public String getGroup(String player) {
		Collection<Group> groupCollection = groups.values();
		for (Group group : groupCollection) {
			if (group.players.contains(player)) {
				return group.name;
			}
		}
		return null;
	}

	public boolean createGroup(String name) {
		if (!groups.containsKey(name)) {
			groups.put(name, new Group());
			groups.get(name).name = name;
			return true;
		} else {
			return false;
		}
	}

	public boolean remove(String group) {
		if (groups.containsKey(group)) {
			groups.remove(group);
			return true;
		} else {
			return false;
		}
	}

	public boolean setOwner(String group, String owner) {
		if (groups.containsKey(group)) {
			groups.get(group).owner = owner;
			if (!groups.get(group).players.contains(owner)) {
				groups.get(group).players.add(owner);
			}
			return true;
		} else {
			return false;
		}
	}

	public synchronized void load() {
		File file = new File("plugins" + File.separator + "PvPlus" + File.separator + "groups.yml");
		if (!file.exists()) {
			return;
		}
		groups = new HashMap<String, Group>();
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		Set<String> keys = config.getKeys(false);
		for (String key : keys) {
			if (config.get(key + ".players") != null && config.get(key + ".owner") != null) {
				groups.put(key, new Group());
				groups.get(key).name = key;
				groups.get(key).players = config.getList(key + ".players", new ArrayList<String>());
				groups.get(key).owner = config.getString(key + ".owner");
			}
		}
	}

	public synchronized void save() {
		File file = new File("plugins" + File.separator + "PvPlus" + File.separator + "groups.yml");
		file.getParentFile().mkdir();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		YamlConfiguration config = new YamlConfiguration();
		Set<String> keys = groups.keySet();
		for (String key : keys) {
			config.set(key + ".players", groups.get(key).players);
			config.set(key + ".owner", groups.get(key).owner);
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
