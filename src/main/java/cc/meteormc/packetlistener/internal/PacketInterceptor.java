package cc.meteormc.packetlistener.internal;

import cc.meteormc.packetlistener.event.PacketInboundEvent;
import cc.meteormc.packetlistener.event.PacketOutboundEvent;
import cc.meteormc.packetlistener.objects.Packet;
import cc.meteormc.packetlistener.objects.PacketDirection;
import cc.meteormc.packetlistener.objects.PacketStage;
import com.mojang.authlib.GameProfile;
import io.netty.channel.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Packet interceptor. All written or read packets pass through here.
 *
 * @author Meteor23333
 */
class PacketInterceptor extends ChannelDuplexHandler {
    private GameProfile profile;
    private Player player;

    /**
     * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx the {@link ChannelHandlerContext} for which the read operation is made
     * @param msg the message to read
     * @throws Exception thrown if an error occurs
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Inbound
        Packet packet = Packet.fromHandle(msg);
        if (packet != null) {
            handlePacket(packet);
            if (callEvent(new PacketInboundEvent(ctx, profile, player, packet))) {
                return;
            }
        }

        super.channelRead(ctx, msg);
    }

    /**
     * Calls {@link ChannelHandlerContext#write(Object, ChannelPromise)} to forward
     * to the next {@link ChannelOutboundHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx the {@link ChannelHandlerContext} for which the write operation is made
     * @param msg the message to write
     * @param promise the {@link ChannelPromise} to notify once the operation completes
     * @throws Exception thrown if an error occurs
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // Outbound
        Packet packet = Packet.fromHandle(msg);
        if (packet != null) {
            handlePacket(packet);
            if (callEvent(new PacketOutboundEvent(ctx, profile, player, packet))) {
                return;
            }
        }

        super.write(ctx, msg, promise);
    }

    /**
     * Calls {@link ChannelHandlerContext#fireExceptionCaught(Throwable)} to forward
     * to the next {@link ChannelHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx the {@link ChannelHandlerContext} for which the exception was raised
     * @param cause the exception that was caught
     * @throws Exception thrown if an error occurs
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        PacketListener.getLogger().log(Level.WARNING, "An exception occurred while handling client traffic!", cause);
        super.exceptionCaught(ctx, cause);
    }

    /**
     * Handles certain special packets.
     *
     * @param packet the packet
     */
    private void handlePacket(@NotNull Packet packet) {
        try {
            if (packet.getDirection() == PacketDirection.CLIENTBOUND) {
                if (packet.getStage() == PacketStage.LOGIN && "Success".equalsIgnoreCase(packet.getName())) {
                    // Success Login
                    this.profile = packet.getFieldValueOfType(GameProfile.class, 0);
                }
            }

            if (packet.getDirection() == PacketDirection.SERVERBOUND) {
                if (packet.getStage() == PacketStage.LOGIN && "Start".equalsIgnoreCase(packet.getName())) {
                    // Hello Login
                    this.profile = packet.getFieldValueOfType(GameProfile.class, 0);
                }
            }

            if (player == null && profile != null) {
                this.player = Bukkit.getPlayer(profile.getId());
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * Conveniently calls the event and returns whether it was cancelled.
     *
     * @param event the event to call
     * @return true if the event was cancelled
     */
    private static boolean callEvent(Event event) {
        try {
            Bukkit.getPluginManager().callEvent(event);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (event instanceof Cancellable) {
            return ((Cancellable) event).isCancelled();
        } else {
            return false;
        }
    }
}
