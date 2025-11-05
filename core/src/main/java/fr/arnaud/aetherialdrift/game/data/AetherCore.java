package fr.arnaud.aetherialdrift.game.data;

import fr.arnaud.aetherialdrift.AetherialDrift;
import org.bukkit.*;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class AetherCore {

    private final Team team;
    private final Location location;
    private int health;

    private UUID crystalUUID;
    private int hologramEntityId;

    private static final int MAX_HEALTH = 100;

    public AetherCore(Team team, Location location) {
        this.team = team;
        this.location = location;
        this.health = MAX_HEALTH;
    }

    public void spawn(Collection<Player> players) {

        EnderCrystal crystal = location.getWorld().spawn(location, EnderCrystal.class);
        this.crystalUUID = crystal.getUniqueId();

        location.clone().subtract(0, 1, 0).getBlock().setType(Material.BEDROCK);

        Location hologramLocation = location.clone().add(0, 0.3, 0);
        Bukkit.getScheduler().runTaskLater(AetherialDrift.getInstance(), () ->
            hologramEntityId = AetherialDrift.getInstance().getNmsHandler().spawnHologram(hologramLocation, getFormattedHealth(), players)
                , 10L);
    }

    public int damage(Collection<Player> players) {
        if (this.health <= 0) return 0;

        this.health--;

        AetherialDrift.getInstance().getNmsHandler().updateHologram(hologramEntityId, getFormattedHealth(), players);
        location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());

        return this.health;
    }

    public void destroy(Collection<Player> players) {
        Entity crystal = null;
        for (Entity entity : location.getWorld().getEntities()) {
            if (entity.getUniqueId().equals(this.crystalUUID)) {
                crystal = entity;
                break;
            }
        }

        if (crystal != null) {
            crystal.remove();
        }

        AetherialDrift.getInstance().getNmsHandler().removeNMSEntity(hologramEntityId, players);

        location.getWorld().createExplosion(location, 0F);
    }

    public Location getCoreBlockLocation() {
        return location.clone().subtract(0, 1, 0);
    }

    public int getHealth() {
        return health;
    }

    public static int getMaxHealth() {
        return MAX_HEALTH;
    }

    private String getFormattedHealth() {
        int totalBars = 12;
        char barChar = '█';

        double healthPercent = (double) this.health / MAX_HEALTH;

        ChatColor barColor;
        if (healthPercent > 0.66) {
            barColor = ChatColor.GREEN;
        } else if (healthPercent > 0.33) {
            barColor = ChatColor.YELLOW;
        } else {
            barColor = ChatColor.RED;
        }

        int filledBars = (int) (totalBars * healthPercent);
        int emptyBars = totalBars - filledBars;

        StringBuilder sb = new StringBuilder();

        sb.append(ChatColor.DARK_GRAY).append("[");
        sb.append(barColor);
        for (int i = 0; i < filledBars; i++) {
            sb.append(barChar);
        }

        sb.append(ChatColor.GRAY);
        for (int i = 0; i < emptyBars; i++) {
            sb.append(barChar);
        }

        sb.append(ChatColor.DARK_GRAY).append("]");

        return sb.toString();
    }

    public UUID getCrystalUUID() {
        return crystalUUID;
    }

    public Team getTeam() {
        return team;
    }
}