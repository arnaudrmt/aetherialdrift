package fr.arnaud.aetherialdrift.game.state;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class LobbyState implements GameState, Listener {

    private final AetherialDrift plugin;
    private final GameManager gameManager;

    private static final int LOBBY_COUNTDOWN_SECONDS = 5;
    private int countdown = LOBBY_COUNTDOWN_SECONDS;

    public LobbyState(GameManager gameManager) {
        this.plugin = AetherialDrift.getInstance();
        this.gameManager = gameManager;
    }

    @Override
    public void onEnter() {
        countdown = LOBBY_COUNTDOWN_SECONDS;
        plugin.getLogger().info("Entering LOBBY state. Waiting for players...");
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onUpdate(long elapsedTime) {

        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        if(onlinePlayers < AetherialDrift.MIN_PLAYERS) {
            countdown = LOBBY_COUNTDOWN_SECONDS;
            return;
        }

        countdown--;

        Bukkit.getOnlinePlayers()
                .forEach(player ->
                        plugin.getNmsHandler().sendActionBar(player, ChatColor.RED + "Game starting in " + countdown + " seconds!"));

        if(countdown <= 0) {
            gameManager.changeState(new LoadingState(gameManager));
        }
    }

    @Override
    public void onExit() {
        HandlerList.unregisterAll(this);
        plugin.getLogger().info("Exiting LOBBY state");
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName().replace("State", "").toUpperCase();
    }
}