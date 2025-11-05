package fr.arnaud.aetherialdrift.utils;

import org.bukkit.Location;

public class LocationUtils {

    public static boolean isSameBlock(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return false;
        }
        if (loc1.getWorld() != null && loc2.getWorld() != null && !loc1.getWorld().equals(loc2.getWorld())) {
            return false;
        }
        return loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ();
    }
}
