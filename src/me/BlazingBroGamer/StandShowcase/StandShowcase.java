package me.BlazingBroGamer.StandShowcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class StandShowcase extends JavaPlugin {
	
	List<ArmorStand> armorstands = new ArrayList<ArmorStand>();
	HashMap<ArmorStand, Integer> standid = new HashMap<ArmorStand, Integer>();
	HashMap<UUID, Integer> despawned = new HashMap<UUID, Integer>();
	FileConfiguration fc;
	Updater u;
	ArmorData ad;
	StandListener sl;
	Presenter p;
	StandGUI gui;
	
	@SuppressWarnings("unused")
	@Override
	public void onEnable() {
		fc = getConfig();
		fc.addDefault("Rotation", 5);
		fc.addDefault("Speed", 1);
		fc.options().copyDefaults(true);
		saveConfig();
		u = new Updater(fc.getDouble("Rotation"), fc.getLong("Speed"), this);
		u.startUpdater();
		ad = new ArmorData();
		List<String> armorstand = ad.getArmorStands();
		if(armorstand != null){
			int i = 1;
			for(String s : armorstand){
				ArmorStand as = ad.parseStand(i);
				if(as != null){
					armorstands.add(as);
					standid.put(as, i);
				}
				i++;
			}
		}
		saveConfig();
		gui = new StandGUI(this);
		sl = new StandListener(this);
		p = new Presenter(this);
	}
	
	@Override
	public void onDisable() {
		ad.resetArmorData();
		int i = 0;
		for(ArmorStand as : armorstands){
			ad.saveArmorData(as, i);
			as.remove();
			i++;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("sc") || label.equalsIgnoreCase("showcase")){
			if(!sender.hasPermission("standshowcase.admin")){
				sender.sendMessage("§cYou do not have permissions to use this command!");
				return false;
			}
			if(!(sender instanceof Player)){
				sender.sendMessage("§cYou need to be a player to use this command!");
				return false;
			}
			if(args.length >= 3){
				if(args[0].equalsIgnoreCase("create")){
					Player p = (Player)sender;
					String name = "";
					int i = 0;
					Location loc = p.getLocation();
					loc.setY(loc.getY() - 1);
					for(String s : args){
						if(i > 1){
							name += s + " ";
						}
						i++;
					}
					name = name.substring(0, name.length() - 1);
					name = ChatColor.translateAlternateColorCodes('&', name);
					ItemStack is = getItem(args[1], (Player)sender);
					if(is == null)
						return true;
					ArmorStand as = new StandGenerator(loc, is, name).getStand();
					as.setRemoveWhenFarAway(false);
					armorstands.add(as);
					standid.put(as, armorstands.size());
					ad.addSlideItem(is, armorstands.size());
					sender.sendMessage(ChatColor.GREEN + "Successfully created a stand showcase!");
					return true;
				}else if(args[0].equalsIgnoreCase("addcommand")){
					String cmdsender = args[1];
					if(!cmdsender.equalsIgnoreCase("player") && !cmdsender.equalsIgnoreCase("console")){
						cmdsender = "console";
					}
					String addcmd = cmdsender.toLowerCase() + " ";
					int i = 0;
					for(String s : args){
						if(i >= 2){
							addcmd += s + " ";
						}
						i++;
					}
					addcmd = addcmd.substring(0, addcmd.length());
					sl.commandadd.put((Player)sender, addcmd);
					sender.sendMessage(ChatColor.GREEN + "Right click the stand you want to add the command to!");
					return true;
				}
			}else if(args.length == 1){
				if(args[0].equalsIgnoreCase("delete")){
					sender.sendMessage(ChatColor.GREEN + "Right click the armor stand you want to delete!");
					sl.delete.add((Player)sender);
					return true;
				}else if(args[0].equalsIgnoreCase("reload")){
					reloadConfig();
					fc = getConfig();
					u.rotation = fc.getDouble("Rotation");
					u.speed = fc.getLong("Speed");
					u.restartUpdater();
					sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the configuration!");
					return true;
				}else if(args[0].equalsIgnoreCase("align")){
					u.alignDirections();
					sender.sendMessage(ChatColor.GREEN + "Successfully aligned stand directions!");
					return true;
				}else if(args[0].equalsIgnoreCase("deleteall")){
					u.deletAll();
					sender.sendMessage(ChatColor.GREEN + "Successfully deleted all armor stands!");
					return true;
				}else if(args[0].equalsIgnoreCase("stop")){
					u.started = false;
					sender.sendMessage(ChatColor.GREEN + "Successfully stopped all showcase movements!");
					return true;
				}else if(args[0].equalsIgnoreCase("resume")){
					u.started = true;
					sender.sendMessage(ChatColor.GREEN + "Successfully resumed all showcase movements!");
					return true;
				}else if(args[0].equalsIgnoreCase("resetcommands")){
					sender.sendMessage(ChatColor.GREEN + "Right click the stand you want to reset the slide on!");
					sl.resetcmd.add((Player)sender);
					return true;
				}else if(args[0].equalsIgnoreCase("resetslides")){
					sender.sendMessage(ChatColor.GREEN + "Right click the stand you want to reset the commands on!");
					sl.resetslide.add((Player)sender);
					return true;
				}else if(args[0].equalsIgnoreCase("slidegui")){
					sender.sendMessage(ChatColor.GREEN + "Right click the stand you want to open the slide gui on!");
					sl.slidegui.add((Player)sender);
					return true;
				}
			}else if(args.length == 2){
				if(args[0].equalsIgnoreCase("speed")){
					int i = Integer.parseInt(args[1]);
					u.speed = i;
					u.restartUpdater();
					fc.set("Speed", i);
					saveConfig();
					sender.sendMessage(ChatColor.GREEN + "Successfully set the speed to " + i + "!");
					return true;
				}else if(args[0].equalsIgnoreCase("rotation")){
					double d = Double.parseDouble(args[1]);
					u.rotation = d;
					u.restartUpdater();
					fc.set("Rotation", d);
					saveConfig();
					sender.sendMessage(ChatColor.GREEN + "Successfully set the rotation to " + d + "!");
					return true;
				}else if(args[0].equalsIgnoreCase("addslide")){
					ItemStack is = getItem(args[1], (Player)sender);
					sl.slideadd.put((Player)sender, is);
					sender.sendMessage(ChatColor.GREEN + "Right click the stand you want to add the slide to!");
					return true;
				}
			}
			sender.sendMessage("§6========§0[§aStand Showcase§0]§6========");
			sender.sendMessage("§0§l/§asc §ccreate §0[§cItemName§6:§cData§0] §0[§cName§0]");
			sender.sendMessage("§0§l/§asc §cdelete");
			sender.sendMessage("§0§l/§asc §cdeleteall");
			sender.sendMessage("§0§l/§asc §calign");
			sender.sendMessage("§0§l/§asc §cspeed §0[§cSpeed§0]");
			sender.sendMessage("§0§l/§asc §crotation §0[§cRotation§0]");
			sender.sendMessage("§0§l/§asc §caddslide §0[§cItemName§6:§cData§6/§chand§0]");
			sender.sendMessage("§0§l/§asc §caddcommand §0[§cPlayer§6/§cConsole0] §0[§cCommand§0]");
			sender.sendMessage("§0§l/§asc §cresetslides");
			sender.sendMessage("§0§l/§asc §cresetcommands");
		}
		return false;
	}
	
	public int getStandID(ArmorStand as){
		return standid.get(as);
	}
	
	public ItemStack getItem(String arg, Player p){
		if(arg.equalsIgnoreCase("hand")){
			ItemStack is = p.getItemInHand();
			if(is == null)
				is = new ItemStack(Material.AIR);
			Material m = is.getType();
			if(!m.isBlock() && !m.name().contains("HELMET") && m != Material.SKULL_ITEM){
				p.sendMessage(ChatColor.RED + "The item must be a block or helmet or skull!");
				return null;
			}
			return is;
		}else{
			String[] itemdata = arg.split(":");
			Material m = Material.matchMaterial(itemdata[0]);
			if(m == null){
				p.sendMessage(ChatColor.RED + "Invalid item name!");
				return null;
			}
			if(!m.isBlock() && !m.name().contains("HELMET")){
				p.sendMessage(ChatColor.RED + "The item must be a block or helmet!");
				return null;
			}
			short data = 0;
			if(itemdata.length == 2){
				data = Short.parseShort(itemdata[1]);
			}
			ItemStack is = new ItemStack(m);
			is.setDurability(data);
			return is;
		}
	}
	
}
