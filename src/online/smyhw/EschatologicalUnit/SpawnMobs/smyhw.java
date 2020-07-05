package online.smyhw.EschatologicalUnit.SpawnMobs;

import org.bukkit.configuration.ConfigurationSection;
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
	public static List<Location> EnablePoint;
	public static Hashtable<String,Continued> ContinuedMap = new Hashtable<String,Continued>();
	@Override
    public void onEnable() 
	{
		getLogger().info("EschatologicalUnit.SpawnMobs加载");
		getLogger().info("正在加载环境...");
		loger=getLogger();
		configer = getConfig();
		smyhw_=this;
		getLogger().info("正在加载配置...");
		EnablePoint = new ArrayList<Location>();
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
                {//设定刷怪点
                	if(args.length<3) {CSBZ(sender);return true;}
                	String GroupName = args[1];
                	String PointName = args[2];
                	Location zb = ((Player)sender).getLocation();
                	sender.sendMessage(prefix+"刷怪组<"+GroupName+">刷怪点<"+PointName+">已经设定为<x="+(int)zb.getX()+";y="+(int)zb.getY()+";z="+(int)zb.getZ()+">");
                	configer.set("SpawnPoint."+"."+GroupName+"."+PointName+".x", (int)zb.getX());
                	configer.set("SpawnPoint."+"."+GroupName+"."+PointName+".y", (int)zb.getY());
                	configer.set("SpawnPoint."+"."+GroupName+"."+PointName+".z", (int)zb.getZ());
                	saveConfig();
                	return true;
                }
                case "do":
                {//开启一波刷怪
                	if(args.length<2) {CSBZ(sender);return true;}
                	//获得需要开启的刷怪波数
                	int Wave =Integer.parseInt( args[1]);
                	//获取该波数的刷怪列表
                	List<String> MobTypeTexts = configer.getStringList("Wave."+Wave);
                	//遍历该刷怪列表
                	Iterator<String> temp1 = MobTypeTexts.iterator();
                	while(temp1.hasNext())
                	{
                		String temp2 = temp1.next();
                		sender.sendMessage(temp2);
                		String[] temp3 = temp2.split("\\*");
                		if(temp3.length!=2) 
                		{sender.sendMessage(smyhw.prefix+"语句<"+temp2+">没有检测到分隔符<*>");return true;}
                		//获取怪物名称
                		String MobName = temp3[0];
                		//获取怪物数量
                		int  MobNum = Integer.parseInt( temp3[1] );
                		//判断是否是持续性刷怪
                		if(args.length >2)
                		{
                			switch(args[2])
                			{
                			case "sc":
                			{
                    			if(ContinuedMap.containsKey(args[1]+"_"+MobName))
                    			{
                    				loger.warning("怪物种类<"+MobName+">在持续性刷怪中重复，持续性刷怪时相同种类怪物条目不能重复(您不能同时开启两个相同波数的持续性刷怪)");
                    				return true;
                    			}
                    			Continued temp4 = new Continued(MobNum,MobName);
                    			ContinuedMap.put(args[1]+"_"+MobName,temp4);
                    			sender.sendMessage(args[1]+"_"+MobName);
                    			continue;
                			}
                			case "ec":
                			{
                				sender.sendMessage(args[1]+"_"+MobName);
                				Continued  temp4 =  ContinuedMap.get(args[1]+"_"+MobName);
                				temp4.cancel();
//                				ContinuedMap.remove(args[2]+"_"+MobName);
                				continue;
                			}
                			default:
                				CSBZ(sender);
                				return true;
                			}
                		}
                		//end 持续性刷怪
                		else
                		{//开启一般刷怪
                    		for(int i=0;i<MobNum;i++)
                    		{
                    			SpanMob(MobName);
                    		}
                		}

                	}
                    
                	return true;
                }
                
                //激活刷怪组
                case "ACT":
                {
                	if(args.length<2) {CSBZ(sender);return true;}
                	String GroupName  = args[1];
                	//获取指定刷怪组的刷怪点列表
                	ConfigurationSection temp1 = configer.getConfigurationSection("SpawnPoint."+GroupName);
                	if(temp1==null)
                	{sender.sendMessage(smyhw.prefix+"对应的刷怪组不存在！");return true;}
                	Set<String> Points = temp1.getKeys(false);
                	//遍历目标刷怪组中的刷怪点
                    Iterator<String> it = Points.iterator();
                    while (it.hasNext()) 
                    {
                    	//遍历的是刷怪点的名称
                        String PointName = (String) it.next();
                        //获得刷怪点的坐标
                        int x = configer.getInt("SpawnPoint."+GroupName+"."+PointName+".x");
                        int y = configer.getInt("SpawnPoint."+GroupName+"."+PointName+".y");
                        int z = configer.getInt("SpawnPoint."+GroupName+"."+PointName+".z");
                        //根据坐标构造Location实例
                        Location zb = new Location(Bukkit.getWorld("world"),x,y,z);
                        //将构造好的实例加入激活的刷怪点列表
                        EnablePoint.add(zb);
                    }
                    sender.sendMessage(smyhw.prefix+"刷怪组<"+args[1]+">已激活");
                	return true;
                }
                case "reset":
                {//重置所有刷怪点
                	EnablePoint = new ArrayList<Location>();
                	ContinuedMap = new Hashtable<String,Continued>();
                	 sender.sendMessage(smyhw.prefix+"刷怪点已重置");
                	 return true; 
                }
                default:
                	 sender.sendMessage(smyhw.prefix+"未知指令");
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


class Continued extends BukkitRunnable
{
	String cmd;
	public Continued(int tick,String type)
	{
		cmd = smyhw.configer.getString("MOBs."+type);
		this.runTaskTimer(smyhw.smyhw_,0,tick);
	}

	@Override
	public void run() 
	{
		Random temp1 = new Random();
		int temp2 = temp1.nextInt(smyhw.EnablePoint.size());
		int x = (int) smyhw.EnablePoint.get(temp2).getX();
		int y = (int) smyhw.EnablePoint.get(temp2).getY();
		int z = (int) smyhw.EnablePoint.get(temp2).getZ();
		cmd = cmd.replaceAll("%x%", x+"");
		cmd = cmd.replaceAll("%y%", y+"");
		cmd = cmd.replaceAll("%z%", z+"");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),cmd);
	}
	
}