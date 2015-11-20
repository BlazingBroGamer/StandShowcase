package me.BlazingBroGamer.StandShowcase;

import java.util.HashMap;

import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class Presenter {
	
	StandShowcase plugin;
	HashMap<ArmorStand, Integer> cstandslide = new HashMap<ArmorStand, Integer>();
	
	public Presenter(StandShowcase plugin){
		this.plugin = plugin;
	}
	
	public void addSlide(ArmorStand as, ItemStack is){
		plugin.ad.addSlideItem(is, plugin.standid.get(as));
	}
	
	public int getNextSlide(ArmorStand as){
		if(cstandslide.get(as) == null){
			cstandslide.put(as, 0);
		}
		int next = cstandslide.get(as) + 1;
		if(next >= plugin.ad.getSlides(plugin.getStandID(as)).size()){
			return 0;
		}else{
			return next;
		}
	}
	
	public void setSlide(ArmorStand as, int i){
		cstandslide.put(as, i);
	}
	
	public ItemStack getSlideItem(ArmorStand as){
		return plugin.ad.getSlideItem(cstandslide.get(as), plugin.standid.get(as));
	}
	
}
