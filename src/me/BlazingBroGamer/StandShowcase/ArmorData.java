package me.BlazingBroGamer.StandShowcase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ArmorData {
	
	File f;
	FileConfiguration fc;
	HashMap<Integer, List<String>> slides = new HashMap<Integer, List<String>>();
	HashMap<Integer, List<String>> commands = new HashMap<Integer, List<String>>();
	HashMap<Integer, StandType> standtype = new HashMap<Integer, StandType>();
	
	public ArmorData() {
		f = new File("data-storage/StandShowcase/ArmorData.yml");
		fc = YamlConfiguration.loadConfiguration(f);
	}
	
	public void saveConfig(){
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveArmorData(ArmorStand as, int id){
		Location loc = as.getLocation();
		as.remove();
		String section = "ArmorStands." + (id + 1);
		fc.set(section + ".X", loc.getX());
		fc.set(section + ".Y", loc.getY());
		fc.set(section + ".Z", loc.getZ());
		fc.set(section + ".World", loc.getWorld().getName());
		String itemdata = slides.get(id + 1).get(0);
		fc.set(section + ".ItemData", itemdata);
		fc.set(section + ".Name", as.getCustomName());
		List<String> saved = slides.get(id + 1);
		saved.remove(0);
		fc.set(section + ".Slides", saved);
		fc.set(section + ".Commands", commands.get(id + 1));
		StandType st = standtype.get(id + 1);
		if(st != null)
			fc.set(section + ".Type", st.name());
		saveConfig();
	}
	
	public void error(){
		
	}
	
	public String formatItem(ItemStack is){
		String data = is.getType().name() + ":" + is.getDurability();
		if(is.getType() == Material.SKULL_ITEM){
			SkullMeta sm = (SkullMeta)is.getItemMeta();
			data += ":" + sm.getOwner();
		}
		return data;
	}
	
	public List<String> getSlides(int id){
		return slides.get(id);
	}
	
	public List<ItemStack> getItemSlides(int id){
		List<String> slides = getSlides(id);
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(String s : slides){
			items.add(parseItem(s));
		}
		return items;
	}
	
	public void setType(int id, StandType type){
		standtype.put(id, type);
	}
	
	public StandType getType(int id){
		return standtype.get(id);
	}
	
	public List<String> getCommands(int id){
		return commands.get(id);
	}
	
	public void addCommand(int id, String cmd){
		List<String> cmds = getCommands(id);
		if(cmds == null)
			cmds = new ArrayList<String>();
		cmds.add(cmd);
		commands.put(id, cmds);
	}
	
	public ItemStack parseItem(String s){
		String[] itemdata = s.split(":");
		ItemStack is = new ItemStack(Material.matchMaterial(itemdata[0]));
		is.setDurability(Short.parseShort(itemdata[1]));
		if(is.getType() == Material.SKULL_ITEM){
			if(itemdata.length != 2){
				SkullMeta sm = (SkullMeta)is.getItemMeta();
				sm.setOwner(itemdata[2]);
				is.setItemMeta(sm);
			}
		}
		return is;
	}
	
	public void addSlideItem(ItemStack is, int id){
		List<String> slides = getSlides(id);
		if(slides == null)
			slides = new ArrayList<String>();
		slides.add(formatItem(is));
		this.slides.put(id, slides);
	}
	
	public ItemStack getSlideItem(int slide, int id){
		return parseItem(slides.get(id).get(slide));
	}
	
	public void resetArmorData(){
		fc.set("ArmorStands", null);
		saveConfig();
	}
	
	public String formatStand(ArmorStand as){
		Location loc = as.getLocation();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		String world = loc.getWorld().getName();
		return x + "," + y + "," + z + "," + world + "=" + as.getCustomName();
	}
	
	public ArmorStand parseStand(String s, int id){
		String[] data = s.split("=");
		String[] locdata = data[0].split(",");
		Location loc = new Location(Bukkit.getWorld(locdata[3]), 
				Double.parseDouble(locdata[0]), Double.parseDouble(locdata[1]), Double.parseDouble(locdata[2]));
		return new StandGenerator(loc, parseItem(slides.get(id).get(0))
				, data[1]).getStand();
	}
	
	public ArmorStand parseStand(int id){
		String section = "ArmorStands." + id;
		double x = fc.getDouble(section + ".X");
		double y = fc.getDouble(section + ".Y");
		double z = fc.getDouble(section + ".Z");
		String world = fc.getString(section + ".World");
		if(world == null){
			return null;
		}
		Location loc = new Location(Bukkit.getWorld(world), x, y, z);
		String name = fc.getString(section + ".Name");
		StandType type = StandType.matchType(fc.getString(section + ".Type"));
		ItemStack is = parseItem(fc.getString(section + ".ItemData"));
		addSlideItem(is, id);
		if(type == StandType.COMMAND){
			commands.put(id, fc.getStringList(section + ".Commands"));
		}else if(type == StandType.SLIDES){
			for(String s : fc.getStringList("ArmorStands." + id + ".Slides")){
				addSlideItem(parseItem(s), id);
			}
		}
		setType(id, type);
		return new StandGenerator(loc, is
				, name).getStand();
	}
	
	public List<String> getArmorStands(){
		List<String> armorstands = new ArrayList<String>();
		ConfigurationSection configsection = fc.getConfigurationSection("ArmorStands");
		if(configsection == null)
			return null;
		for(String s : configsection.getKeys(false)){
			armorstands.add(s);
		}
		return armorstands;
	}
	
}