package me.BlazingBroGamer.StandShowcase;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class StandListener implements Listener{
	
	StandShowcase plugin;
	List<Player> delete;
	
	public StandListener(StandShowcase plugin){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		delete = new ArrayList<Player>();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent e){
		if(e.getRightClicked() instanceof ArmorStand){
			ArmorStand as = (ArmorStand)e.getRightClicked();
			if(plugin.armorstands.contains(as)){
				e.setCancelled(true);
				if(delete.contains(e.getPlayer())){
					delete.remove(e.getPlayer());
					plugin.armorstands.remove(as);
					as.remove();
					e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully deleted showcasing armorstand!");
				}
				return;
			}
		}
		if(delete.contains(e.getPlayer())){
			delete.remove(e.getPlayer());
			e.getPlayer().sendMessage(ChatColor.RED + "That is not a showcasing armorstand!");
			return;
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof ArmorStand){
			if(plugin.armorstands.contains(e.getEntity())){
				e.setCancelled(true);
			}
		}
	}
	
}
