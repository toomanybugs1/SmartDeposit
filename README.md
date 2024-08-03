# Smart Deposit

A simple plugin for Bukkit that allows users to quickly empty their inventory into selected/nearby chests and smartly sort items based on what is already in chests.

## Commands
**Smart Deposit:**
- Usage: /sd
- Permission: smartdeposit.deposit
- Description: Deposits player's inventory into chests within a 5 block radius in any direction. Only deposits items that already appear in the chests. For example, if the player has cobblestone, the cobblestone will be deposited into the first chest containing any cobblestone with space for more. Will not deposit item if any of the same items are not found. 

**Configure Radius:**
- Usage: /sd-radius <radius>
- Permission: smartdeposit.radius
- Description: Changes the global setting for the radius in which Smart Deposit will be able to detect chests.

## Permissions
- smartdeposit.deposit: Allows user to use /sd command.
- smartdeposit.radius: Allows user to use /sd-radius command.

## To Do

- Integration with lockette??
