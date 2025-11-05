package fr.arnaud.aetherialdrift;

import fr.arnaud.aetherialdrift.api.BukkitWrapperAPI;
import fr.arnaud.aetherialdrift.commands.DriftCommand;
import fr.arnaud.aetherialdrift.game.manager.ArenaManager;
import fr.arnaud.aetherialdrift.listeners.GameListener;
import fr.arnaud.aetherialdrift.api.NMSHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class AetherialDrift extends JavaPlugin {

    public static final int MIN_PLAYERS = 2;

    private static AetherialDrift instance;

    private NMSHandler nmsHandler;
    private BukkitWrapperAPI wrapperAPI;
    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("AetherialDrift is loading up...");

        if (!setupImplementations()) {
            getLogger().severe("----------------------------------------------------");
            getLogger().severe("AetherialDrift could not find a compatible implementation for this server version.");
            getLogger().severe("This version of AetherialDrift is not compatible with your server.");
            getLogger().severe("Disabling plugin.");
            getLogger().severe("----------------------------------------------------");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Successfully hooked into NMS version.");

        arenaManager = new ArenaManager(this);

        getServer().getPluginManager().registerEvents(new GameListener(), this);

        getCommand("drift").setExecutor(new DriftCommand(arenaManager));

        getLogger().info("Aetherial Drift has been successfully enabled!");
    }

    private boolean setupImplementations() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            getLogger().severe("Could not determine server version.");
            return false;
        }

        getLogger().info("Detected server version: " + version);

        try {
            String nmsHandlerClassName = "fr.arnaud.aetherialdrift.nms." + version + ".NMSHandler_" + version;
            String apiWrapperClassName = "fr.arnaud.aetherialdrift.api." + version + ".Wrapper_" + version;

            Class<?> nmsHandlerClass = Class.forName(nmsHandlerClassName);
            Class<?> apiWrapperClass = Class.forName(apiWrapperClassName);

            this.nmsHandler = (NMSHandler) nmsHandlerClass.getConstructor(AetherialDrift.class).newInstance(this);
            this.wrapperAPI = (BukkitWrapperAPI) apiWrapperClass.getConstructor().newInstance();

            getLogger().info("Successfully loaded implementations for " + version);
            return true;

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Could not find implementations for " + version, e);
            return false;
        }
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public NMSHandler getNmsHandler() {
        return nmsHandler;
    }

    public BukkitWrapperAPI getWrapperApi() {
        return wrapperAPI;
    }

    public static AetherialDrift getInstance() {
        return instance;
    }
}
