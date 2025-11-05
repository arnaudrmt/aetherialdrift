package fr.arnaud.aetherialdrift.world;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.api.BukkitWrapperAPI;
import fr.arnaud.aetherialdrift.islands.ResourceType;
import fr.arnaud.aetherialdrift.utils.FastNoiseLite;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class IslandGenerator {

    private final BukkitWrapperAPI wrapperAPI;
    private final FastNoiseLite lowTierMaterialNoise;
    private final FastNoiseLite highTierMaterialNoise;

    public IslandGenerator(long seed) {

        this.wrapperAPI = AetherialDrift.getInstance().getWrapperApi();

        Random masterRandom = new Random(seed);

        this.lowTierMaterialNoise = new FastNoiseLite(masterRandom.nextInt());
        this.lowTierMaterialNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        this.lowTierMaterialNoise.SetFrequency(0.06f);

        this.highTierMaterialNoise = new FastNoiseLite(masterRandom.nextInt());
        this.highTierMaterialNoise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        this.highTierMaterialNoise.SetFrequency(0.1f);
        this.highTierMaterialNoise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
        this.highTierMaterialNoise.SetCellularReturnType(FastNoiseLite.CellularReturnType.CellValue);
    }

    public void generateIsland(BridgingIsland island, List<Player> players, List<RegeneratingResource> resourceList) {
        int height = island.getHeight();

        for (int yOffset = -height; yOffset <= 0; yOffset++) {
            final int currentYOffset = yOffset;
            long layerDelay = (long) (height + yOffset) * 15L;

            Bukkit.getScheduler().runTaskLater(AetherialDrift.getInstance(), () -> {
                animateLayer(island, currentYOffset, players);

                if (currentYOffset == 0) {
                    Bukkit.getScheduler().runTaskLater(AetherialDrift.getInstance(), () ->
                            populateIsland(island, resourceList), (height * 15L) + 20L);
                }
            }, layerDelay);

        }
    }

    private void animateLayer(BridgingIsland island, int yOffset, List<Player> players) {
        Location center = island.getCenter();
        int radius = island.getRadius();
        int height = island.getHeight();

        double bottomRadius = radius * (1.0 - (double) Math.abs(yOffset) / height);

        double currentRadius;
        if (yOffset == 0) currentRadius = radius;
        else currentRadius = bottomRadius;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x*x + z*z > currentRadius*currentRadius) continue;

                Location blockLoc = center.clone().add(x, yOffset, z);

                Bukkit.getScheduler().runTaskLater(AetherialDrift.getInstance(), () -> {
                    blockLoc.getWorld().playEffect(blockLoc, Effect.STEP_SOUND, wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.ENDER_STONE).getId());
                    blockLoc.getBlock().setType(wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.ENDER_STONE));
                }, 10L);
            }
        }
    }

    private void populateIsland(BridgingIsland island, List<RegeneratingResource> resourceList) {

        Location center = island.getCenter();
        int radius = island.getRadius();
        World world = center.getWorld();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x*x + z*z > radius*radius) continue;

                Block topBlock = center.clone().add(x, 0, z).getBlock();
                if(topBlock.getType() == wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.ENDER_STONE)) {
                    topBlock.setType(wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.GRASS_BLOCK));
                }
            }
        }

        if (island.getResourceType() == ResourceType.WOOD) {
            createWoodPile(center.clone().add(3, 1, 0), resourceList);
            createWoodPile(center.clone().add(-2, 1, 3), resourceList);
            createWoodPile(center.clone().add(-1, 1, -3), resourceList);
        } else {
            int height = island.getHeight();
            Material oreType = island.getResourceType().getOreMaterial();
            long respawnTime;

            switch (island.getResourceType()) {
                case GOLD: respawnTime = 20; break;
                case DIAMOND: respawnTime = 40; break;
                default: respawnTime = 10; break;
            }

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    for (int y = -height; y < 0; y++) {
                        Location blockLoc = center.clone().add(x, y, z);
                        if (world.getBlockAt(blockLoc).getType() == wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.ENDER_STONE)) {

                            boolean shouldSpawn = false;

                            switch (island.getResourceType()) {
                                case IRON:
                                case DIAMOND:
                                    if (highTierMaterialNoise.GetNoise((float) blockLoc.getX(), (float) blockLoc.getY(), (float) blockLoc.getZ()) < -0.8) {
                                        shouldSpawn = true;
                                    }
                                    break;
                                case GOLD:
                                    if (highTierMaterialNoise.GetNoise((float) blockLoc.getX(), (float) blockLoc.getY(), (float) blockLoc.getZ()) < -0.7) {
                                        shouldSpawn = true;
                                    }
                                    break;
                                default:
                                    if (lowTierMaterialNoise.GetNoise((float) blockLoc.getX(), (float) blockLoc.getY(), (float) blockLoc.getZ()) > 0.5) {
                                        shouldSpawn = true;
                                    }
                                    break;
                            }

                            if (shouldSpawn) {
                                world.getBlockAt(blockLoc).setType(oreType);
                                resourceList.add(new RegeneratingResource(blockLoc, oreType, respawnTime));
                            }
                        }
                    }
                }
            }
        }
    }

    private void createWoodPile(Location center, List<RegeneratingResource> resourceList) {
        World world = center.getWorld();
        long respawnTime = 45;

        byte logData = 4;

        for (int x = -2; x <= 2; x++) {
            Location logLoc = center.clone().add(x, 0, -1);
            Block block = world.getBlockAt(logLoc);
            block.setType(wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.OAK_LOG));
            block.setData(logData);

            resourceList.add(new RegeneratingResource(logLoc, wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.OAK_LOG), respawnTime, logData));
        }

        for (int x = -2; x <= 2; x++) {
            Location logLoc = center.clone().add(x, 0, 1);
            Block block = world.getBlockAt(logLoc);
            block.setType(wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.OAK_LOG));
            block.setData(logData);

            resourceList.add(new RegeneratingResource(logLoc, wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.OAK_LOG), respawnTime, logData));
        }

        for (int x = -2; x <= 2; x++) {
            Location logLoc = center.clone().add(x, 1, 0);
            Block block = world.getBlockAt(logLoc);
            block.setType(wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.OAK_LOG));
            block.setData(logData);

            resourceList.add(new RegeneratingResource(logLoc, wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.OAK_LOG), respawnTime, logData));
        }
    }
}