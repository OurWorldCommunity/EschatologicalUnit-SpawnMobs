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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	public static int delayed;
	@Override
    public void onEnable() 
	{
		getLogger().info("EschatologicalUnit.SpawnMobs加载");
		getLogger().info("正在加载环境...");
		loger=getLogger();
		configer = getConfig();
		smyhw_=this;
		getLogger().info("正在加载配置...");
		prefix = configer.getString("config.prefix");
		delayed = configer.getInt("config.delayed");
		saveConfig();
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
        if (cmd.getName().equals("euS"))
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
                if(args[0].equals("reload"))
                {
                	reloadConfig();
                	configer=getConfig();
                	sender.sendMessage(prefix+"重载配置文件...");
                }
                new showThread(configer.getStringList("data.Sentence."+args[0]).iterator());
                return true;                                                       
        }
       return false;
	}
}

class showThread extends BukkitRunnable
{
	public Iterator<String> msg;
	public showThread(Iterator<String> msg)
	{
		this.msg = msg;
		this.runTaskTimer(smyhw.smyhw_,0,smyhw.delayed*20);
	}
	
	public void run()
	{
        if(msg.hasNext())
        {
        	String mmsg = msg.next();
        	mmsg = mmsg.replace('&', '§');
        	Bukkit.broadcastMessage(mmsg);
        }
        else
        {
        	this.cancel();
        }
	}
}