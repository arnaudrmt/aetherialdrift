package fr.arnaud.aetherialdrift.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface NMSHandler {

    int spawnHologram(Location location, String text, Collection<Player> players);

    void updateHologram(int entityId, String text, Collection<Player> players);

    void removeNMSEntity(int entityId, Collection<Player> players);

    void sendActionBar(Player player, String message);
}
