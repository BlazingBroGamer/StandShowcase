package me.BlazingBroGamer.StandShowcase;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;

public class Updater {
	
	int taskid;
	double rotation;
	double crotation;
	long speed;
	StandShowcase plugin;
	boolean started = true;
	
	public Updater(double rotation, long speed, StandShowcase plugin){
		this.rotation = rotation;
		this.speed = speed;
		this.plugin = plugin;
		crotation = 0;
	}
	
	public void startUpdater(){
		taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){

			@Override
			public void run() {
				if(!started)
					return;
				crotation += rotation/100;
				if(crotation >= 360)
					crotation -= 360;
				updateDirection();
			}
			
		}, 0, speed);
	}
	
	public void restartUpdater(){
		Bukkit.getScheduler().cancelTask(taskid);
		startUpdater();
	}
	
	public void updateDirection(){
		for(ArmorStand as : plugin.armorstands){
			if(!as.isValid()){
				
			}
			as.setHeadPose(as.getHeadPose().setY(crotation));
		}
	}
	
	public void alignDirections(){
		List<String> armors = new ArrayList<String>();
		for(ArmorStand as : plugin.armorstands){
			armors.add(plugin.ad.formatStand(as));
			as.remove();
		}
		plugin.armorstands.clear();
		int i = 1;
		for(String s : armors){
			ArmorStand as = plugin.ad.parseStand(s, i);
			plugin.armorstands.add(as);
			plugin.standid.put(as, i);
			i++;
		}
	}
	
	public void deletAll(){
		for(ArmorStand as : plugin.armorstands){
			as.remove();
		}
		plugin.armorstands.clear();
	}
	
}
