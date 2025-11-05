package fr.arnaud.aetherialdrift.game.state;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.game.GameManager;
import fr.arnaud.aetherialdrift.game.state.ActiveGameState;
import fr.arnaud.aetherialdrift.game.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LoadingState implements GameState {

    private final GameManager gameManager;

    public LoadingState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void onEnter() {
        broadcast(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "PREPARE YOURSELF! " + ChatColor.GOLD + "The island is generating...");

        World world = gameManager.getWorldManager().createArenaWorld(gameManager.getArena());
        gameManager.getArena().setWorld(world);

        gameManager.getWorldManager().prepareArena(gameManager);
        gameManager.getTeamManager().assignTeams();
        gameManager.getPlayerManager().teleportAllToSpawns();

        AetherialDrift.getInstance().getLogger().info("Arena prepared. Transitioning to ActiveGameState.");

        gameManager.changeState(new ActiveGameState(gameManager, gameManager.getArena()));
    }

    @Override
    public void onUpdate(long elapsedTime) {
    }

    @Override
    public void onExit() {}

    @Override
    public String getName() {
        return "LOADING";
    }

    private void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
}