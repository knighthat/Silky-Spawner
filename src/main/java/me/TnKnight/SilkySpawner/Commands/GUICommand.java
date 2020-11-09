package me.TnKnight.SilkySpawner.Commands;

import org.bukkit.entity.Player;

import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Menus.MainMenu;

public class GUICommand extends CommandsAbstractClass {
  @Override
  public String getName() { return "gui"; }
  
  @Override
  public void executeCommand(Player player, String label, String[] args) {
    switch (args.length) {
      case 0 :
        new MainMenu(SilkySpawner.getStorage(player)).openMenu();
        break;
      
      default :
        player.spigot().sendMessage(cBuilder(getUsg().replace(getUsg().split(" ")[0], label)).create());
        break;
    }
  }
}
