package cc.meteormc.packetlistener.objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the stage of a packet during the network communication process.
 *
 * @author Meteor23333
 */
public enum PacketStage {
    /**
     * The handshake stage.
     * Occurs when the client has just established a connection with the server.
     */
    HANDSHAKE("Handshaking"),
    /**
     * The login stage.
     * Occurs after the handshake, when the client sends its profile to the server and awaits authentication.
     */
    LOGIN("Login"),
    /**
     * The play stage.
     * Covers all packet exchanges during gameplay after the client has successfully logged in.
     */
    PLAY("Play"),
    /**
     * The query stage.
     * Occurs when the client requests server information (e.g., during a server list ping).
     */
    QUERY("Status"),
    /**
     * An unknown or unspecified stage.
     * Used when the packet class does not explicitly indicate its stage,
     * such as in newer Spigot mappings where class names no longer contain stage information.
     */
    UNKNOWN("");

    private final String spigotName;
    private static final Map<String, PacketStage> BY_SPIGOTNAME = new HashMap<>();

    static {
        for (PacketStage stage : PacketStage.values()) {
            BY_SPIGOTNAME.put(stage.spigotName, stage);
        }
    }

    PacketStage(String spigotName) {
        this.spigotName = spigotName;
    }

    public static @Nullable PacketStage getBySpigotName(@NotNull String spigotName) {
        return BY_SPIGOTNAME.getOrDefault(spigotName, UNKNOWN);
    }
}
