package fr.arnaud.aetherialdrift.world;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.api.BukkitWrapperAPI;
import fr.arnaud.aetherialdrift.game.world.WorldConstants;
import fr.arnaud.aetherialdrift.utils.FastNoiseLite;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class IslandChunkGenerator extends ChunkGenerator {

    private final BukkitWrapperAPI wrapperAPI;
    private final FastNoiseLite detailNoise;
    private final FastNoiseLite materialNoise;
    private final Location solIslandCenter;
    private final Location lunaIslandCenter;

    public IslandChunkGenerator(long seed, Location solIslandCenter, Location lunaIslandCenter) {
        this.wrapperAPI = AetherialDrift.getInstance().getWrapperApi();
        this.solIslandCenter = solIslandCenter;
        this.lunaIslandCenter = lunaIslandCenter;

        this.detailNoise = new FastNoiseLite((int) seed);
        this.detailNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        this.detailNoise.SetFrequency(0.025f);

        this.materialNoise = new FastNoiseLite((int) (seed + 1));
        this.materialNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        this.materialNoise.SetFrequency(0.08f);
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;

                double distToSol = Math.sqrt(Math.pow(worldX - solIslandCenter.getX(), 2) + Math.pow(worldZ - solIslandCenter.getZ(), 2));
                double distToLuna = Math.sqrt(Math.pow(worldX - lunaIslandCenter.getX(), 2) + Math.pow(worldZ - lunaIslandCenter.getZ(), 2));
                double nearestDist = Math.min(distToSol, distToLuna);

                for (int y = 0; y < WorldConstants.ISLAND_Y_LEVEL + 15; y++) {

                    double maxRadiusAtThisY = calculateRadiusAtHeight(y);
                    double noiseModifier = detailNoise.GetNoise(worldX, y, worldZ) * 8;

                    if (nearestDist < maxRadiusAtThisY + noiseModifier) {
                        chunkData.setBlock(x, y, z, getMaterialFor(worldX, y, worldZ));
                    }
                }
            }
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Random grassRandom = new Random((chunkX * 16L + x) * 101L + (chunkZ * 16L + z) * 151L);

                for (int y = WorldConstants.ISLAND_Y_LEVEL + 15; y > 0; y--) {
                    Material currentBlock = chunkData.getType(x, y, z);
                    Material blockAbove = chunkData.getType(x, y + 1, z);

                    if (currentBlock != Material.AIR && blockAbove == Material.AIR) {
                        chunkData.setBlock(x, y, z, wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.MYCELIUM));
                        chunkData.setBlock(x, y - 1, z, Material.DIRT);
                        chunkData.setBlock(x, y - 2, z, wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.ENDER_STONE));

                        if (grassRandom.nextInt(10) < 1) {
                            chunkData.setBlock(x, y + 1, z, Material.RED_MUSHROOM);
                        }

                        break;
                    }
                }
            }
        }

        return chunkData;
    }

    private Material getMaterialFor(int x, int y, int z) {
        double noiseValue = materialNoise.GetNoise(x, y, z);

        if (y < 10) {
            return Material.OBSIDIAN;
        }
        if (noiseValue > 0.65) {
            return Material.SEA_LANTERN;
        }
        if (noiseValue > 0.4) {
            return Material.PRISMARINE;
        }
        return wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.ENDER_STONE);
    }

    private double calculateRadiusAtHeight(int currentY) {
        int coreY = WorldConstants.ISLAND_Y_LEVEL;
        double maxRadius = WorldConstants.ISLAND_RADIUS;

        if (currentY < coreY) {
            int verticalDist = coreY - currentY;
            double falloffRatio = (double) verticalDist / coreY;
            double radiusMultiplier = 1.0 - Math.pow(falloffRatio, 2);
            return maxRadius * radiusMultiplier;
        } else {
            int topHeightRange = 10;
            int verticalDist = currentY - coreY;
            double falloffRatio = (double) verticalDist / topHeightRange;
            double radiusMultiplier = 1.0 - falloffRatio;
            return maxRadius * Math.max(0, radiusMultiplier);
        }
    }
}