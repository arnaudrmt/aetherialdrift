package fr.arnaud.aetherialdrift.world;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.game.GameManager;
import fr.arnaud.aetherialdrift.game.data.AetherCore;
import fr.arnaud.aetherialdrift.game.data.Arena;
import fr.arnaud.aetherialdrift.game.data.Team;
import fr.arnaud.aetherialdrift.game.world.WorldConstants;
import fr.arnaud.aetherialdrift.world.IslandChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;

public class WorldManager {

    private final Location solIslandCenter = new Location(null, 200, WorldConstants.ISLAND_Y_LEVEL, 0);
    private final Location lunaIslandCenter = new Location(null, -200, WorldConstants.ISLAND_Y_LEVEL, 0);

    public World createArenaWorld(Arena arena) {
        String worldName = "drift_arena_" + arena.getId();

        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.seed(arena.getWorldSeed());
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generator(new IslandChunkGenerator(arena.getWorldSeed(), solIslandCenter, lunaIslandCenter));

        World world = Bukkit.createWorld(worldCreator);
        if (world != null) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setTime(6000);
        }
        return world;
    }

    public void deleteWorld(World world) {
        if (world == null) return;

        File worldFolder = world.getWorldFolder();

        boolean unloaded = Bukkit.unloadWorld(world, false);

        if (unloaded) {
            AetherialDrift.getInstance().getLogger().info("SYNC: Successfully unloaded world: " + world.getName());
            if (deleteWorldFolder(worldFolder)) {
                AetherialDrift.getInstance().getLogger().info("SYNC: Successfully deleted world folder.");
            } else {
                AetherialDrift.getInstance().getLogger().warning("SYNC ERROR: Could not delete world folder: " +
                        worldFolder.getPath());
            }
        } else {
            AetherialDrift.getInstance().getLogger().warning("SYNC ERROR: Could not unload world: " + world.getName());
        }
    }

    private boolean deleteWorldFolder(File path) {
        if (!path.exists()) {
            return false;
        }
        File[] files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (!deleteWorldFolder(file)) {
                        return false;
                    }
                } else {
                    if (!file.delete()) {
                        return false;
                    }
                }
            }
        }
        return path.delete();
    }

    public void prepareArena(GameManager gameManager) {
        if (gameManager.getArena().getWorld() == null) {
            AetherialDrift.getInstance().getLogger().severe("Attempted to prepare an arena with a null world!");
            return;
        }

        findTeamSpawns(gameManager.getArena());
        placeAetherCores(gameManager);
    }

    private void findTeamSpawns(Arena arena) {
        World world = arena.getWorld();

        solIslandCenter.setWorld(world);
        lunaIslandCenter.setWorld(world);

        Location solSpawn = world.getHighestBlockAt(solIslandCenter).getLocation().add(0.5, 1.5, 0.5);
        Location lunaSpawn = world.getHighestBlockAt(lunaIslandCenter).getLocation().add(0.5, 1.5, 0.5);

        arena.getTeamSpawns().put(Team.SOL, solSpawn);
        arena.getTeamSpawns().put(Team.LUNA, lunaSpawn);
    }

    private void placeAetherCores(GameManager gameManager) {
        for (Team team : Team.values()) {
            Location coreSpawnLocation = gameManager.getArena().getTeamSpawns().get(team);
            if (coreSpawnLocation != null) {
                AetherCore core = new AetherCore(team, coreSpawnLocation);
                core.spawn(gameManager.getPlayerManager().getOnlineBukkitPlayers());
                gameManager.getArena().getAetherCores().put(team, core);
            }
        }
    }
}