package me.TnKnight.SilkySpawner.Commands;

import java.util.Iterator;

import org.bukkit.entity.Player;

import Utilities.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class HelpCommand extends CommandsAbstractClass {
  @Override
  public String getName() { return "help"; }
  
  @SuppressWarnings ("deprecation")
  @Override
  public void executeCommand(Player player, String label, String[] args) {
    player.sendMessage(Utils.AddColors(ValidateCfg("HelpCommand.Header")));
    Iterator<CommandsAbstractClass> cmdAb = CommandsManager.Argument.iterator();
    while (cmdAb.hasNext()) {
      CommandsAbstractClass sCommand = cmdAb.next();
      final String usage = sCommand.getUsg().replace(getUsg().split(" ")[0], label);
      ComponentBuilder builder = new ComponentBuilder(usage).color(TextColor());
      builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, usage));
      builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(HoverText()).color(HoverColor()).create()));
      builder.append(Utils.AddColors("&f - " + sCommand.getDes()));
      player.spigot().sendMessage(builder.create());
    }
  }
}
