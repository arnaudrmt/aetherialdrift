package fr.arnaud.aetherialdrift.islands;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.api.BukkitWrapperAPI;
import org.bukkit.Material;

public enum ResourceType {
    WOOD(AetherialDrift.getInstance().getWrapperApi().getMaterial(BukkitWrapperAPI.MaterialType.OAK_LOG), 0),
    IRON(Material.IRON_ORE, 1),
    GOLD(Material.GOLD_ORE, 2),
    DIAMOND(Material.DIAMOND_ORE, 3);

    private final Material oreMaterial;
    private final int tier;

    ResourceType(Material oreMaterial, int tier) {
        this.oreMaterial = oreMaterial;
        this.tier = tier;
    }

    public Material getOreMaterial() {
        return oreMaterial;
    }

    public int getTier() {
        return tier;
    }
}