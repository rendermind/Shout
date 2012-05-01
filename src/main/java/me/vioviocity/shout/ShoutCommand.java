package me.vioviocity.shout;

import java.util.Collections;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class ShoutCommand implements CommandExecutor {

    private Shout plugin;
    public ShoutCommand(Shout plugin) {
	this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
	// check if player
	if (!(sender instanceof Player)) {
	    sender.sendMessage(ChatColor.RED + "Command must be issued in-game.");
	    return true;
	}
	
	// initialize core variables
	Player player = (Player) sender;
	
	// command handler
	String cmd = command.getName().toLowerCase();
	if (cmd.equals("shout")) {
	    // check permission
	    if (!Shout.checkPermission("shout.shout", player, true))
		return true;
	    // invalid args
	    if (args.length < 1 || args.length == 3)
		return false;
	    
	    // shout [reload]
	    if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
		Shout.config = null;
		plugin.getPluginLoader().disablePlugin(plugin);
		plugin.getPluginLoader().enablePlugin(plugin);
		player.sendMessage(ChatColor.GREEN + "Shout reloaded.");
		return true;
	    }
	    
	    // shout [list]
	    if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
		String shoutList = "";
		for (String each : Shout.shout)
		    shoutList += each + ", ";
		shoutList = shoutList.substring(0, shoutList.length() - 2);
		player.sendMessage(ChatColor.GREEN + "Shouts: " + ChatColor.WHITE + shoutList);
		return true;
	    }
	    
	    // shout [del] (shout)
	    if (args.length == 2 && args[0].equalsIgnoreCase("del")) {
		
		// initialize variables
		String shoutName = args[1];
		
		// load shouts
		Set<String> shouts = Collections.EMPTY_SET;
		if (Shout.config.isConfigurationSection("shout"))
		    shouts = Shout.config.getConfigurationSection("shout").getKeys(false);
		
		// check shout
		for (String each : shouts) {
		    if (shoutName.equalsIgnoreCase(each)) {
			
			// delete shout
			Shout.config.set("shout." + shoutName, null);
			Shout.saveShoutConfig();
			player.sendMessage(ChatColor.RED + "Shout " + each + " deleted.");
			return true;
		    }
		}
		
		// shout not found
		player.sendMessage(ChatColor.RED + "Shout does not exist.");
		return true;
	    }
	    
	    // shout [set] (shout) (delay) (message)
	    if (args.length > 3 && args[0].equalsIgnoreCase("set")) {
		
		// initialize variables
		String shoutName = args[1];
		int shoutDelay = Integer.parseInt(args[2]);
		String shoutMessage = "";
		
		// format message
		for (int i = 3; i < args.length; i ++)
		    shoutMessage += args[i] + ' ';
		shoutMessage = shoutMessage.substring(0, shoutMessage.length() - 1);
		
		// load shouts
		Set<String> shouts = Collections.EMPTY_SET;
		if (Shout.config.isConfigurationSection("shout"))
		    shouts = Shout.config.getConfigurationSection("shout").getKeys(false);
		
		// check shout
		for (String each : shouts) {
		    if (shoutName.equalsIgnoreCase(each)) {
			
			// save shout
			Shout.config.set("shout." + each + ".message", shoutMessage);
			Shout.config.set("shout." + each + ".delay", shoutDelay * 20);
			Shout.saveShoutConfig();
			player.sendMessage(ChatColor.GREEN + "Shout " + each + " reset.");
			return true;
		    }
		}
		
		// create shout
		Shout.config.set("shout." + shoutName + ".message", shoutMessage);
		Shout.config.set("shout." + shoutName + ".message", shoutDelay * 20);
		Shout.saveShoutConfig();
		player.sendMessage(ChatColor.GREEN + "Shout " + shoutName + " set.");
		return true;
	    }
	}
	
	// end of command
	return false;
    }
}