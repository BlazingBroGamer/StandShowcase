package me.BlazingBroGamer.StandShowcase;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StandGUI {
	
	ArmorData ad;
	StandShowcase plugin;
	
	public StandGUI(StandShowcase plugin){
		this.plugin = plugin;
		ad = plugin.ad;
	}
	
	public Inventory getSlideGUI(int standid){
		Inventory inv = Bukkit.createInventory(null, 54, "§aSlide GUI: " + standid);
		int i = 0;
		for(String s : ad.getSlides(standid)){
			inv.setItem(i, ad.parseItem(s));
			i++;
		}
		return inv;
	}
	
	public void parseSlideGUI(int standid, Inventory inv){
		List<String> slides = new ArrayList<String>();
		ad.slides.remove(standid);
		for(ItemStack is : inv.getContents()){
			if(is != null && is.getType() != Material.AIR){
				slides.add(ad.formatItem(is));
			}
		}
		if(slides.isEmpty()){
			slides.add("AIR:0");
		}
		ad.slides.put(standid, slides);
		if(slides.size() > 1)
			ad.standtype.put(standid, StandType.SLIDES);
	}
	
	public int round(int i){
		if(i % 9 == 0)
			return i;
		else
			return i + 9 - (i % 9);
	}
	
}
