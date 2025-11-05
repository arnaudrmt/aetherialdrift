package fr.arnaud.aetherialdrift.listeners;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.api.BukkitWrapperAPI;
import fr.arnaud.aetherialdrift.game.GameManager;
import fr.arnaud.aetherialdrift.game.data.AetherCore;
import fr.arnaud.aetherialdrift.game.data.Arena;
import fr.arnaud.aetherialdrift.game.data.GamePlayer;
import fr.arnaud.aetherialdrift.game.state.ActiveGameState;
import fr.arnaud.aetherialdrift.utils.LocationUtils;
import fr.arnaud.aetherialdrift.world.RegeneratingResource;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameListener implements Listener {

    BukkitWrapperAPI wrapperAPI = AetherialDrift.getInstance().getWrapperApi();

    @EventHandler
    public void onCrystalDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if (!(damaged instanceof EnderCrystal) && !(damager instanceof Player)) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) damager;
        UUID crystalUUID = damaged.getUniqueId();

        Optional<GameManager> gameOpt = AetherialDrift.getInstance().getArenaManager().getGameManager(player);
        if (!gameOpt.isPresent()) return;
        GameManager gameManager = gameOpt.get();

        if (!(gameManager.getCurrentState() instanceof ActiveGameState)) return;

        for (AetherCore core : gameManager.getArena().getAetherCores().values()) {
            if (core.getCrystalUUID().equals(crystalUUID)) {
                handleCoreDamage(gameManager, player, core);
                return;
            }
        }
    }

    public void handleCoreDamage(GameManager gameManager, Player damager, AetherCore core) {

        Optional<GamePlayer> gamePlayerOpt = gameManager.getPlayerManager().getGamePlayer(damager.getUniqueId());
        if (!gamePlayerOpt.isPresent()) return;
        GamePlayer gamePlayer = gamePlayerOpt.get();

        List<Player> onlineBukkitPlayers = gameManager.getPlayerManager().getOnlineBukkitPlayers();

        if (gamePlayer.getTeam() == core.getTeam()) {
            damager.sendMessage(ChatColor.RED + "You cannot damage your own team's Aether Core!");
            return;
        }

        int healthBefore = core.getHealth();
        int healthAfter = core.damage(onlineBukkitPlayers);

        damager.playSound(damager.getLocation(), wrapperAPI.getSound(BukkitWrapperAPI.SoundType.NOTE_PLING), 1.0f, 0.8f);

        if (healthAfter <= 0) {
            core.destroy(onlineBukkitPlayers);
            gameManager.endGame(gamePlayer.getTeam());
            return;
        }

        int maxHealth = AetherCore.getMaxHealth();
        String coreName = core.getTeam().getColor() + "Team " + core.getTeam().getDisplayName() + "'s Core" + ChatColor.YELLOW;

        if (healthBefore == maxHealth) {
            broadcast(coreName + " is under attack!", onlineBukkitPlayers);
        } else if (healthBefore > maxHealth * 0.75 && healthAfter <= maxHealth * 0.75) {
            broadcast(coreName + " is at 75% health!", onlineBukkitPlayers);
        } else if (healthBefore > maxHealth * 0.50 && healthAfter <= maxHealth * 0.50) {
            broadcast(coreName + " is at 50% health!", onlineBukkitPlayers);
        } else if (healthBefore > maxHealth * 0.25 && healthAfter <= maxHealth * 0.25) {
            broadcast(coreName + " is at 25% health!", onlineBukkitPlayers);
        } else if (healthBefore > maxHealth * 0.10 && healthAfter <= maxHealth * 0.10) {
            broadcast(coreName + ChatColor.RED + " is at 10% health and is critically damaged!", onlineBukkitPlayers);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Optional<GameManager> gameOpt = AetherialDrift.getInstance().getArenaManager().getGameManager(player);
        if (!gameOpt.isPresent()) {
            if (!player.isOp()) event.setCancelled(true);
            return;
        }
        GameManager gameManager = gameOpt.get();
        Arena arena = gameManager.getArena();

        if (!(gameManager.getCurrentState() instanceof ActiveGameState)) {
            return;
        }

        for (RegeneratingResource resource : arena.getRegeneratingResources()) {
            if (LocationUtils.isSameBlock(resource.getLocation(), block.getLocation()) && !resource.isDepleted()) {
                resource.harvest();
                event.setCancelled(true);
                block.setType(Material.AIR);

                Material brokenType = block.getType();
                ItemStack drop;
                Sound sound;

                if (brokenType == wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.OAK_LOG)) {
                    drop = new ItemStack(wrapperAPI.getMaterial(BukkitWrapperAPI.MaterialType.OAK_LOG), 1);
                    sound = wrapperAPI.getSound(BukkitWrapperAPI.SoundType.DIG_WOOD);
                } else {
                    drop = new ItemStack(brokenType, 1);
                    sound = wrapperAPI.getSound(BukkitWrapperAPI.SoundType.ORB_PICKUP);
                }

                player.getInventory().addItem(drop);
                player.playSound(block.getLocation(), sound, 1.0f, 1.2f);
                return;
            }
        }

        if (!player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    private void broadcast(String message, List<Player> players) {
        players.forEach(player -> player.sendMessage(message));
    }
}