package me.BlazingBroGamer.StandShowcase;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

public class StandListener implements Listener{
	
	StandShowcase plugin;
	List<Player> delete;
	List<Player> resetcmd;
	List<Player> resetslide;
	List<Player> slidegui;
	ArmorData ad;
	HashMap<Player, ItemStack> slideadd = new HashMap<Player, ItemStack>();
	HashMap<Player, String> commandadd = new HashMap<Player, String>();
	StandGUI gui;
	
	public StandListener(StandShowcase plugin){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		delete = new ArrayList<Player>();
		resetcmd = new ArrayList<Player>();
		resetslide = new ArrayList<Player>();
		ad = plugin.ad;
		gui = plugin.gui;
		slidegui = new ArrayList<Player>();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent e){
		if(e.getRightClicked() instanceof ArmorStand){
			ArmorStand as = (ArmorStand)e.getRightClicked();
			if(plugin.armorstands.contains(as)){
				int standid = plugin.getStandID(as);
				StandType st = ad.getType(standid);
				e.setCancelled(true);
				Player p = e.getPlayer();
				if(delete.contains(p)){
					delete.remove(p);
					plugin.armorstands.remove(as);
					plugin.standid.remove(as);
					ad.slides.remove(standid);
					ad.commands.remove(standid);
					ad.standtype.remove(standid);
					as.remove();
					p.sendMessage(ChatColor.GREEN + "Successfully deleted showcasing armorstand!");
				}else if(slideadd.containsKey(p)){
					ItemStack add = slideadd.get(p);
					slideadd.remove(p);
					plugin.p.addSlide(as, add);
					p.sendMessage(ChatColor.GREEN + "Successfully added slide to the showcasing armorstand!");
					if(st == StandType.COMMAND){
						p.sendMessage(ChatColor.RED + "The armorstand has previously been a command stand!");
					}
					ad.setType(standid, StandType.SLIDES);
				}else if(commandadd.containsKey(p)){
					String cmdadd = commandadd.get(p);
					commandadd.remove(p);
					ad.addCommand(standid, cmdadd);
					p.sendMessage(ChatColor.GREEN + "Successfully added command to the showcasing armorstand!");
					if(st == StandType.SLIDES){
						p.sendMessage(ChatColor.RED + "The armorstand has previously been a slide stand!");
					}
					ad.setType(standid, StandType.COMMAND);
				}else if(resetslide.contains(p)){
					if(st != StandType.SLIDES){
						p.sendMessage(ChatColor.RED + "That is not a slide stand!");
						return;
					}
					plugin.p.setSlide(as, 0);
					ItemStack helmet = plugin.p.getSlideItem(as);
					as.setHelmet(helmet);
					ad.slides.put(standid, new ArrayList<String>());
					p.sendMessage(ChatColor.GREEN + "Successfully reset the slides of the showcasing armorstand!");
					resetslide.remove(p);
					ad.addSlideItem(helmet, standid);
				}else if(resetcmd.contains(p)){
					if(st != StandType.COMMAND){
						p.sendMessage(ChatColor.RED + "That is not a command stand!");
						return;
					}
					ad.commands.put(standid, new ArrayList<String>());
					p.sendMessage(ChatColor.GREEN + "Successfully reset the commands of the showcasing armorstand!");
					resetcmd.remove(p);
					ad.setType(standid, null);
				}else if(slidegui.contains(p)){
					p.openInventory(gui.getSlideGUI(standid));
					slidegui.remove(p);
				}else{
					if(st == StandType.SLIDES){
						int nextslide = plugin.p.getNextSlide(as);
						plugin.p.setSlide(as, nextslide);
						as.setHelmet(plugin.p.getSlideItem(as));
					}else if(st == StandType.COMMAND){
						for(String s : ad.getCommands(standid)){
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
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e){
		if(e.getInventory() != null){
			if(e.getInventory().getTitle().startsWith("§aSlide GUI: ")){
				int standid = Integer.parseInt(e.getInventory().getTitle().split(": ")[1]);
				gui.parseSlideGUI(standid, e.getInventory());
				e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully inputted information to the slide!");
			}
		}
	}
	
}
