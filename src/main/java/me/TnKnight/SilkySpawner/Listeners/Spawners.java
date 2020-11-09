package me.TnKnight.SilkySpawner.Listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import Files.Config;
import Files.Message;
import Utilities.Methods;
import Utilities.Utils;
import me.TnKnight.SilkySpawner.CustomEnchantment;
import me.TnKnight.SilkySpawner.SilkySpawner;
import me.TnKnight.SilkySpawner.Storage;
import me.TnKnight.SilkySpawner.Menus.ConfirmMenu;
import me.TnKnight.SilkySpawner.Menus.MenusStorage;
import me.TnKnight.SilkySpawner.Menus.MenusStorage.ConfirmType;

public class Spawners extends Storage implements Listener {
  public Spawners(SilkySpawner main) {
    main.getServer().getPluginManager().registerEvents(this, main);
  }
  
  public static final Double Serial = 5.85731840133667;
  
  private void spawnAS(Block block, String name, Double addY) {
    ArmorStand as = block.getWorld().spawn(block.getLocation().add(.5, addY, .5), ArmorStand.class);
    as.setVisible(false);
    as.setHealth(5.85731858674105);
    as.setBasePlate(false);
    as.setGravity(false);
    as.setInvulnerable(true);
    as.setCollidable(false);
    as.setCanPickupItems(false);
    as.setCustomName(Utils.AddColors(name));
    as.setCustomNameVisible(true);
  }
  
  private Double getDistance() {
    Double distance = Config.getConfig().getDouble("NameAndLore.Distance") + 1D;
    if (distance < 0 || distance > 4D)
      distance = distance < 0 ? 0 : 4D;
    return distance;
  }
  
  private Double getSpace() {
    Double space = Config.getConfig().getDouble("NameAndLore.Space");
    if (space < 0 || space > 1)
      space = space < 0 ? 0 : 1D;
    return space;
  }
  
  private boolean charsCount(Player player, String message, String type) {
    if (Utils.charsCounting(player, message, type))
      return true;
    player.sendMessage(getMsg("Set" + type).replace("%min%", ValidateCfg("MinimumChars")).replace("%max%", ValidateCfg("MaximumChars")));
    player.sendMessage(Utils.AddColors(Message.getConfig().getString("RequestCancel").replace("%request%", ValidateCfg("CancelRequest"))));
    return false;
  }
  
