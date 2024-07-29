package io.github.toomanybugs1.smartdeposit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class SmartDeposit extends JavaPlugin {
	
	@Override
    public void onEnable() {

    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!cmd.getName().equalsIgnoreCase("sd") || args.length != 0)
            return false;

        if (sender instanceof Player) {

        	if (this.smartDeposit((Player) sender)) {
        		sender.sendMessage(ChatColor.GOLD + "[SmartDeposit] "
                        + ChatColor.DARK_GREEN + "Deposited inventory successfully.");
        	}
        	else {
        		sender.sendMessage(ChatColor.GOLD + "[SmartDeposit] "
                        + ChatColor.DARK_RED + "Deposited inventory successfully.");
        	}
            
        } else {
            sender.sendMessage("Only players can use this command.");
        }


        return true;
    }
    
    private boolean smartDeposit(Player player) {
    	return true;
    }
}
