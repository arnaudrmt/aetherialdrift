package fr.arnaud.aetherialdrift.game.state;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.game.GameManager;
import fr.arnaud.aetherialdrift.game.data.Arena;
import fr.arnaud.aetherialdrift.game.data.Team;
import fr.arnaud.aetherialdrift.game.state.GameState;
import fr.arnaud.aetherialdrift.game.world.WorldConstants;
import fr.arnaud.aetherialdrift.islands.ResourceType;
import fr.arnaud.aetherialdrift.world.BridgingIsland;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Random;

public class ActiveGameState implements GameState {

    private final GameManager gameManager;
    private final Arena arena;
    private final Random random = new Random();

    private int islandSpawnTimer = 30;
    private int currentIslandLayer = 0;

    public ActiveGameState(GameManager gameManager, Arena arena) {
        this.gameManager = gameManager;
        this.arena = arena;
    }

    @Override
    public void onEnter() {
        AetherialDrift.getInstance().getLogger().info("Entering ACTIVE game state.");
        broadcast(ChatColor.GOLD + "Let the game begin! Destroy the enemy Aether Core!");
    }

    @Override
    public void onUpdate(long elapsedTime) {

        if (currentIslandLayer <= 3) {
            if (islandSpawnTimer <= 0) {
                spawnBridgingIslands();
                islandSpawnTimer = 240;
                currentIslandLayer++;
            }

            if (islandSpawnTimer == 10) {
                broadcast(ChatColor.AQUA + "New islands will form in 10 seconds!");
            }

            islandSpawnTimer--;
        }
    }

    @Override
    public void onExit() {
        AetherialDrift.getInstance().getLogger().info("Exiting ACTIVE game state.");
    }

    @Override
    public String getName() {
        return "ACTIVE";
    }

    private void spawnBridgingIslands() {
        ResourceType type;
        switch (currentIslandLayer) {
            case 0: type = ResourceType.WOOD; break;
            case 1: type = ResourceType.IRON; break;
            case 2: type = ResourceType.GOLD; break;
            default: type = ResourceType.DIAMOND; break;
        }

        Location solBase = arena.getTeamSpawns().get(Team.SOL);
        Location lunaBase = arena.getTeamSpawns().get(Team.LUNA);

        Vector solBase2D = new Vector(solBase.getX(), 0, solBase.getZ());
        Vector lunaBase2D = new Vector(lunaBase.getX(), 0, lunaBase.getZ());

        Vector directionLunaToSol = solBase2D.clone().subtract(lunaBase2D.clone()).normalize();
        Vector directionSolToLuna = lunaBase2D.clone().subtract(solBase2D.clone()).normalize();

        double baseDistance = WorldConstants.ISLAND_RADIUS + 20.0;

        double distance = baseDistance + (currentIslandLayer * 40.0);

        Location solIslandPoint = solBase.clone().add(directionSolToLuna.clone().multiply(distance));
        Location lunaIslandPoint = lunaBase.clone().add(directionLunaToSol.clone().multiply(distance));

        Vector sideVector = new Vector(-directionLunaToSol.getZ(), 0, directionLunaToSol.getX()).normalize();

        double solOffsetStrength = (random.nextDouble() - 0.5) * 40.0;
        Vector solFinalOffset = sideVector.clone().multiply(solOffsetStrength);
        solIslandPoint.add(solFinalOffset);

        double lunaOffsetStrength = (random.nextDouble() - 0.5) * 40.0;
        Vector lunaFinalOffset = sideVector.clone().multiply(lunaOffsetStrength);
        lunaIslandPoint.add(lunaFinalOffset);

        solIslandPoint.setY(WorldConstants.ISLAND_Y_LEVEL);
        lunaIslandPoint.setY(WorldConstants.ISLAND_Y_LEVEL);

        BridgingIsland solIsland = new BridgingIsland(solIslandPoint, 10, 8, type);
        BridgingIsland lunaIsland = new BridgingIsland(lunaIslandPoint, 10, 8, type);

        arena.getIslandGenerator().generateIsland(solIsland, gameManager.getPlayerManager().getOnlineBukkitPlayers(), arena.getRegeneratingResources());
        arena.getIslandGenerator().generateIsland(lunaIsland, gameManager.getPlayerManager().getOnlineBukkitPlayers(), arena.getRegeneratingResources());

        broadcast(ChatColor.GOLD + "New " + type.name() + " islands have formed in the Drift!");
    }

    private void broadcast(String message) {
        gameManager.getPlayerManager().getOnlineBukkitPlayers().forEach(p -> p.sendMessage(message));
    }
}