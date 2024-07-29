package io.github.toomanybugs1.smartdeposit;

import java.util.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class SmartDeposit extends JavaPlugin {
	
	List<String> enabledPlayers;
	
	@Override
    public void onEnable() {
		this.saveDefaultConfig();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!cmd.getName().equalsIgnoreCase("togglesmartdeposit") || args.length != 0)
            return false;

        if (sender instanceof Player) {

            this.enabledPlayers = this.getConfig().getStringList("players-enabled");

            String playerName = sender.getName();

            if (this.enabledPlayers.contains(playerName)) {
                this.enabledPlayers.remove(playerName);

                sender.sendMessage(ChatColor.GOLD + "[SmartDeposit] "
                    + ChatColor.DARK_RED + "Smart depositing disabled.");
            } else {
                this.enabledPlayers.add(playerName);

                sender.sendMessage(ChatColor.GOLD + "[SmartDeposit] "
                    + ChatColor.DARK_GREEN + "Smart depositing enabled.");
            }

            this.getConfig().set("players-enabled", this.enabledPlayers);
            this.saveConfig();
        } else {
            sender.sendMessage("Only players can use this command.");
        }


        return true;
    }
}
