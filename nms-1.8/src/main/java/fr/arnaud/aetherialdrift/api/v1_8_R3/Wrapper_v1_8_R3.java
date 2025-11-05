package fr.arnaud.aetherialdrift.api.v1_8_R3;

import fr.arnaud.aetherialdrift.api.BukkitWrapperAPI;
import org.bukkit.Material;
import org.bukkit.Sound;

import static org.bukkit.Material.MYCEL;

public class Wrapper_v1_8_R3 implements BukkitWrapperAPI {

    @Override
    public Sound getSound(BukkitWrapperAPI.SoundType type) {
        switch (type) {
            case NOTE_PLING: return Sound.NOTE_PLING;
            case ORB_PICKUP: return Sound.ORB_PICKUP;
            case DIG_WOOD: return Sound.DIG_WOOD;
        }
        return Sound.LEVEL_UP;
    }

    @Override
    public Material getMaterial(MaterialType type) {
        switch (type) {
            case OAK_LOG: return Material.LOG;
            case ENDER_STONE: return Material.ENDER_STONE;
            case MYCELIUM: return MYCEL;
            case GRASS_BLOCK: return Material.GRASS;
        }
        return Material.STICK;
    }
}
