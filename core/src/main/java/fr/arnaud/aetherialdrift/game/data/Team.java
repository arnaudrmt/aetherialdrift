package fr.arnaud.aetherialdrift.game.data;

import org.bukkit.ChatColor;

public enum Team {
    SOL("Sol", ChatColor.GOLD),
    LUNA("Luna", ChatColor.AQUA);

    private final String displayName;
    private final ChatColor color;

    Team(String displayName, ChatColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }
}
