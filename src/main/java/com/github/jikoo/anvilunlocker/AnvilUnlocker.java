package com.github.jikoo.anvilunlocker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AnvilUnlocker extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onInventoryOpen(@NotNull InventoryOpenEvent event) {
		if (!(event.getInventory() instanceof AnvilInventory anvilInventory)) {
			return;
		}

		int maximumCost = getConfig().getInt("maximumcost");
		anvilInventory.setMaximumRepairCost(maximumCost);

		if (event.getPlayer() instanceof Player player
				&& player.getGameMode() != GameMode.CREATIVE) {
			setInstantBuild(player, true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onInventoryClose(@NotNull InventoryCloseEvent event) {
		if (event.getInventory() instanceof AnvilInventory
				&& event.getPlayer() instanceof Player player
				&& player.getGameMode() != GameMode.CREATIVE) {
			setInstantBuild(player, false);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPrepareAnvil(@NotNull PrepareAnvilEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player)
				|| player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		getServer().getScheduler().runTask(this, () -> {
			AnvilInventory anvil = event.getInventory();
			setInstantBuild(player, anvil.getRepairCost() < anvil.getMaximumRepairCost());
		});
	}

	public void setInstantBuild(@NotNull Player player, boolean instantBuild) {
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
