package me.TnKnight.SilkySpawner.Menus;

import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MenusStorage
{
	public MenusStorage(Player player) {
		super();
		this.player = player;
	}
	
	private Player player;
	private ItemStack spawner;
	private ConfirmType type;
	private int line;
	private Boolean bolean = false;
	private CreatureSpawner creatureSpawner;
	private Boolean hand;
	private Location location;
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Boolean getHand() {
		return hand;
	}
	
	public void setHand(Boolean hand) {
		this.hand = hand;
	}
	
	public CreatureSpawner getCreatureSpawner() {
		return creatureSpawner;
	}
	
	public void setCreatureSpawner(CreatureSpawner creatureSpawner) {
		this.creatureSpawner = creatureSpawner;
	}
	
	public int getLine() {
		return line;
	}
	
	public void setLine(int line) {
		this.line = line;
	}
	
	public Boolean getBolean() {
		return bolean;
	}
	
	public void setBolean(Boolean bolean) {
		this.bolean = bolean;
	}
	
	public ConfirmType getType() {
		return type;
	}
	
	public void setType(ConfirmType type) {
		this.type = type;
	}
	
	public ItemStack getSpawner() {
		return spawner;
	}
	
	public void setSpawner(ItemStack spawner) {
		this.spawner = spawner;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public enum ConfirmType {
		CREATE, NAME, ADD_LORE, SET_LORE, INSERT_LORE, REMOVE_LORE, REMOVE_ARMORSTANDS, CONFIRM_ITEM;
	}
}
