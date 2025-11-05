package fr.arnaud.aetherialdrift.commands;

import fr.arnaud.aetherialdrift.game.GameManager;
import fr.arnaud.aetherialdrift.game.manager.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DriftCommand implements CommandExecutor {

    private final ArenaManager arenaManager;

    public DriftCommand(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Wrong syntaxe: /drift <joni|leave>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equalsIgnoreCase("join")) {
            GameManager gameToJoin = arenaManager.findAvailableGame().orElseGet(arenaManager::createGame);
            gameToJoin.getPlayerManager().addPlayer(player);
            player.sendMessage(ChatColor.GREEN + "You have successfully joined the queue for the next game.");
            return true;
        }

        if (subCommand.equalsIgnoreCase("leave")) {
            arenaManager.getGameManager(player).ifPresent(game -> {
                game.getPlayerManager().removePlayer(player);
            });
            player.sendMessage(ChatColor.RED + "You have left the queue.");
            return true;
        }

        player.sendMessage(ChatColor.RED + "Wrong syntaxe: /drift <joni|leave>");
        return true;
    }
}
