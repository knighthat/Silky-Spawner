package me.TnKnight.SilkySpawner.Menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MenusStorage {

	private Player player;
	private ItemStack spawner;
	private ConfirmType type;

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

	public MenusStorage(Player player) {
		super();
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public enum ConfirmType {
		CREATE, MODIFICATION;
	}
}
