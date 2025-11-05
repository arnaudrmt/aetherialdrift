package fr.arnaud.aetherialdrift.game.state;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.game.GameManager;
import fr.arnaud.aetherialdrift.game.data.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class EndState implements GameState {

    private final GameManager gameManager;
    private final Team winningTeam;

    public EndState(GameManager gameManager, Team winningTeam) {
        this.gameManager = gameManager;
        this.winningTeam = winningTeam;
    }

    @Override
    public void onEnter() {
        AetherialDrift.getInstance().getLogger().info("Entering END game state.");

        String winnerName = (winningTeam != null) ? winningTeam.getColor() + "Team " + winningTeam.getDisplayName() : "Nobody";

        broadcast(ChatColor.GOLD + "========================================");
        broadcast(" ");
        broadcast(ChatColor.YELLOW + "            GAME OVER!");
        broadcast("         " + winnerName + ChatColor.YELLOW + " has won the game!");
        broadcast(" ");
        broadcast(ChatColor.GOLD + "========================================");
    }

    @Override
    public void onUpdate(long elapsedTime) {
        int countdown = 10;

        if (elapsedTime == countdown) {
            broadcast(ChatColor.GRAY + "Teleporting to lobby...");
            gameManager.getPlayerManager().getOnlineBukkitPlayers().forEach(player ->
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));

            gameManager.shutdown();
        }
    }

    @Override
    public void onExit() {
        Bukkit.getScheduler().runTaskLater(AetherialDrift.getInstance(), () ->
                gameManager.getWorldManager().deleteWorld(gameManager.getArena().getWorld()), 40L);
    }

    @Override
    public String getName() {
        return "END";
    }

    private void broadcast(String message) {
        gameManager.getPlayerManager().getOnlineBukkitPlayers().forEach(p -> p.sendMessage(message));
    }
}