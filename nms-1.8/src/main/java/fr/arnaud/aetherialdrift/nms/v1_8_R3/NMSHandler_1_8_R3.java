package fr.arnaud.aetherialdrift.nms.v1_8_R3;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.api.NMSHandler;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class NMSHandler_1_8_R3 implements NMSHandler {

    private final AetherialDrift plugin;
    private static final AtomicInteger entityIdCounter = new AtomicInteger(Integer.MAX_VALUE);

    public NMSHandler_1_8_R3(AetherialDrift plugin) {
        this.plugin = plugin;
    }

    @Override
    public int spawnHologram(Location location, String text, Collection<Player> players) {

        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        EntityArmorStand armorStand = new EntityArmorStand(nmsWorld);

        armorStand.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);

        armorStand.setCustomName(text);
        armorStand.setCustomNameVisible(true);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);

        int entityId = entityIdCounter.decrementAndGet();

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armorStand);

        try {
            java.lang.reflect.Field idField = packet.getClass().getDeclaredField("a");
            idField.setAccessible(true);
            idField.setInt(packet, entityId);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not spawn hologram", e);
        }

        sendPacket(players, packet);

        return entityId;
    }

    @Override
    public void updateHologram(int entityId, String text, Collection<Player> players) {
        DataWatcher watcher = new DataWatcher(null);
        watcher.a(2, text);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityId, watcher, true);

        sendPacket(players, packet);
    }

    @Override
    public void removeNMSEntity(int entityId, Collection<Player> players) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityId);

        sendPacket(players, packet);
    }

    @Override
    public void sendActionBar(Player player, String message){
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc,(byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
    }

    private void sendPacket(Collection<Player> players, Packet<?> packet) {
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}
