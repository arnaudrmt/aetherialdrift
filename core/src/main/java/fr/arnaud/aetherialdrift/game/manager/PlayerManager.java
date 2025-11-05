package fr.arnaud.aetherialdrift.game.manager;

import fr.arnaud.aetherialdrift.game.data.Arena;
import fr.arnaud.aetherialdrift.game.data.GamePlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerManager {

    private final Map<UUID, GamePlayer> players = new HashMap<>();
    private final Arena arena;

    public PlayerManager(Arena arena) {
        this.arena = arena;
    }

    public void addPlayer(Player player) {
        players.put(player.getUniqueId(), new GamePlayer(player));
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public Optional<GamePlayer> getGamePlayer(UUID uuid) {
        return Optional.ofNullable(players.get(uuid));
    }

    public Collection<GamePlayer> getAllGamePlayers() {
        return players.values();
    }

    public List<Player> getOnlineBukkitPlayers() {
        return players.values().stream()
                .map(GamePlayer::getBukkitPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void teleportAllToSpawns() {
        if (arena.getWorld() == null || arena.getTeamSpawns().isEmpty()) return;

        for (GamePlayer gamePlayer : players.values()) {
            Player bukkitPlayer = gamePlayer.getBukkitPlayer();
            if (bukkitPlayer != null && gamePlayer.getTeam() != null) {
                Location spawnPoint = arena.getTeamSpawns().get(gamePlayer.getTeam());
                bukkitPlayer.teleport(spawnPoint);
                bukkitPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
                bukkitPlayer.setHealth(20);
                bukkitPlayer.setFoodLevel(20);
            }
        }
    }
}