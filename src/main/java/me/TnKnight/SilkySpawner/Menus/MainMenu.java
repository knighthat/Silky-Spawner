package me.TnKnight.SilkySpawner.Menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import Files.InvConfiguration;
import Utilities.Utils;
import me.TnKnight.SilkySpawner.CustomEnchantment;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;

public class MainMenu extends MenuManager {
  public MainMenu(MenusStorage storage) {
    super(storage);
  }
  
  @Override
  public String getMenuName() { return getInv("MainMenu.Title"); }
  
  @Override
  public int getRows() { return 2; }
  
  private final Map<Material,
    String> items = new HashMap<Material, String>() {
      private static final long serialVersionUID = 1L;
      {
        put(Material.SPAWNER, "create");
        put(Material.NAME_TAG, "setname");
        put(Material.PAPER, "lore");
        put(Material.ENCHANTED_BOOK, "enchant");
        put(Material.ARMOR_STAND, "remove");
        put(Material.SLIME_BALL, "reload");
        put(Material.BOOK, "help");
        put(Material.BARRIER, "CloseMenu");
      }
    };
  
  @Override
  public void itemClicked(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    for (Material material : items.keySet())
      if (item.getType().equals(material))
        if (material.equals(Material.BARRIER)) {
          player.sendMessage(getMsg("CloseMenu"));
          player.closeInventory();
          return;
        } else if (permConfirm(player, "menu.*") ||
          permConfirm(player, "menu." + items.get(material) + ".*") ||
          permConfirm(player, "menu." + items.get(material))) {
          String key = items.get(material);
          if (e.getSlot() == 2 || e.getSlot() == 3)
            if (!player.getInventory().getItemInMainHand().getType().equals(Material.SPAWNER)) {
              player.sendMessage(getMsg("NotHoldingSpawner").replace("%type%", ValidateCfg(e.getSlot() == 2 ? "Name" : "Lore")));
              return;
            }
          switch (e.getSlot()) {
            case 1 :
              new MobsListMenu(storage).openMenu();
              break;
            
            case 2 :
              accessModification(player, true, ConfirmType.NAME);
              break;
            
            case 3 :
              new LoreMenu(storage).openMenu();
              break;
            
            case 4 :
              if (!getBoolean("CustomEnchantment")) {
                player.sendMessage(getMsg("CETurnedOff"));
                return;
              }
              ItemStack inHand = new ItemStack(player.getInventory().getItemInMainHand());
              if (!inHand.getType().name().endsWith("_PICKAXE")) {
                player.sendMessage(getMsg("NotHoldingPickaxe"));
                return;
              }
              if (inHand.containsEnchantment(CustomEnchantment.PICKDASPAWNER)) {
                player.sendMessage(getMsg("AlreadyAdded"));
                return;
              }
              CustomEnchantment.enchantItem(inHand);
              storage.setType(ConfirmType.CONFIRM_ITEM);
              storage.setSpawner(inHand);
              new ConfirmMenu(storage).openMenu();
              break;
            
            case 5 :
              storage.setLine(1);
              storage.setType(ConfirmType.REMOVE_ARMORSTANDS);
              new ConfirmMenu(storage).openMenu();
              break;
            
            default :
              player.performCommand("silkyspawner " + key);
              player.closeInventory();
              break;
          }
        }
  }
  
  @Override
  public void setMenuItems() {
    int slot = 1;
    Set<String> sections = InvConfiguration.getConfig().getConfigurationSection("MainMenu").getKeys(false);
    sections.stream().filter(section -> !section.equals("Title") && !section.equals("Close")).close();
    for (String key : sections) {
      String path = "MainMenu." + key + ".";
      String materialName = new String();
      switch (key.replace("Item", "")) {
        case "CreateSpawner" :
          materialName = "SPAWNER";
          break;
        
        case "SetName" :
          materialName = "NAME_TAG";
          break;
        
        case "SetLore" :
          materialName = "PAPER";
          break;
        
        case "EnchantPickaxe" :
          materialName = "ENCHANTED_BOOK";
          break;
        
        case "Remove" :
          materialName = "ARMOR_STAND";
          break;
        
        case "Reload" :
          materialName = "SLIME_BALL";
          break;
      }
      Material material = Material.getMaterial(materialName);
      List<String> lore = InvConfiguration.getConfig().contains(path + "Name")
        ? InvConfiguration.getConfig().getStringList(path + "Lore").stream().map(string -> Utils.AddColors(string)).collect(Collectors.toList())
        : new ArrayList<String>();
      setInvItem(material, 1, getInv(path + "Name"), lore, slot++);
    }
    setInvItem(Material.BARRIER, 1, getInv("CloseButton"), null, 13);
  }
}
