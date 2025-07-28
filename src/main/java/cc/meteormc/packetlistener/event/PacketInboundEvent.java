package cc.meteormc.packetlistener.event;

import cc.meteormc.packetlistener.objects.Packet;
import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Packet inbound event.
 *
 * @author Meteor23333
 */
public class PacketInboundEvent extends PacketEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Creates a {@link PacketInboundEvent}.
     *
     * @param ctx     the ctx, representing the handler's context in the pipeline
     * @param profile the profile, available after the client and server have completed the handshake
     * @param player  the player, available after the client and server have completed the login process
     * @param packet  the packet
     */
    public PacketInboundEvent(@NotNull ChannelHandlerContext ctx, @Nullable GameProfile profile, @Nullable Player player, @NotNull Packet packet) {
        super(ctx, profile, player, packet);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