  private List<ArmorStand> getArmorStands(Location loc, Number height) {
    Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc.add(.5, 1, .5), .5, height.doubleValue(), .5);
    List<ArmorStand> armor_stands
      = entities.stream().filter(E -> E.getType().equals(EntityType.ARMOR_STAND)).map(A -> (ArmorStand) A).collect(Collectors.toList());
    for (int i = 0; i < armor_stands.size(); i++)
      if (armor_stands.get(i).getCustomName() == null || armor_stands.get(i).getHealth() != Spawners.Serial)
        armor_stands.remove(i);
    return armor_stands;
  }
  
  @EventHandler (priority = EventPriority.HIGH)
  public void onSpawnerBreak(BlockBreakEvent e) {
    if (!e.getBlock().getType().equals(Material.SPAWNER))
      return;
    final Block spawner = e.getBlock();
    final Location sLocation = spawner.getLocation();
    final EntityType creature = ((CreatureSpawner) spawner.getState()).getSpawnedType();
    if (getBoolean("CustomEnchantment")
      && !e.getPlayer().getInventory().getItemInMainHand()
        .containsEnchantment(getBoolean("RequireEnchantment") ? CustomEnchantment.PICKDASPAWNER : Enchantment.SILK_TOUCH)) {
      e.getPlayer().sendMessage(getMsg("NotEnchanted"));
      e.setCancelled(true);
      return;
    }
    if (getBoolean("Require") && !e.getPlayer().getGameMode().equals(GameMode.valueOf(ValidateCfg("GameMode")))) {
      e.getPlayer().sendMessage(getMsg("WrongGameMode"));
      e.setCancelled(true);
      return;
    }
    List<ArmorStand> armor_stands = getArmorStands(sLocation, getDistance());
    boolean hasName = false;
    if (armor_stands.size() >= 1)
      hasName = armor_stands.size() == 1 ? true : armor_stands.get(0).getLocation().getY() -
        armor_stands.get(1).getLocation().getY() >= 0.3;
    String displayName = hasName ? armor_stands.get(0).getCustomName() : null;
    List<String> lore = new ArrayList<>();
    if (armor_stands.size() > 1) {
      if (hasName)
        lore.addAll(
          armor_stands.stream().filter(A -> !armor_stands.get(0).equals(A)).map(A -> A.getCustomName()).collect(Collectors.toList()));
    }
    lore.add(Methods.getMobName(creature));
    ItemStack Spawner = Methods.setItem(new ItemStack(Material.SPAWNER), displayName, lore, creature);
    spawner.getWorld().dropItemNaturally(sLocation, Spawner);
    armor_stands.stream().forEach(A -> A.remove());
  }
  
  @EventHandler
  public void onSpawnerPlace(BlockPlaceEvent e) {
    if (!e.getBlock().getType().equals(Material.SPAWNER))
      return;
    ItemMeta iMeta = e.getItemInHand().getItemMeta();
    Block bSpawner = e.getBlock();
    if (e.getItemInHand().getItemMeta().hasDisplayName())
      spawnAS(bSpawner, iMeta.getDisplayName(), getDistance());
    List<String> lore = iMeta.getLore();
    lore.remove(iMeta.getLore().size() - 1);
    if (iMeta.hasLore())
      for (int i = 0; i < lore.size(); i++)
        spawnAS(bSpawner, lore.get(i), (getDistance() - .3) - (getSpace() * (i + 1)));
    e.getPlayer().sendMessage(getMsg("PlaceSpawner").replace("%name%", iMeta.getDisplayName()));
  }
  
  @EventHandler
  public void asyncChat(AsyncPlayerChatEvent e) {
    Player player = e.getPlayer();
    String message = e.getMessage();
    MenusStorage storage = SilkySpawner.getStorage(player);
    if (storage.getBolean()) {
      e.setCancelled(true);
      if (message.equalsIgnoreCase(ValidateCfg("CancelRequest"))) {
        player.sendMessage(getMsg("Cancelled"));
        storage.setBolean(false);
        return;
      }
      if (!charsCount(player, message, storage.getType().equals(ConfirmType.NAME) ? "Name" : "Lore"))
        return;
      ItemStack spawner = new ItemStack(Material.SPAWNER);
      spawner.setItemMeta(storage.getSpawner().getItemMeta());
      ItemMeta sMeta = spawner.getItemMeta();
      List<String> lore = new ArrayList<>();
      if (sMeta.hasLore()) {
        lore = sMeta.getLore();
        lore.remove(sMeta.getLore().size() - 1);
      }
      int line = storage.getLine() - 1;
      switch (storage.getType()) {
        case NAME :
          sMeta.setDisplayName(Utils.AddColors(message));
          break;
        
        case ADD_LORE :
          lore.add(Utils.AddColors(message));
          break;
        
        case INSERT_LORE :
          List<String> nLore = new ArrayList<>();
          for (int i = 0; i < lore.size(); i++) {
            nLore.add(lore.get(i));
            if (lore.get(line).equals(lore.get(i)))
              nLore.add(Utils.AddColors(message));
          }
          lore.clear();
          lore = nLore;
          break;
        
        case SET_LORE :
          lore.set(line, Utils.AddColors(message));
          break;
        
        default :
          break;
      }
      if (!storage.getType().equals(ConfirmType.CREATE) && !storage.getType().equals(ConfirmType.NAME)) {
        EntityType mobType = ((CreatureSpawner) ((BlockStateMeta) sMeta).getBlockState()).getSpawnedType();
        String nLore = sMeta.hasLore() ? sMeta.getLore().get(sMeta.getLore().size() - 1) : Methods.getMobName(mobType);
        lore.add(nLore);
        sMeta.setLore(lore);
      }
      spawner.setItemMeta(sMeta);
      storage.setBolean(false);
      storage.setSpawner(spawner);
      storage.setType(ConfirmType.CONFIRM_ITEM);
      new BukkitRunnable() {
        @Override
        public void run() {
          new ConfirmMenu(storage).openMenu();
        }
      }.runTaskLater(SilkySpawner.instance, 5);
    }
  }
}