package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Utilities.Methods;
import Utilities.Utils;

public class SetNameCommand extends CommandsAbstractClass {
  @Override
  public String getName() { return "setname"; }
  
  @Override
  public void executeCommand(Player player, String label, String[] args) {
    if (!mConfirm(player, "SPAWNER", "Name"))
      return;
    if (!cConfirm(player, Utils.arrayToString(args, 0), getUsg(), label))
      return;
    if (!Utils.charsCounting(player, Utils.arrayToString(args, 0), "Name"))
      return;
    ItemStack spawner = new ItemStack(player.getInventory().getItemInMainHand());
    ItemMeta sMeta = spawner.getItemMeta();
    sMeta.setDisplayName(Utils.AddColors(Utils.arrayToString(args, 0)));
    spawner.setItemMeta(sMeta);
    Methods.addItem(player, spawner, true);
  }
}
