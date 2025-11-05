package fr.arnaud.aetherialdrift.nms.v1_21_R1;

import fr.arnaud.aetherialdrift.api.BukkitWrapperAPI;
import org.bukkit.Material;
import org.bukkit.Sound;

public class Wrapper_v1_21_R1 implements BukkitWrapperAPI {

    @Override
    public Sound getSound(BukkitWrapperAPI.SoundType type) {
        switch (type) {
            case NOTE_PLING: return Sound.BLOCK_NOTE_BLOCK_PLING;
            case ORB_PICKUP: return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
            case DIG_WOOD: return Sound.BLOCK_WOOD_BREAK;
        }
        return Sound.ENTITY_PLAYER_LEVELUP;
    }

    @Override
    public Material getMaterial(MaterialType type) {
        switch (type) {
            case OAK_LOG: return Material.OAK_LOG;
            case ENDER_STONE: return Material.END_STONE;
            case MYCELIUM: return Material.MYCELIUM;
            case GRASS_BLOCK: return Material.GRASS_BLOCK;
        }
        return Material.STICK;
    }
}
