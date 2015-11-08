package me.BlazingBroGamer.StandShowcase.event.invenotry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.BlazingBroGamer.StandShowcase.StandShowcase;

public class InvenotryClick implements Listener
{

	private StandShowcase plugin;

	public InvenotryClick(StandShowcase pl)
	{
		plugin = pl;
	}

	@EventHandler
	public void onInvenotryClick(InventoryClickEvent event)
	{
		Inventory inv = event.getInventory();
		if (!inv.getTitle().equals("Custom Invenotry Test"))
			return;
		if (!(event.getWhoClicked() instanceof Player))
			return;

		int i = 1;
		Double currentrotation = plugin.getConfig().getDouble("Rotation");
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if (item.getType() == Material.COMPASS)
		{
			player.performCommand("sc rotation " + currentrotation + ++i);
			// player.teleport(player.getWorld().getSpawnLocation());
			player.sendMessage("Opened an inventory and clicked the compass");
			// player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 1);
		}
		event.setCancelled(true);
		player.closeInventory();
	}
}