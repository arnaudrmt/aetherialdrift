package fr.arnaud.aetherialdrift.world;

import fr.arnaud.aetherialdrift.AetherialDrift;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class RegeneratingResource {

    private final Location location;
    private final Material material;
    private final byte data;
    private final long respawnTicks;
    private boolean isDepleted = false;

    public RegeneratingResource(Location location, Material material, long respawnSeconds, byte data) {
        this.location = location;
        this.material = material;
        this.respawnTicks = respawnSeconds * 20L;
        this.data = data;
    }

    public RegeneratingResource(Location location, Material material, long respawnSeconds) {
        this(location, material, respawnSeconds, (byte) 0);
    }

    public void harvest() {
        if (isDepleted) return;

        this.isDepleted = true;
        location.getBlock().setType(Material.BEDROCK);

        Bukkit.getScheduler().runTaskLater(AetherialDrift.getInstance(), this::respawn, respawnTicks);
    }

    private void respawn() {
        this.isDepleted = false;
        Block block = location.getBlock();
        if (block.getChunk().isLoaded()) {
            block.setType(material);
            block.setData(this.data);
        }
    }

    public Location getLocation() {
        return location;
    }

    public boolean isDepleted() {
        return isDepleted;
    }
}