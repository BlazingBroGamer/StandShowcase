package me.BlazingBroGamer.StandShowcase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
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
				int standid = plugin.getStandID(as);
				StandType st = plugin.ad.getType(standid);
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
					if(st == StandType.COMMAND){
						e.getPlayer().sendMessage(ChatColor.RED + "The armorstand has previously been a command stand!");
					}
					plugin.ad.setType(standid, StandType.SLIDES);
				}else if(plugin.commandadd.containsKey(e.getPlayer())){
					String cmdadd = plugin.commandadd.get(e.getPlayer());
					plugin.commandadd.remove(e.getPlayer());
					plugin.ad.addCommand(standid, cmdadd);
					e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully added command to the showcasing armorstand!"
							);
					if(st == StandType.SLIDES){
						e.getPlayer().sendMessage(ChatColor.RED + "The armorstand has previously been a slide stand!");
					}
					plugin.ad.setType(standid, StandType.COMMAND);
				}else{
					if(st == StandType.SLIDES){
						int nextslide = plugin.p.getNextSlide(as);
						plugin.p.setSlide(as, nextslide);
						as.setHelmet(plugin.p.getSlideItem(as));
					}else if(st == StandType.COMMAND){
						for(String s : plugin.ad.getCommands(standid)){
							if(s.startsWith("console")){
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.split(" ", 2)[1]
										.replaceAll("%player%", e.getPlayer().getName()));
							}else if(s.startsWith("player")){
								e.getPlayer().chat("/" + s.split(" ", 2)[1]);
							}
						}
					}
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
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e){
		for(Entity ent : e.getChunk().getEntities()){
			if(ent instanceof ArmorStand){
				if(plugin.armorstands.contains(ent)){
					plugin.despawned.put(ent.getUniqueId(), plugin.getStandID((ArmorStand)ent));
					plugin.armorstands.remove((ArmorStand)ent);
					plugin.standid.remove((ArmorStand)ent);
				}
			}
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e){
		for(Entity ent : e.getChunk().getEntities()){
			if(ent instanceof ArmorStand){
				UUID id = ent.getUniqueId();
				if(plugin.despawned.keySet().contains(id)){
					plugin.armorstands.add((ArmorStand)ent);
					plugin.standid.put((ArmorStand)ent, plugin.despawned.get(id));
					plugin.despawned.remove(id);
				}
			}
		}
	}
	
}
