package online.smyhw.EschatologicalUnit.SpawnMobs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class smyhw extends JavaPlugin implements Listener 
{
	public static Plugin smyhw_;
	public static Logger loger;
	public static FileConfiguration configer;
	public static String prefix;
	static List<Location> EnablePoint = new ArrayList<Location>();
	@Override
    public void onEnable() 
	{
		getLogger().info("EschatologicalUnit.SpawnMobs加载");
		getLogger().info("正在加载环境...");
		loger=getLogger();
		configer = getConfig();
		smyhw_=this;
		getLogger().info("正在加载配置...");
		saveDefaultConfig();
		prefix = configer.getString("config.prefix");
		getLogger().info("正在注册监听器...");
		Bukkit.getPluginManager().registerEvents(this,this);
		getLogger().info("EschatologicalUnit.SpawnMobs加载完成");
    }

	@Override
    public void onDisable() 
	{
		getLogger().info("EschatologicalUnit.SpawnMobs卸载");
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
        if (cmd.getName().equals("euSm"))
        {
                if(!sender.hasPermission("eu.plugin")) 
                {
                	sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
                	loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令<"+args+">{权限不足}");
                	return true;
                }
                if(args.length<1) 
                {
                	sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
                	loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令<"+args+">{参数不足}");
                	return true;
                }
                switch(args[0])
                {
                case "reload":
                {
                	reloadConfig();
                	configer=getConfig();
                	sender.sendMessage(prefix+"重载配置文件...");
                	return true;
                }
                case"set":
                {
                	if(args.length<3) {CSBZ(sender);return true;}
                	String GroupName = args[1];
                	String PointName = args[2];
                	Location zb = ((Player)sender).getLocation();
                	sender.sendMessage(prefix+"刷怪组<"+GroupName+">刷怪点<"+PointName+">已经设定为<x="+(int)zb.getX()+";y="+(int)zb.getY()+";z="+(int)zb.getZ()+">");
                	configer.set("SpawnPoint."+GroupName+"."+PointName+".x", (int)zb.getX());
                	configer.set("SpawnPoint."+GroupName+"."+PointName+".y", (int)zb.getX());
                	configer.set("SpawnPoint."+GroupName+"."+PointName+".z", (int)zb.getX());
                	saveConfig();
                	return true;
                }
                case "do":
                {
                	if(args.length<2) {CSBZ(sender);return true;}
                	int Wave =Integer.parseInt( args[1]);
                	List<String> MobTypeTexts = configer.getStringList("Wave."+Wave);
                	Iterator<String> temp1 = MobTypeTexts.iterator();
                	while(temp1.hasNext())
                	{
                		String temp2 = temp1.next();
                		sender.sendMessage(temp2);
                		String[] temp3 = temp2.split("\\*");
                		String MobName = temp3[0];
                		int  MobNum = Integer.parseInt( temp3[1] );
                		for(int i=0;i<MobNum;i++)
                		{
                			SpanMob(MobName);
                		}
                	}
                    
                	return true;
                }
                case "ACT":
                {
                	if(args.length<2) {CSBZ(sender);return true;}
                	String GroupName  = args[1];
                	List Points = configer.getList("SpawnGroup."+GroupName);
                    Iterator<String> it = Points.iterator();
                    while (it.hasNext()) 
                    {
                        String PointName = (String) it.next();
                        int x = configer.getInt("SpawnPoint."+PointName+".x");
                        int y = configer.getInt("SpawnPoint."+PointName+".y");
                        int z = configer.getInt("SpawnPoint."+PointName+".z");
                        Location zb = new Location(Bukkit.getWorld("world"),x,y,z);
                        EnablePoint.add(zb);
                    }
                	return true;
                }
                
                }
                return true;                                                       
        }
       return false;
	}
	
	static void CSBZ(CommandSender sender)
	{
		sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
		loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令{参数不足}");
	}
	
	void SpanMob(String type)
	{
		String cmd = configer.getString("MOBs."+type);
		Random temp1 = new Random();
		int temp2 = temp1.nextInt(EnablePoint.size());
		int x = (int) EnablePoint.get(temp2).getX();
		int y = (int) EnablePoint.get(temp2).getY();
		int z = (int) EnablePoint.get(temp2).getZ();
		cmd = cmd.replaceAll("%x%", x+"");
		cmd = cmd.replaceAll("%y%", y+"");
		cmd = cmd.replaceAll("%z%", z+"");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),cmd);
	}
	
}