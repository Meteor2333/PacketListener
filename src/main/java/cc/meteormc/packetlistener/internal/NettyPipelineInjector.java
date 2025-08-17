package cc.meteormc.packetlistener.internal;

import cc.meteormc.packetlistener.PacketListener;
import cc.meteormc.packetlistener.helper.Reflection;
import io.netty.channel.*;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

/**
 * Used to inject the packet interceptor into a {@link ChannelPipeline} at the appropriate time.
 *
 * @author Meteor23333
 */
public class NettyPipelineInjector {
    private final ChannelPipeline pipeline;

    private static final Class<?> SERVER_BOOTSTRAP_ACCEPTOR_CLASS;

    static {
        try {
            SERVER_BOOTSTRAP_ACCEPTOR_CLASS = Class.forName("io.netty.bootstrap.ServerBootstrap$ServerBootstrapAcceptor");
        } catch (ClassNotFoundException e) {
            // This is most likely impossible.
            throw new IllegalStateException(e);
        }
    }

    /**
     * Injects the packet interceptor into a {@link ChannelPipeline}.
     *
     * @param pipeline the pipeline to inject into
     */
    public NettyPipelineInjector(@NotNull ChannelPipeline pipeline) {
        this.pipeline = pipeline;
        pipeline.addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                try {
                    // Add the interceptor before the packet is handled.
                    ctx.pipeline().addBefore(
                            "packet_handler",
                            "packetlistener_interceptor",
                            new PacketInterceptor()
                    );
                } finally {
                    super.channelActive(ctx);
                }
            }
        });
    }

    /**
     * Gets the target pipeline this injector is associated with.
     *
     * @return the target pipeline
     */
    public @NotNull ChannelPipeline getPipeline() {
        return pipeline;
    }

    /**
     * Adds the injector logic to the server's {@code Channels}.
     * The interceptor will be automatically injected when a {@link Channel} is initialized
     * after the client and server have established a connection.
     * <p>
     * This method is executed on a separate thread. Its purpose is to wait until the server has completed its network initialization.
     *
     * @throws IllegalThreadStateException if the thread was already started
     */
    @SuppressWarnings("ALL")
    public static void injectAll() throws IllegalThreadStateException {
        Thread thread = new Thread(() -> {
            Object server = getMinecraftServer();
            if (server == null) return;

            Field serverConnectionField = Reflection.findField(server.getClass(), "ServerConnection");
            if (serverConnectionField == null) {
                PacketListener.getLogger().log(Level.SEVERE, "Could not find a field of type ServerConnection in MinecraftServer!");
                return;
            }

            List<ChannelFuture> channels;

            try {
                // Block until ServerConnection is available.
                while (true) {
                    Object object = serverConnectionField.get(server);
                    if (object == null) continue;

                    Field[] fields = serverConnectionField.getType().getDeclaredFields();
                    Field channelsField = fields[fields.length - 2]; // It is always the second to last element.
                    channelsField.setAccessible(true);
                    channels = List.class.cast(channelsField.get(object));
                    break;
                }
            } catch (Exception e) {
                PacketListener.getLogger().log(Level.SEVERE, "Could not find the channels field in ServerConnection!", e);
                return;
            }

            // Block until Channels is not empty.
            while (channels.isEmpty()) ;

            // Typically, Channels on the server side contain only one element, but this is done just to be safe.
            for (ChannelFuture channel : channels) {
                channel.channel().pipeline().addFirst(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        try {
                            if (msg instanceof Channel) {
                                Channel channel = (Channel) msg;
                                new NettyPipelineInjector(channel.pipeline());
                            }
                        } finally {
                            super.channelRead(ctx, msg);
                        }
                    }
                });
            }

            Thread.yield();
        }, "PacketListener-ServerSocketChannelWatcher");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Gets the {@code MinecraftServer} instance using reflection.
     *
     * @return the MinecraftServer instance
     */
    private static @Nullable Object getMinecraftServer() {
        try {
            Server server = Bukkit.getServer();
            return server.getClass().getDeclaredMethod("getServer").invoke(server);
        } catch (Exception e) {
            PacketListener.getLogger().log(Level.SEVERE, "Could not find MinecraftServer!", e);
        }
        return null;
    }
}
