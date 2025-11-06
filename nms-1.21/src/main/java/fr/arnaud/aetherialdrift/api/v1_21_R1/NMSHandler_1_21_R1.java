package fr.arnaud.aetherialdrift.api.v1_21_R1;

import fr.arnaud.aetherialdrift.AetherialDrift;
import fr.arnaud.aetherialdrift.api.NMSHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class NMSHandler_1_21_R1 implements NMSHandler {

    private final AetherialDrift plugin;
    private static final AtomicInteger entityIdCounter = new AtomicInteger(Integer.MAX_VALUE);

    public NMSHandler_1_21_R1(AetherialDrift plugin) {
        this.plugin = plugin;
    }

    @Override
    public int spawnHologram(Location location, String text, Collection<Player> players) {

        ServerLevel nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        ArmorStand armorStand = new ArmorStand(nmsWorld, location.getX(), location.getY(), location.getZ());
        armorStand.setCustomName(Component.literal(text));
        armorStand.setCustomNameVisible(true);
        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);

        int entityId = entityIdCounter.decrementAndGet();

        try {
            java.lang.reflect.Field idField = Entity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.setInt(armorStand, entityId);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not set entity ID for armorstand.", e);
        }

        Packet<?> packet = null;
        try {
            Constructor<ClientboundAddEntityPacket> constructor =
                    ClientboundAddEntityPacket.class.getDeclaredConstructor(Entity.class, int.class);
            constructor.setAccessible(true);
            packet = constructor.newInstance(armorStand, 2);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not create armor stand entity packet.", e);
        }

        if (packet != null) {
            sendPacket(players, packet);
        }

        return entityId;
    }

    @Override
    public void updateHologram(int entityId, String text, Collection<Player> players) {
        Component nameComponent = Component.literal(text);

        Packet<?> packet = null;

        try {
            for (World world : Bukkit.getWorlds()) {
                ServerLevel nmsWorld = ((CraftWorld) world).getHandle();
                Entity entity = nmsWorld.getEntity(entityId);
                if (entity != null) {
                    entity.setCustomName(nameComponent);

                    SynchedEntityData data = entity.getEntityData();

                    List<SynchedEntityData.DataValue<?>> metadataList = new ArrayList<>();
                    java.lang.reflect.Field entriesField = SynchedEntityData.class.getDeclaredField("entries");
                    entriesField.setAccessible(true);
                    Map<Integer, SynchedEntityData.DataValue<?>> entries =
                            (Map<Integer, SynchedEntityData.DataValue<?>>) entriesField.get(data);

                    metadataList.addAll(entries.values());

                    packet = new ClientboundSetEntityDataPacket(entity.getId(), metadataList);
                    break;
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not create armorstand update packet.", e);
        }

        if (packet != null) {
            sendPacket(players, packet);
        }
    }

    @Override
    public void removeNMSEntity(int entityId, Collection<Player> players) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(new int[]{entityId});

        sendPacket(players, packet);
    }

    @Override
    public void sendActionBar(Player player, String message) {
        Component component = Component.literal(message);

        ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(component, true);

        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    private void sendPacket(Collection<Player> players, Packet<?> packet) {
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().connection.sendPacket(packet);
        }
    }
}
