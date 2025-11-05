package fr.arnaud.aetherialdrift.game.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePlayer {

    private final UUID uuid;
    private Team team;

    public GamePlayer(Player player) {
        this.uuid = player.getUniqueId();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}