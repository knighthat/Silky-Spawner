package me.TnKnight.SilkySpawner.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Utilities.Methods;
import Utilities.MobsList;
import Utilities.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class CreateSpawnerCommand extends CommandsAbstractClass {
  @Override
  public String getName() { return "create"; }
  
  private String label = new String();
  
  @Override
  public void executeCommand(Player player, String label, String[] args) {
    this.label = label + " " + getName() + " ";
    if (args.length == 0) {
      if (!permConfirm(player, "create.getlist") && !permConfirm(player, "create.*"))
        return;
      player.spigot().sendMessage(this.getMobsList());
    } else if (args.length <= 2) {
      if (!MobsList.toList().contains(args[0].toUpperCase())) {
        player.spigot().sendMessage(this.getMobsList());
        return;
      }
      EntityType mob = EntityType.valueOf(args[0].toUpperCase());
      if (!permConfirm(player, mob.name().toLowerCase()) && !permConfirm(player, "create.*"))
        return;
      int amount = 0;
      if (args.length == 2) {
        if (args[1].matches("-?\\d+")) {
          amount = Integer.parseInt(args[1]);
          if (amount < 1)
            return;
        } else {
          player.sendMessage(getMsg("NotANumber").replace("%input%", args[1]));
          return;
        }
      } else
        amount = 1;
      final List<String> lore = new ArrayList<>(
        Arrays.asList(ValidateCfg("TypeOfCreature").replace("%creature_type%",
          MobsList.getMobName(mob))));
      Methods.addItem(player, Methods.setItem(new ItemStack(Material.SPAWNER, amount), null, lore, mob), false);
    } else
      player.spigot().sendMessage(cBuilder(this.label).create());
  }
  
  private TextComponent getMobsList() {
    TextComponent text = new TextComponent();
    for (int i = 0; i < MobsList.toList().size(); i++)
      text.addExtra(
        Utils.hoverNclick(MobsList.toList().get(i).toLowerCase() + ChatColor.GRAY +
          ((i == MobsList.toList().size() - 1) ? "." : ", "),
          label + MobsList.toList().get(i)));
    text.setItalic(true);
    return text;
  }
}
