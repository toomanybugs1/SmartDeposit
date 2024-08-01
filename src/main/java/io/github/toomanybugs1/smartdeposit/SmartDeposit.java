package io.github.toomanybugs1.smartdeposit;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;

import net.md_5.bungee.api.ChatColor;

public class SmartDeposit extends JavaPlugin {
	
	final int CHEST_RADIUS = 5;
	int chestRadius;
	
	@Override
    public void onEnable() {
		
		this.saveDefaultConfig();
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
                        + ChatColor.DARK_RED + "Could not deposit your inventory.");
        	}
            
        } else {
            sender.sendMessage("Only players can use this command.");
        }


        return true;
    }
    
    /**
     * Loop over all items in the player's inventory. Loop over every chest in the radius of the player. At every item per chest, try to deposit if chest contains the object.
     * @param player The player that sent the command.
     * @return Boolean, true if we deposited any items, false if we didn't.
     */
    private boolean smartDeposit(Player player) {
    	
    	ArrayList<Inventory> chestInvs = getChests(player.getLocation(), CHEST_RADIUS, player.getWorld());
    	PlayerInventory pInv = player.getInventory();
    	
    	for (Inventory chest : chestInvs) {
    		depositToChest(pInv, chest);
    	}	
    	
    	return true;
    }
    
    /**
    * Get all chest inventories within the radius of the player. 
    * @param pLoc The location of the player.
    * @param radius The radius around the player that we will look for chests.
    * @param world The world object in which the player exists.
    * @return List of the inventories of the chests found in the radius around the player.
    */
    private ArrayList<Inventory> getChests(Location pLoc, int radius, World world) {
    	ArrayList<Inventory> chestList = new ArrayList<Inventory>();
    	
    	int xLoc = pLoc.getBlockX();
    	int yLoc = pLoc.getBlockY();
    	int zLoc = pLoc.getBlockZ();
    	
    	ArrayList<InventoryHolder> doubleChests = new ArrayList<InventoryHolder>();
    	
    	for (int i = xLoc - radius; i < xLoc + radius; i++) {
    		for (int j = yLoc - radius; j < yLoc + radius; j++) {
    			for (int k = zLoc - radius; k < zLoc + radius; k++) {
    				BlockState b = world.getBlockAt(i, j, k).getState();
    				
    				if (b instanceof DoubleChest) {
    					DoubleChest chest = (DoubleChest) b;
    					
    					if (!doubleChests.contains(chest.getLeftSide()) && 
    					    !doubleChests.contains(chest.getRightSide())) {
    						
        					if (chest.getLeftSide() != null) {
        						doubleChests.add(chest.getLeftSide());
        					}
        					else {
        						doubleChests.add(chest.getRightSide());
        					}
        					
    						chestList.add(chest.getInventory());
    					}
    					
    				}
    				else if (b instanceof Chest) {
						Chest chest = (Chest) b;
						chestList.add(chest.getBlockInventory());
					}
    				else {
    					// Do nothing, not a chest.
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
    	// iterate over SLOTS not just item stacks
    	for(ItemStack playerItem : playerInv.getStorageContents()) {
    	    if (playerItem != null && chestInv.contains(playerItem.getType())) {
    	    	HashMap<Integer, ItemStack> leftOverItems = chestInv.addItem(playerItem);
    	    	
    	    	if (!leftOverItems.isEmpty()) {
    	    		playerItem.setAmount(((ItemStack) leftOverItems.values().toArray()[0]).getAmount());
    	    	}
    	    	else {
    	    		playerInv.remove(playerItem);
    	    	}
    	    }
    	}
    	
    	return true;
    }
}
