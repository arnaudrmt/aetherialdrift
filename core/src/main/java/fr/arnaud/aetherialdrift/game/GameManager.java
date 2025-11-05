package fr.arnaud.aetherialdrift.game;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.game.data.Arena;
import fr.arnaud.aetherialdrift.game.data.Team;
import fr.arnaud.aetherialdrift.game.manager.PlayerManager;
import fr.arnaud.aetherialdrift.game.manager.TeamManager;
import fr.arnaud.aetherialdrift.game.state.EndState;
import fr.arnaud.aetherialdrift.game.state.GameState;
import fr.arnaud.aetherialdrift.game.state.LobbyState;
import fr.arnaud.aetherialdrift.world.WorldManager;
import org.bukkit.event.Listener;

public class GameManager implements Listener {

    private final AetherialDrift plugin;
    private final GameStateManager stateManager;

    private final Arena arena;
    private final WorldManager worldManager;
    private final PlayerManager playerManager;
    private final TeamManager teamManager;

    public GameManager(AetherialDrift plugin, String gameId) {
        this.plugin = plugin;
        this.stateManager = new GameStateManager(plugin);

        this.arena = new Arena(gameId);
        this.worldManager = new WorldManager();
        this.playerManager = new PlayerManager(this.arena);
        this.teamManager = new TeamManager(this.playerManager);

        startGame();
    }

    public boolean isJoinable() {
        return stateManager.getCurrentState() instanceof LobbyState;
    }

    public void endGame(Team winningTeam) {
        if (stateManager.getCurrentState() instanceof EndState) {
            return;
        }
        changeState(new EndState(this, winningTeam));
    }

    public void startGame() {
        changeState(new LobbyState(this));
    }

    public void changeState(GameState newState) {
        stateManager.start(newState);
    }

    public void shutdown(){
        stateManager.stop();
        plugin.getArenaManager().removeGame(this.arena.getId());
    }

    public GameState getCurrentState() {
        return stateManager.getCurrentState();
    }

    public AetherialDrift getPlugin() { return plugin; }
    public Arena getArena() { return arena; }
    public WorldManager getWorldManager() { return worldManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public TeamManager getTeamManager() { return teamManager; }
}