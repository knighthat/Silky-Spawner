package Utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;

import Files.Mobs;

public enum MobsList {

	ZOMBIE, SKELETON, BAT, BEE, BLAZE, CAT, CAVE_SPIDER, CHICKEN, COD, COW, CREEPER, DOLPHIN, DONKEY, DROWNED, ELDER_GUARDIAN, ENDER_DRAGON, ENDERMAN,
	ENDERMITE, EVOKER, FOX, GHAST, GIANT, OCELOT, GUARDIAN, HOGLIN, HORSE, HUSK, ILLUSIONER, IRON_GOLEM, LLAMA, MAGMA_CUBE, MOOSHROOM, MULE, PANDA,
	PARROT, PHANTOM, PIG, PIGLIN, PILLAGER, POLAR_BEAR, PUFFERFISH, RABBIT, RAVANGER, SALMON, SHEEP, SHULKER, SILVERFISH, SKELETON_HORSE, SLIME,
	SNOW_MAN, SPIDER, SQUID, STRAY, STRIDER, TRADER_LLAMA, TROPICAL_FISH, TURTLE, VEX, VILLAGER, VINDICATOR, WANDERING_TRADER, WITCH, WITHER_BOSS,
	WITHER_SKELETON, WOLF, ZOGLIN, ZOMBIE_HORSE, ZOMBIE_VILLAGER, ZOMBIFIED_PIGLIN;

	private static final List<String> list = Arrays.asList(MobsList.values()).stream().map(mob -> mob.toString()).filter(mob -> MobChecking(mob))
	    .sorted().collect(Collectors.toList());
	private static boolean MobChecking(String MobsName) {
		return Arrays.stream(EntityType.values()).map(EntityType::name).collect(Collectors.toList()).contains(MobsName.toUpperCase());
	}

	public static List<String> toList() {
		Collections.sort(list);
		return list;
	}

	private static Map<String, String> mobCustomName = new HashMap<>();

	public static void setCustomName() {
		toList().stream().forEach(mob -> mobCustomName.put(mob, mob));
		Mobs.getConfig().getConfigurationSection("").getKeys(false).forEach(mob -> {
			String Mob = mob.toUpperCase();
			if (toList().contains(Mob))
				mobCustomName.replace(Mob, Mob, Mobs.getConfig().getString(mob));
		});
	}
	public static String getMobName(String mob) {
		return mobCustomName.get(mob.toUpperCase());
	}
}
