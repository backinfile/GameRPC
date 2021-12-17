package com.backinfile.gameRPC.net;

import com.backinfile.gameRPC.Log;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class Server extends Thread {
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            startServer(port);
        } catch (InterruptedException e) {
            Log.net.error("listen start error", e);
        }
    }

    private void startServer(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new Decoder(), new Encoder(), new ServerHandler());
                        }
                    });

            // 第四步，开启监听
            Channel channel = b.bind(port).sync().channel();
            Log.server.info("start listen:{}", port);

            countDownLatch.await();
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void stopServer() {
        countDownLatch.countDown();
    }


    private static class ServerHandler extends ChannelInboundHandlerAdapter {
        private ChannelConnection connection;
        private static final AtomicLong idAllot = new AtomicLong(0);
        private static final AtomicLong connectionCount = new AtomicLong(0);

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            Channel channel = ctx.channel();
            long id = idAllot.incrementAndGet();
            connection = new ChannelConnection(id, channel);
            connectionCount.incrementAndGet();
            Log.server.info("channelActive id={}, num={}", connection.getId(), connectionCount);

            // TODO set connection to game

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            connection.addInput((byte[]) msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            connection.close();
            Log.server.info("channelInactive id=" + connection.getId());
            connectionCount.decrementAndGet();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            Log.server.error("exceptionCaught id=" + connection.getId() + " {}", cause.getMessage());
        }
    }
}