package fr.arnaud.aetherialdrift.game.data;

import fr.arnaud.aetherialdrift.world.IslandGenerator;
import fr.arnaud.aetherialdrift.world.RegeneratingResource;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class Arena {

    private final String id;
    private World world;
    private final long worldSeed;

    private final Map<Team, Location> teamSpawns = new EnumMap<>(Team.class);
    private final Map<Team, AetherCore> aetherCores = new EnumMap<>(Team.class);
    private final Map<Team, Integer> shopkeeperEntityIds = new EnumMap<>(Team.class);

    private final List<RegeneratingResource> regeneratingResources = new ArrayList<>();

    private final IslandGenerator islandGenerator;

    public Arena(String id) {
        this.id = id;
        this.worldSeed = new Random().nextLong();
        this.islandGenerator = new IslandGenerator(this.worldSeed);
    }

    public String getId() { return id; }
    public World getWorld() { return world; }
    public long getWorldSeed() { return worldSeed; }
    public IslandGenerator getIslandGenerator() { return islandGenerator; }
    public Map<Team, Location> getTeamSpawns() { return teamSpawns; }
    public Map<Team, AetherCore> getAetherCores() { return aetherCores; }
    public Map<Team, Integer> getShopkeeperEntityIds() { return shopkeeperEntityIds; }
    public List<RegeneratingResource> getRegeneratingResources() { return regeneratingResources; }

    public void setWorld(World world) { this.world = world; }
}