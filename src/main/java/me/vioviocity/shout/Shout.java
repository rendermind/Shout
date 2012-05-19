package me.vioviocity.shout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Shout extends JavaPlugin {
    
    static Logger log = Logger.getLogger("Shout");
    
    protected Pattern chatColorPattern = Pattern.compile("(?i)&([0-9A-F])");
    
    static Set<String> shout = Collections.EMPTY_SET;
    
    static FileConfiguration config = null;
    static File configFile = null;
    
    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        log.info(this + " is now disabled.");
    }

    @Override
    public void onEnable() {
        
        // set config file
        loadShoutConfig();
        saveShoutConfig();
	
	// register commands based on config
	getCommand("shout").setExecutor(new ShoutCommand(this));
        
        // start the schedule
        scheduleShouts();
	
	// metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            log.warning("[Shout] Unable to submit metrics.");
        }
	
	log.info(this + " is now enabled.");
    }
    
    public void scheduleShouts() {
	// load shouts
	if (config.isConfigurationSection("shout"))
	    shout = config.getConfigurationSection("shout").getKeys(false);
        
        // setup schedules
        for (final String each : shout) {
	    int delay = config.getInt("shout." + each + ".delay") * 20;
	    getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
		public void run() {
		
		    // init variables
		    String message = config.getString("prefix") + config.getString("shout." + each + ".message");
		    message = chatColorPattern.matcher(message).replaceAll("\u00A7$1");
		
		    // display shout
		    for(Player another : getServer().getOnlinePlayers()) {
			another.sendMessage(message);
		    }
		    log.info(message);
		}
	    }, delay, delay);
	}
    }
    
    public FileConfiguration loadShoutConfig() {
        if (config == null) {
            if (configFile == null)
                configFile = new File(this.getDataFolder(), "config.yml");
            if (configFile.exists()) {
                config = YamlConfiguration.loadConfiguration(configFile);
            } else {
                InputStream defConfigStream = getResource("config.yml");
                config = YamlConfiguration.loadConfiguration(defConfigStream);
            }
        }
        return config;
    }
    
    static public void saveShoutConfig() {
        if (config == null || configFile == null)
            return;
        try {
            config.save(configFile);
        } catch (IOException e) {
            log.severe("[Shout] Unable to save config to " + configFile + '.');
        }
    }
    
    // check permission node
    static public boolean checkPermission(String permission, Player player, Boolean notify) {
	if (!player.hasPermission(permission)) {
	    if (notify) {
		player.sendMessage(ChatColor.RED + "You do not have permission.");
		log.info("[Shout] " + player.getName() + " is denied permission to " + permission + '.');
	    }
	    return false;
	} else {
	    return true;
	}
    }
}