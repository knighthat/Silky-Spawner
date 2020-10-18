package me.TnKnight.SilkySpawner.MenusStorage;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Storage {

	private Player player;
	private ItemStack spawner;

	public ItemStack getSpawner() {
		return spawner;
	}

	public void setSpawner(ItemStack spawner) {
		this.spawner = spawner;
	}

	public Storage(Player player) {
		super();
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public static Map<Player, Boolean> lore = new HashMap<>();
	public static Map<Player, String> returns = new HashMap<>();
}
