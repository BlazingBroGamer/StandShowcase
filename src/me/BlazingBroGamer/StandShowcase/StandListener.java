package me.BlazingBroGamer.StandShowcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
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
	Sound sound;
	Effect e;
	int particledata = 0;
	double offset = 2.25;
	
	public StandListener(StandShowcase plugin){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		delete = new ArrayList<Player>();
		resetcmd = new ArrayList<Player>();
		resetslide = new ArrayList<Player>();
		ad = plugin.ad;
		gui = plugin.gui;
		slidegui = new ArrayList<Player>();
		loadParticle();
		loadSound();
	}
	
	public void loadParticle(){
		e = null;
		boolean contains = false;
		String effect = plugin.fc.getString("Particle.Type").replaceAll(" ", "_");
		if(effect.equalsIgnoreCase("nothing"))
			return;
		for(Effect e2 : Effect.values()){
			if(e2.name().equalsIgnoreCase(effect)){
				contains = true;
				this.e = e2;
			}
		}
		if(contains == false)
			plugin.debugWarning("Error loading sound: Invalid particle name!");
		else{
			plugin.debugInfo("Loaded particle: " + effect);
			particledata = plugin.fc.getInt("Particle.Data");
			offset = plugin.fc.getDouble("Particle.Offset");
		}
	}
	
	public void loadSound(){
		this.sound = null;
		String sound = plugin.fc.getString("Sounds").replaceAll(" ", "_");
		boolean contains = false;
		if(sound.equalsIgnoreCase("nothing"))
			return;
		for(Sound s : Sound.values()){
			if(s.name().equalsIgnoreCase(sound)){
				contains = true;
				this.sound = s;
			}
		}
		if(contains == false)
			plugin.debugWarning("Error loading sound: Invalid sound name!");
		else
			plugin.debugInfo("Loaded sound: " + sound);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent e){
		Player p = e.getPlayer();
		if(e.getRightClicked() instanceof ArmorStand){
			ArmorStand as = (ArmorStand)e.getRightClicked();
			if(plugin.armorstands.contains(as)){
				int standid = plugin.getStandID(as);
				StandType st = ad.getType(standid);
				e.setCancelled(true);
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
						playSound(p);
						playParticle(as);
					}else if(st == StandType.COMMAND){
						playSound(p);
						playParticle(as);
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
		if(delete.contains(p) || slidegui.contains(p) || slideadd.containsKey(p) || commandadd.containsKey(p)
				|| resetcmd.contains(p) || resetslide.contains(p)){
			delete.remove(e.getPlayer());
			e.getPlayer().sendMessage(ChatColor.RED + "That is not a showcasing armorstand!");
			return;
		}
	}
	
	public void playParticle(ArmorStand as){
		Location loc = as.getLocation();
		loc.setY(loc.getY() + offset);
		int amount = plugin.fc.getInt("Particle.Amount");
		if(e != null)
			while(amount > 0){
				as.getWorld().playEffect(loc, e, particledata);
				amount--;
			}
	}
	
	public void playSound(Player p){
		if(sound != null)
			p.playSound(p.getLocation(), sound, 1, 0);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof ArmorStand){
			if(plugin.armorstands.contains(e.getEntity()))
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e){
		for(Entity ent : e.getChunk().getEntities()){
			if(ent instanceof ArmorStand){
				ArmorStand as = (ArmorStand)ent;
				if(plugin.armorstands.contains(as)){
					plugin.despawned.put(as, plugin.getStandID((ArmorStand)ent));
					plugin.armorstands.remove(as);
				}
			}
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e){
		for(Entity ent : e.getChunk().getEntities()){
			if(ent instanceof ArmorStand){
				ArmorStand as = (ArmorStand)ent;
				UUID id = as.getUniqueId();
				for(ArmorStand pas : plugin.despawned.keySet()){
					if(pas.getUniqueId().equals(id)){
						plugin.armorstands.add(as);
						plugin.standid.put(as, plugin.despawned.get(pas));
						plugin.despawned.remove(pas);
						return;
					}
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
