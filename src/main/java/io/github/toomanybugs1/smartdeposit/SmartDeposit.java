package io.github.toomanybugs1.smartdeposit;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;

import net.md_5.bungee.api.ChatColor;

public class SmartDeposit extends JavaPlugin {
	
	int chestRadius;
	
	@Override
    public void onEnable() {		
		this.saveDefaultConfig();

        this.chestRadius = this.getConfig().getInt("deposit-radius");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("sd") && args.length == 0) {
        	if (sender instanceof Player) {
        		
        		Player player = (Player) sender;
        		
        		if (player.hasPermission("smartdeposit.deposit")) {
        			if (this.smartDeposit(player)) {
                		player.sendMessage(ChatColor.GOLD + "[SmartDeposit] "
                                + ChatColor.DARK_GREEN + "Deposited inventory successfully.");
                	}
                	else {
                		player.sendMessage(ChatColor.GOLD + "[SmartDeposit] "
                                + ChatColor.DARK_RED + "Nothing can be deposited or chests not in range.");
                	}
        		}
        		else {
        			sender.sendMessage(ChatColor.GOLD + "[SmartDeposit] " + ChatColor.RED + "Only players with permissions can use this command.");
        		}
            } 
        	else {
                sender.sendMessage("Only players can use this command.");
            }

            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("sd-radius") && args.length == 1) {
        	if (sender instanceof Player) {
        		Player player = (Player) sender;
        		int newRad;

        		if (player.hasPermission("smartdeposit.radius")) {
	        		try {
	            		newRad = Integer.parseInt(args[0]);
	            	}
	            	catch (Exception e) {
	            		sender.sendMessage(ChatColor.GOLD + "[SmartDeposit] " + ChatColor.RED + "Radius must be a number.");
	            		return false;
	            	}
	        		
	        		this.getConfig().set("deposit-radius", newRad);
	        		this.saveConfig();
	        		
	        		sender.sendMessage(ChatColor.GOLD + "[SmartDeposit] "
	                        + ChatColor.DARK_GREEN + "Saved deposit radius.");
        		}
        		else {
        			sender.sendMessage(ChatColor.GOLD + "[SmartDeposit] " + ChatColor.RED + "Only players with permissions can use this command.");
        		}
            } 
        	else {
                sender.sendMessage("Only players can use this command.");
            }

        	return true;
        }
        else {
        	return false;
        }
    }
    
    /**
     * Loop over all items in the player's inventory. Loop over every chest in the radius of the player. At every item per chest, try to deposit if chest contains the object.
     * @param player The player that sent the command.
     * @return Boolean, true if we deposited any items, false if we didn't.
     */
    private boolean smartDeposit(Player player) {
    	ArrayList<Inventory> chestInvs = getChests(player.getLocation(), player.getWorld());
    	PlayerInventory pInv = player.getInventory();
    	
    	boolean didDeposit = false;
    	
    	for (Inventory chest : chestInvs) {
    		boolean curDeposit = depositToChest(pInv, chest);
    
    		if (!didDeposit && curDeposit) {
    			didDeposit = true;
    		}
    	}	
    	
    	return didDeposit;
    }
    
    /**
    * Get all chest inventories within the radius of the player. 
    * @param pLoc The location of the player.
    * @param radius The radius around the player that we will look for chests.
    * @param world The world object in which the player exists.
    * @return List of the inventories of the chests found in the radius around the player.
    */
    private ArrayList<Inventory> getChests(Location pLoc, World world) {
    	ArrayList<Inventory> chestList = new ArrayList<Inventory>();
    	this.chestRadius = this.getConfig().getInt("deposit-radius");
    	
    	int xLoc = pLoc.getBlockX();
    	int yLoc = pLoc.getBlockY();
    	int zLoc = pLoc.getBlockZ();
    	
    	ArrayList<DoubleChest> doubleChests = new ArrayList<DoubleChest>();
    	
    	for (int i = xLoc - this.chestRadius; i < xLoc + this.chestRadius; i++) {
    		for (int j = yLoc - this.chestRadius; j < yLoc + this.chestRadius; j++) {
    			for (int k = zLoc - this.chestRadius; k < zLoc + this.chestRadius; k++) {
    				BlockState b = world.getBlockAt(i, j, k).getState();
    				
    				if (b instanceof Chest) {
    					Chest chest = (Chest) b;
    					Inventory inventory = chest.getInventory();
    					
    					if (inventory instanceof DoubleChestInventory) {
    						DoubleChest doubleChest = (DoubleChest) inventory.getHolder();
    						
    						if (!doubleChests.contains(doubleChest)) {
    							chestList.add(inventory);
    							doubleChests.add(doubleChest);
    						}
    					}
    					else {
    						chestList.add(inventory);
    					}
    				}
    			}
    		}
    	}
    	
    	return chestList;
    }
    
    /**
     * Given a chest and a player inventory, attempt to deposit items from the player to the chest. Only deposit if we see the same item in the chest as the inventory slot.
     * @param playerInv The player's inventory.
     * @param chestInv The chest's inventory.
     * @return Boolean, true if anything was deposited, false if not.
     */
    private boolean depositToChest(PlayerInventory playerInv, Inventory chestInv) {
    	boolean didDeposit = false;
    	
    	for(ItemStack playerItem : playerInv.getStorageContents()) {
    	    if (playerItem != null && chestInv.contains(playerItem.getType())) {
    	    	int itemAmtBeforeDeposit = playerItem.getAmount();	
    	    	
    	    	HashMap<Integer, ItemStack> leftOverItems = chestInv.addItem(playerItem);
    	    	
    	    	if (!leftOverItems.isEmpty()) {
    	    		int newItemAmt = ((ItemStack) leftOverItems.values().toArray()[0]).getAmount();
    	    		
    	    		if (newItemAmt != itemAmtBeforeDeposit) {
    	    			playerItem.setAmount(newItemAmt);
    	    			didDeposit = true;
    	    		}
    	    	}
    	    	else {
    	    		playerInv.remove(playerItem);
    	    		didDeposit = true;
    	    	}
    	    }
    	}
    	
    	return didDeposit;
    }
}
