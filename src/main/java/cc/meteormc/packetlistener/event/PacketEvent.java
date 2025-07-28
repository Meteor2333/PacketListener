package cc.meteormc.packetlistener.event;

import cc.meteormc.packetlistener.objects.Packet;
import cc.meteormc.packetlistener.objects.PacketStage;
import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Internal abstract packet event.
 *
 * @author Meteor23333
 */
abstract class PacketEvent extends Event implements Cancellable {
    private final ChannelHandlerContext ctx;
    private final GameProfile profile;
    private final Player player;
    private final Packet packet;
    private boolean cancelled = false;

    /**
     * Creates a {@link PacketEvent}.
     *
     * @param ctx the ctx, representing the handler's context in the pipeline
     * @param profile the profile, available after the client and server have completed the handshake
     * @param player the player, available after the client and server have completed the login process
     * @param packet the packet
     */
    protected PacketEvent(@NotNull ChannelHandlerContext ctx, @Nullable GameProfile profile, @Nullable Player player, @NotNull Packet packet) {
        this.ctx = ctx;
        this.profile = profile;
        this.player = player;
        this.packet = packet;
    }

    /**
     * Gets the {@link ChannelHandlerContext} for this packet activity.
     * Typically used during the {@link PacketStage#HANDSHAKE} stage.
     *
     * @return the ctx
     */
    public @NotNull ChannelHandlerContext getCtx() {
        return ctx;
    }

    /**
     * Gets the {@link GameProfile} for this packet activity.
     * Typically used during the {@link PacketStage#LOGIN} stage.
     *
     * @return the profile
     */
    public GameProfile getProfile() {
        return profile;
    }

    /**
     * Gets the {@link Player} for this packet activity.
     * Typically used during the {@link PacketStage#PLAY} stage.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the active packet.
     *
     * @return the packet
     */
    public @NotNull Packet getPacket() {
        return packet;
    }

    /**
     * Gets the cancellation state of this event.
     * A cancelled event will not be executed in the server, but will still pass to other plugins.
     *
     * @return true if this event was cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event.
     * A cancelled event will not be executed in the server, but will still pass to other plugins.
     *
     * @param cancelled true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
