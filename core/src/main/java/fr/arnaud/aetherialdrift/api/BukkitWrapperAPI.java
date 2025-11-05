package fr.arnaud.aetherialdrift.api;

import org.bukkit.Material;
import org.bukkit.Sound;

public interface BukkitWrapperAPI {

    enum SoundType {
        NOTE_PLING,
        ORB_PICKUP,
        DIG_WOOD,
    }

    Sound getSound(SoundType type);

    enum MaterialType {
        OAK_LOG,
        ENDER_STONE,
        MYCELIUM,
        GRASS_BLOCK,
    }

    Material getMaterial(MaterialType type);
}
