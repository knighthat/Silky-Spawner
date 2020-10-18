package me.TnKnight.SilkySpawner.MenusStorage;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TnKnight.SilkySpawner.Utils;
import me.TnKnight.SilkySpawner.Files.InventoriesConfiguration;

public abstract class AbstractMobsListMenu extends MenuAbstractClass {

	public AbstractMobsListMenu(Storage storage) {
		super(storage);
	}
	protected int page = 0;
	protected int itemsPerPage = 28;
	protected int index = 0;

	public void fillItem() {
		ItemStack filler = super.FILLER;
		ItemMeta fMeta = filler.getItemMeta();
		fMeta.setDisplayName(" ");
		filler.setItemMeta(fMeta);
		for (int rows = 0; rows < 6; rows++)
			for (int slot = rows * 9; slot < rows * 9 + 9; slot++)
				if (rows == 0 || rows == 5 || ((rows > 0 && rows < 5) && (rows * 9 == slot || slot == rows * 9 + 8)))
					inventory.setItem(slot, super.FILLER);
		addItem(Material.DARK_OAK_BUTTON, "CreateSpawnerMenu.PreviousPage", 48);
		addItem(Material.REDSTONE, "CreateSpawnerMenu.GoBackButton", 49);
		addItem(Material.DARK_OAK_BUTTON, "CreateSpawnerMenu.NextPage", 50);

	}

	private void addItem(Material material, String path, int slot) {
		ItemStack button = new ItemStack(material);
		ItemMeta meta = button.getItemMeta();
		meta.setDisplayName(Utils.AddColors(InventoriesConfiguration.getConfig().getString(path)));
		button.setItemMeta(meta);
		inventory.setItem(slot, button);
	}
}
