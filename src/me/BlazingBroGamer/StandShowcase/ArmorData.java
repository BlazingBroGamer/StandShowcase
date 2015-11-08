package me.BlazingBroGamer.StandShowcase;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;

public class ArmorData {
	
	File f;
	FileConfiguration fc;
	
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
	
	public void saveArmorData(ArmorStand as){
		List<String> stands = getArmorStands();
		stands.add(formatStand(as));
		fc.set("ArmorStands", stands);
		saveConfig();
	}
	
	public void resetArmorData(){
		fc.set("ArmorStands", null);
		saveConfig();
	}
	
	public String formatStand(ArmorStand as){
		Location loc = as.getLocation();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		String world = loc.getWorld().getName();
		String itemdata = as.getHelmet().getType().name() + ":" + as.getHelmet().getDurability();
		return x + "," + y + "," + z + "," + world + "=" + itemdata + "=" + as.getCustomName();
	}
	
	public ArmorStand parseStand(String s){
		String[] data = s.split("=");
		String[] locdata = data[0].split(",");
		String[] itemdata = data[1].split(":");
		Location loc = new Location(Bukkit.getWorld(locdata[3]), 
				Double.parseDouble(locdata[0]), Double.parseDouble(locdata[1]), Double.parseDouble(locdata[2]));
		return new StandGenerator(loc, Material.matchMaterial(itemdata[0]), Short.parseShort(itemdata[1])
				, data[2]).getStand();
	}
	
	public List<String> getArmorStands(){
		return fc.getStringList("ArmorStands");
	}
	
}
