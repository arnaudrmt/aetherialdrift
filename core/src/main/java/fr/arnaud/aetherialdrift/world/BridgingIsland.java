package fr.arnaud.aetherialdrift.world;

import fr.arnaud.aetherialdrift.islands.ResourceType;
import org.bukkit.Location;

public class BridgingIsland {

    private final Location center;
    private final int radius;
    private final int height;
    private final ResourceType resourceType;

    public BridgingIsland(Location center, int radius, int height, ResourceType resourceType) {
        this.center = center;
        this.radius = radius;
        this.height = height;
        this.resourceType = resourceType;
    }

    public Location getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public int getHeight() {
        return height;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }
}