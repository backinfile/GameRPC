package com.backinfile.gameRPC.net;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.RemoteNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class Client extends Thread {
    public static Channel Channel = null;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final String host;
    private final int port;
    private final RemoteNode remoteNode;

    public Client(RemoteNode remoteNode, String host, int port) {
        this.remoteNode = remoteNode;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            startClient(host, port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startClient(String host, int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress(host, port))
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new Decoder(), new Encoder(), new ClientHandler());
                }
            });

            Log.client.info("start connect {}:{}", host, port);
            Channel = b.connect().sync().channel();
            Log.client.info("connected: {}:{}", host, port);

            countDownLatch.await();
            Channel.closeFuture().sync();
            Log.client.info("connection close {}:{}", host, port);
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public void stopClient() {
        countDownLatch.countDown();
    }


    private class ClientHandler extends ChannelInboundHandlerAdapter {
        private ChannelConnection connection = null;


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            connection = new ChannelConnection(0, ctx.channel());
            remoteNode.setConnection(connection);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            stopClient();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            connection.addInput((byte[]) msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            Log.game.error("error in ClientHandler {} {}", cause.getClass().getSimpleName(), cause.getMessage());
        }

    }
}
