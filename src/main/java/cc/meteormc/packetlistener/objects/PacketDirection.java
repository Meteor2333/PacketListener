package cc.meteormc.packetlistener.objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the direction of a packet during the network communication process.
 *
 * @author Meteor23333
 */
public enum PacketDirection {
    /**
     * Represents packets that are sent from the server to the client.
     */
    CLIENTBOUND("Out"),
    /**
     * Represents packets that are sent from the client to the server.
     */
    SERVERBOUND("In");

    private final String legacyName;
    private static final Map<String, PacketDirection> BY_SPIGOTNAME = new HashMap<>();

    static {
        for (PacketDirection direction : PacketDirection.values()) {
            String name = direction.name();
            BY_SPIGOTNAME.put(name.charAt(0) + name.substring(1).toLowerCase(), direction);
            BY_SPIGOTNAME.put(direction.legacyName, direction);
        }
    }

    PacketDirection(String legacyName) {
        this.legacyName = legacyName;
    }

    public static @Nullable PacketDirection getBySpigotName(@NotNull String spigotName) {
        return BY_SPIGOTNAME.get(spigotName);
    }
}
