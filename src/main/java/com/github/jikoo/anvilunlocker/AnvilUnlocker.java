package com.github.jikoo.anvilunlocker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilUnlocker extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory() instanceof AnvilInventory anvilInventory
				&& event.getPlayer() instanceof Player player
				&& event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			int maximumCost = getConfig().getInt("maximumcost");
			anvilInventory.setMaximumRepairCost(maximumCost);
			setInstantBuild(player, true);
		}
	}

	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory() instanceof AnvilInventory
				&& event.getPlayer() instanceof Player player
				&& event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			setInstantBuild(player, false);
		}
	}

	public void setInstantBuild(Player player, boolean instantBuild) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.ABILITIES);
		packet.getBooleans().write(0, player.isInvulnerable());
		packet.getBooleans().write(1, player.isFlying());
		packet.getBooleans().write(2, player.getAllowFlight());
		packet.getBooleans().write(3, instantBuild);
		packet.getFloat().write(0, player.getFlySpeed() / 2);
		packet.getFloat().write(1, player.getWalkSpeed() / 2);

		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
