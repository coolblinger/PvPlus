package me.coolblinger.pvplus.components.outposts;

import me.coolblinger.pvplus.PvPlus;
import me.coolblinger.pvplus.PvPlusUtils;
import me.coolblinger.pvplus.components.outposts.doors.OutpostDoor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OutpostListeners {
	public static void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		//Doors
		if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block.getType() == Material.WOODEN_DOOR) {
				String outpost = PvPlusUtils.getOutpost(block.getLocation().toVector());
				if (outpost != null) {
					String group = PvPlus.gm.getGroup(player.getName());
					String owningGroup = PvPlus.om.getOwner(outpost);
					if (!owningGroup.equals(group)) {
						if (player.hasPermission("pvplus.outposts.manage")) {
							if (player.isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
								event.getClickedBlock().setType(Material.AIR);
								player.sendMessage(ChatColor.GREEN + "The door has been successfully removed.");
								event.setCancelled(true);
								return;
							} else {
								player.sendMessage(ChatColor.RED + "You can't open doors here if your group does not own the outpost. (sneak and right click the door to remove it)");
								event.setCancelled(true);
								return;
							}
						} else {
							player.sendMessage(ChatColor.RED + "You can't open doors here if your group does not own the outpost.");
							event.setCancelled(true);
							return;
						}
					}
				}
			}
			if (block.getType() == Material.WOODEN_DOOR || block.getType() == Material.IRON_DOOR_BLOCK) {
				String outpost = PvPlusUtils.getOutpost(block.getLocation().toVector());
				if (outpost != null) {
					String group = PvPlus.gm.getGroup(player.getName());
					String owningGroup = PvPlus.om.getOwner(outpost);
					if (owningGroup.equals(group)) {
						if (PvPlus.om.doors.containsKey(block.getLocation())) {
							if (PvPlus.om.doors.get(block.getLocation()).isSucceeded) {
								player.sendMessage(ChatColor.RED + "You can't close doors shortly after they have been breached.");
								event.setCancelled(true);
								return;
							}
						}
					}
				}
			}
		}
		//Signs
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) block.getState();
				if (sign.getLine(0).equalsIgnoreCase("[PvPlus Door]")) {
					if (!PvPlus.om.doors.containsKey(block.getLocation())) {
						PvPlus.om.doors.put(block.getLocation(), new OutpostDoor(block, player));
						event.setCancelled(true);
						return;
					}
				}
			}
		}
		//Defining
		String outpost = PvPlus.om.getDefining(player);
		if (outpost != null) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				PvPlus.om.outposts.get(outpost).x1 = event.getClickedBlock().getX();
				PvPlus.om.outposts.get(outpost).z1 = event.getClickedBlock().getZ();
				player.sendMessage(ChatColor.GREEN + "Corner one set to " + ChatColor.WHITE + event.getClickedBlock().getX() + "," + event.getClickedBlock().getZ() + ChatColor.GREEN + ".");
				event.setCancelled(true);
				return;
			} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				PvPlus.om.outposts.get(outpost).x2 = event.getClickedBlock().getX();
				PvPlus.om.outposts.get(outpost).z2 = event.getClickedBlock().getZ();
				player.sendMessage(ChatColor.GREEN + "Corner two set to " + ChatColor.WHITE + event.getClickedBlock().getX() + "," + event.getClickedBlock().getZ() + ChatColor.GREEN + ".");
				event.setCancelled(true);
				return;
			}
		}
		//Fire check
		if (!player.hasPermission("pvplus.outposts.manage")) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (PvPlusUtils.getOutpost(event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().toVector()) != null) {
					if (event.getClickedBlock().getRelative(event.getBlockFace()).getType() == Material.FIRE) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	public static void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("pvplus.outposts.manage")) {
			if (PvPlusUtils.getOutpost(event.getBlock().getLocation().toVector()) != null) {
				event.setCancelled(true);
			}
		}
	}

	public static void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("pvplus.outposts.manage")) {
			if (PvPlusUtils.getOutpost(event.getBlock().getLocation().toVector()) != null) {
				event.setCancelled(true);
			}
		}
	}

	public static void onPlayerQuit (PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (PvPlus.om.getDefining(player) != null) {
			PvPlus.om.toggleDefining(PvPlus.om.getDefining(player), player);
		}
	}
}
