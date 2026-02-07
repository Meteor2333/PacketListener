package cc.meteormc.packetlistener;

import cc.meteormc.packetlistener.internal.NettyPipelineInjector;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Main class of PacketListener.
 *
 * @author Meteor23333
 */
public class PacketListener {
    private static Plugin owner;

    /**
     * Initializes the PacketListener.
     * <p>
     * This method can be called during either the {@link JavaPlugin#onLoad()} or {@link JavaPlugin#onEnable()} phase.
     * If invoked during the plugin loading phase, it is capable of capturing all packets
     * (though technically, listener registration can only happen after the plugin is enabled).
     *
     * @param owner the plugin using the PacketListener
     */
    public static void init(Plugin owner) {
        PacketListener.owner = owner;
        NettyPipelineInjector.inject();
    }

    /**
     * Gets the Logger instance.
     *
     * @return the Logger instance
     */
    public static @NotNull Logger getLogger() {
        return owner.getLogger();
    }

    /**
     * This class cannot be instantiated!
     */
    private PacketListener() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
