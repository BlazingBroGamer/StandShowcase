package me.BlazingBroGamer.StandShowcase;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class StandGenerator {
	
	ArmorStand as;
	
	public StandGenerator(Location loc, ItemStack is, String name){
		loc.getChunk().load();
		Entity e = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		as = (ArmorStand)e;
		as.setVisible(false);
		as.setGravity(false);
		as.setCustomNameVisible(true);
		as.setCustomName(name);
		if(name.equalsIgnoreCase("nothing")){
			as.setCustomNameVisible(false);
		}
		as.setHelmet(is);
	}
	
	StandShowcase plugin = StandShowcase.ss;
	
	public ArmorStand getStand(){
		return as;
	}

}
