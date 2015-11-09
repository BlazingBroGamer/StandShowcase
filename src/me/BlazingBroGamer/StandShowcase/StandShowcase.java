package me.BlazingBroGamer.StandShowcase;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class StandShowcase extends JavaPlugin implements Listener{
    
    List<ArmorStand> armorstands = new ArrayList<ArmorStand>();
    FileConfiguration fc;
    Updater u;
    ArmorData ad;
    StandListener sd;
    
    @Override
    public void onEnable() {
        fc = getConfig();
        fc.addDefault("Rotation", 5);
        fc.addDefault("Speed", 1);
        fc.options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
        u = new Updater(fc.getDouble("Rotation"), fc.getLong("Speed"), this);
        u.startUpdater();
        ad = new ArmorData();
        for(String s : ad.getArmorStands()){
            ArmorStand as = ad.parseStand(s);
            if(as != null)
                armorstands.add(as);
        }
        ad.resetArmorData();
        saveConfig();
        sd = new StandListener(this);
    }
    
    @Override
    public void onDisable() {
        for(ArmorStand as : armorstands){
            ad.saveArmorData(as);
            as.remove();
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("sc") || label.equalsIgnoreCase("showcase")){
            if(!sender.hasPermission("standshowcase.admin")){
                sender.sendMessage("§cYou do not have permissions to use this command!");
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
                    if(args[1].equalsIgnoreCase("hand")){
                        ItemStack is = ((Player)sender).getItemInHand();
                        if(is == null)
                            is = new ItemStack(Material.AIR);
                        Material m = is.getType();
                        if(!m.isBlock() && !m.name().contains("HELMET") && m != Material.SKULL_ITEM){
                            sender.sendMessage(ChatColor.RED + "The item must be a block or helmet or skull!");
                            return true;
                        }
                        armorstands.add(new StandGenerator(loc, ((Player)sender).getItemInHand(), name).getStand());
                    }else{
                        String[] itemdata = args[1].split(":");
                        Material m = Material.matchMaterial(itemdata[0]);
                        if(m == null){
                            sender.sendMessage(ChatColor.RED + "Invalid item name!");
                            return true;
                        }
                        if(!m.isBlock() && !m.name().contains("HELMET")){
                            sender.sendMessage(ChatColor.RED + "The item must be a block or helmet!");
                            return true;
                        }
                        short data = 0;
                        if(itemdata.length == 2){
                            data = Short.parseShort(itemdata[1]);
                        }
                        armorstands.add(new StandGenerator(loc, m, data, name).getStand());
                    }
                    return true;
                }
            }else if(args.length == 1){
                if(args[0].equalsIgnoreCase("delete")){
                    sender.sendMessage(ChatColor.GREEN + "Right click the armor stand you want to delete!");
                    sd.delete.add((Player)sender);
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
                }
            }
            sender.sendMessage("§6========§0[§aStand Showcase§0]§6========");
            sender.sendMessage("§0§l/§asc §ccreate §0[§cItemName§6:§cData§0] §0[§cName§0]");
            sender.sendMessage("§0§l/§asc §cdelete");
            sender.sendMessage("§0§l/§asc §cdeleteall");
            sender.sendMessage("§0§l/§asc §calign");
            sender.sendMessage("§0§l/§asc §cspeed §0[§cSpeed§0]");
            sender.sendMessage("§0§l/§asc §crotation §0[§cRotation§0]");
        }
        return false;
    }
    
}
