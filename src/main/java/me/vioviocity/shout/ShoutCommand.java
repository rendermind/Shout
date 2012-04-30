package me.vioviocity.shout;

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
    
    //Shout shout = new Shout();

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
	    if (args.length > 1)
		return false;
	    
	    // shout [reload]
	    if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
		plugin.config = null;
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
	    if (args.length == 2) {
		
	    }
	    
	    // shout [set] (shout) (delay) (message)
	    if (args.length == 4) {
		
	    }
	}
	
	// end of command
	return false;
    }
}