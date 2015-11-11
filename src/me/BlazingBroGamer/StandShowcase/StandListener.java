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
import org.bukkit.inventory.ItemStack;

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
					plugin.standid.remove(as);
					as.remove();
					e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully deleted showcasing armorstand!");
				}else if(plugin.slideadd.containsKey(e.getPlayer())){
					ItemStack add = plugin.slideadd.get(e.getPlayer());
					plugin.slideadd.remove(e.getPlayer());
					plugin.p.addSlide(as, add);
					e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully added slide to the showcasing armorstand!");
				}else{
					int nextslide = plugin.p.getNextSlide(as);
					plugin.p.setSlide(as, nextslide);
					as.setHelmet(plugin.p.getSlideItem(as));
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
