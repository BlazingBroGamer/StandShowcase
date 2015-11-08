package me.BlazingBroGamer.StandShowcase;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;

public class Updater {
	
	int taskid;
	double rotation;
	double crotation;
	long speed;
	StandShowcase plugin;
	
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
			as.setHeadPose(as.getHeadPose().setY(crotation));
		}
	}
	
}
