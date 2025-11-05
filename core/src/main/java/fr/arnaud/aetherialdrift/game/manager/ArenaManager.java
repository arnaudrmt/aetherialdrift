package fr.arnaud.aetherialdrift.game.manager;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.game.GameManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ArenaManager {

    private final AetherialDrift plugin;
    private final Map<String, GameManager> activeGames = new HashMap<>();
    private final AtomicInteger gameIdCounter = new AtomicInteger(0);

    public ArenaManager(AetherialDrift plugin) {
        this.plugin = plugin;
    }

    public GameManager createGame() {
        String gameId = "game-" + gameIdCounter.incrementAndGet();
        GameManager newGame = new GameManager(plugin, gameId);
        activeGames.put(gameId, newGame);
        plugin.getLogger().info("Created new game instance: " + gameId);
        return newGame;
    }

    public void removeGame(String gameId) {
        activeGames.remove(gameId);
        plugin.getLogger().info("Removed game instance: " + gameId);
    }

    public Optional<GameManager> findAvailableGame() {
        return activeGames.values().stream()
                .filter(GameManager::isJoinable)
                .findFirst();
    }

    public Optional<GameManager> getGameManager(Player player) {
        return activeGames.values().stream()
                .filter(game -> game.getPlayerManager().getGamePlayer(player.getUniqueId()).isPresent())
                .findFirst();
    }
}