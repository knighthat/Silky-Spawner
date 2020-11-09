package me.TnKnight.SilkySpawner.Menus;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Utilities.Methods;
import Utilities.Utils;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;

public class ConfirmMenu extends MenuManager {
  public ConfirmMenu(MenusStorage storage) {
    super(storage);
  }
  
  private boolean y = false;
  private final ConfirmType type = storage.getType();
  
  @Override
  public String getMenuName() {
    StringBuffer title = new StringBuffer();
    switch (type) {
      case INSERT_LORE :
      case REMOVE_LORE :
      case SET_LORE :
        title.append("LineRangeMenu.LineTitle");
        y = true;
        break;
      
      case REMOVE_ARMORSTANDS :
        title.append("LineRangeMenu.RangeTitle");
        y = false;
        break;
      
      case CREATE :
      case CONFIRM_ITEM :
        title.append("ConfirmMenu.Title");
        break;
      
      default :
        break;
    }
    return getInv(title.toString());
  }
  
  @Override
  public int getRows() { return 3; }
  
  @Override
  public void itemClicked(InventoryClickEvent e) {
    final Player player = (Player) e.getWhoClicked();
    ItemStack inHand = new ItemStack(player.getInventory().getItemInMainHand());
    ItemMeta hMeta = inHand.getItemMeta();
    if (type.equals(ConfirmType.CONFIRM_ITEM)) {
      if (e.getSlot() == 25) {
        Methods.addItem(player, storage.getSpawner(), true);
        player.sendMessage(getMsg("GetSpawner"));
        player.closeInventory();
      } else if (e.getSlot() == 19)
        new MainMenu(storage).openMenu();
      return;
    }
    ItemStack show = storage.getSpawner();
    ItemMeta sMeta = show.getItemMeta();
    String clicked = Utils.StripColors(e.getCurrentItem().getItemMeta().getDisplayName());
    String DisplayName = Utils.StripColors(getInv("LineRangeMenu." + (y ? "LineDisplay" : "RangeDisplay")));
    DisplayName = DisplayName.replace("%line%", "").replace("%radius%", "");
    int number = type.equals(ConfirmType.CREATE) ? show.getAmount() : Integer.parseInt(Utils.StripColors(sMeta.getDisplayName()).replace(
      DisplayName,
      ""));
    switch (e.getSlot()) {
      case 9 :
        new MainMenu(storage).openMenu();
        break;
      
      case 17 :
        if (type.equals(ConfirmType.CREATE)) {
          Methods.addItem(player, show, false);
          player.sendMessage(getMsg("GetSpawner"));
          player.closeInventory();
        } else if (type.equals(ConfirmType.REMOVE_LORE)) {
          hMeta.getLore().remove(storage.getLine() - 1);
          inHand.setItemMeta(hMeta);
          storage.setBolean(true);
          storage.setSpawner(inHand);
          storage.setType(ConfirmType.CONFIRM_ITEM);
          openMenu();
        } else if (type.equals(ConfirmType.REMOVE_ARMORSTANDS)) {
          Methods.clearArea(player.getLocation(), number);
          player.sendMessage(getMsg("RemoveArmorStandsMessage").replace("%radius%", String.valueOf(number)));
          player.closeInventory();
        } else
          accessModification(player, false, type);
        break;
      
      case 10 :
      case 11 :
      case 12 :
      case 14 :
      case 15 :
      case 16 :
        String amt = clicked.equals("1") || clicked.equals("64") ? clicked : String.valueOf(number + Integer.parseInt(clicked));
        int current = Integer.parseInt(amt);
        if (type.equals(ConfirmType.CREATE)) {
          if (current < 1 || current > show.getMaxStackSize())
            amt = String.valueOf(current < 1 ? 1 : show.getMaxStackSize());
          show.setAmount(Integer.parseInt(amt));
        } else {
          int max = type.equals(ConfirmType.REMOVE_ARMORSTANDS) ? 100 : (hMeta.getLore().size() - 1);
          if (current < 1 || current > max)
            amt = current < 1 ? "1" : String.valueOf(max);
          storage.setLine(Integer.parseInt(amt));
          sMeta.setDisplayName(Utils.AddColors(getInv("LineRangeMenu." + (y ? "LineDisplay" : "RangeDisplay")).replace("%line%", amt)
            .replace("%radius%", amt)));
          show.setItemMeta(sMeta);
        }
        storage.setSpawner(show);
        openMenu();
        break;
      
      default :
        break;
    }
  }
  
  @Override
  public void setMenuItems() {
    final boolean getType = type.equals(ConfirmType.CREATE) || type.equals(ConfirmType.CONFIRM_ITEM);
    String item = getType ? "ConfirmMenu.FillItem" : "LineRangeMenu.FillItem";
    for (int slot = 0; slot < inventory.getSize(); slot++)
      setInvItem(Utils.ItemsChecking(item), 1, " ", null, slot);
    ItemStack display = getType ? storage.getSpawner() : new ItemStack(Material.getMaterial(y ? "PAPER" : "ARMOR_STAND"));
    if (!getType) {
      ItemMeta dMeta = display.getItemMeta();
      String line = String.valueOf(storage.getLine());
      String displayName = getInv("LineRangeMenu." + (y ? "LineDisplay" : "RangeDisplay")).replace("%line%", line).replace("%radius%", line);
      dMeta.setDisplayName(Utils.AddColors(displayName));
      ArrayList<String> lore = new ArrayList<>(Arrays.asList(" ", "", " "));
      lore.set(1, storage.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore().get(storage.getLine() - 1));
      dMeta.setLore(lore);
      display.setItemMeta(dMeta);
      storage.setSpawner(display);
    }
    inventory.setItem(13, display);
    if (!type.equals(ConfirmType.CONFIRM_ITEM)) {
      setInvItem(Material.TORCH, 1, "&c1", null, 10);
      setInvItem(Material.TORCH, 10, "&c-10", null, 11);
      setInvItem(Material.TORCH, 1, "&c-1", null, 12);
      setInvItem(Material.REDSTONE_TORCH, 1, "&a+1", null, 14);
      setInvItem(Material.REDSTONE_TORCH, 10, "&a+10", null, 15);
      setInvItem(Material.REDSTONE_TORCH, 64, "&a64", null, 16);
      YesNoButton(17, 9);
    } else
      YesNoButton(25, 19);
  }
}
