package fr.arnaud.aetherialdrift.game.manager;

import fr.arnaud.aetherialdrift.game.data.GamePlayer;
import fr.arnaud.aetherialdrift.game.data.Team;
import fr.arnaud.aetherialdrift.game.manager.PlayerManager;
import fr.arnaud.aetherialdrift.islands.ResourceType;

import java.util.*;

public class TeamManager {

    private final PlayerManager playerManager;

    private final Map<Team, Map<ResourceType, Integer>> teamResources = new EnumMap<>(Team.class);

    public TeamManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        for (Team team : Team.values()) {
            teamResources.put(team, new EnumMap<>(ResourceType.class));
        }
    }

    public void assignTeams() {
        List<GamePlayer> playersToAssign = new ArrayList<>(playerManager.getAllGamePlayers());
        Collections.shuffle(playersToAssign);

        int teamIndex = 0;
        for (GamePlayer gamePlayer : playersToAssign) {
            Team team = Team.values()[teamIndex];
            gamePlayer.setTeam(team);

            teamIndex = (teamIndex + 1) % Team.values().length;
        }
    }

    public int getResourceCount(Team team, ResourceType type) {
        return teamResources.get(team).getOrDefault(type, 0);
    }

    public void addResources(Team team, ResourceType type, int amount) {
        teamResources.get(team).merge(type, amount, Integer::sum);
    }
}