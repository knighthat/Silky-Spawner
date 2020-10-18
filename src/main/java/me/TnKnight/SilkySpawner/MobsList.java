package me.TnKnight.SilkySpawner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;

public enum MobsList {

	ZOMBIE, SKELETON, BAT, BLAZE, CAVE_SPIDER, CHIKEN, COD, COW, CREEPER, DOLPHIN, DONKEY, DROWNED, ELDER_GUARDIAN, ELDER_DRAGON, EDERMAN, ENDERMITE,
	EVOKER, GHAST, GIANT, GUARDIAN, HORSE, HUSK, ILLUSIONER, IRON_GOLEM, LLAMA, MAGAMA_CUBE, MOOSHROOM, MULE, OCELOT, PARROT, PHANTOM, PIG,
	POLAR_BEAR, PUFFERFISH, RABBIT, SALMON, SHEEP, SHULKER, SILVERFISH, SKELETON_HORSE, SLIME, SNOW_MAN, SPIDER, SQUID, STRAY, TROPICAL_FISH, TURTLE,
	VEX, VILLAGER, VINDICATOR, WITCH, WITHER_BOSS, WITHER_SKELETON, WOLF, ZOMBIE_HORSE, ZOMBIE_PIGMAN, ZOMBIE_VILLAGER;

	private static final List<String> list = new ArrayList<String>(Arrays.asList(MobsList.values()).stream().map(mob -> mob.toString())
	        .filter(mob -> MobChecking(mob)).sorted().collect(Collectors.toList()));

	public static List<String> toList() {
		return list;
	}

	public static boolean MobChecking(String MobsName) {
		return Arrays.stream(EntityType.values()).map(EntityType::name).collect(Collectors.toList()).contains(MobsName.toUpperCase());
	}
}
