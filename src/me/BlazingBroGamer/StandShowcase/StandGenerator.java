package me.BlazingBroGamer.StandShowcase;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class StandGenerator {
	
	ArmorStand as;
	
	public StandGenerator(Location loc, Material m, short durability, String name){
		as = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.setGravity(false);
		as.setCustomNameVisible(true);
		as.setCustomName(name);
		if(name.equalsIgnoreCase("nothing")){
			as.setCustomNameVisible(false);
		}
		ItemStack is = new ItemStack(m);
		is.setDurability(durability);
		as.setHelmet(is);
	}
	
	public ArmorStand getStand(){
		return as;
	}

}
