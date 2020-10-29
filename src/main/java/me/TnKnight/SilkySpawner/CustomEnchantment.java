package me.TnKnight.SilkySpawner;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantment {
	public static final Enchantment PICKDASPAWNER = new BaseCustomEnchant("spawner_picker");
	private static BaseCustomEnchant ench = new BaseCustomEnchant("spawner_picker");

	public static void Register() {
		boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(PICKDASPAWNER);
		if (!registered)
			registerEnchantment(PICKDASPAWNER);
		SilkySpawner.instance.getServer().getConsoleSender()
		    .sendMessage(Utils.AddColors(SilkySpawner.getName + "Custom Enchanment successfully registered!"));
	}

	@SuppressWarnings("unchecked")
	public static void unRegister() {
		try {
			Field field = Enchantment.class.getDeclaredField("acceptingNew");
			field.setAccessible(true);
			Map<String, Enchantment> byName = (HashMap<String, Enchantment>) field.get(null);
			if (byName.containsKey(ench.getName()))
				byName.remove(ench.getName());
		} catch (Exception e) {
		}
	}

	public static void registerEnchantment(Enchantment ench) {
		try {
			Field field = Enchantment.class.getDeclaredField("acceptingNew");
			field.setAccessible(true);
			field.set(null, true);
			Enchantment.registerEnchantment(ench);
		} catch (Exception e) {
		}
	}
}

class BaseCustomEnchant extends Enchantment {

	public BaseCustomEnchant(String key) {
		super(NamespacedKey.minecraft(key));
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment arg0) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public String getName() {
		return "SpawnerPicker";
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

}
