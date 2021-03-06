/*
 * Copyright (c) 2009-present, b3log.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.latke.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Http Server based on Netty 4.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jan 15, 2020
 * @since 3.0.0
 */
public abstract class BaseServer {

    private static final Logger LOGGER = LogManager.getLogger(BaseServer.class);

    private static final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup(1);
    private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup();

    public void start(final int listenPort) {
        startServer(listenPort);
    }

    public void shutdown() {
        shutdownServer();
    }

    private void startServer(final int listenPort) {
        try {
            InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
            new ServerBootstrap().
                    group(BOSS_GROUP, WORKER_GROUP).
                    channel(NioServerSocketChannel.class).
                    handler(new LoggingHandler(LogLevel.INFO)).
                    childHandler(new HttpServerInitializer()).
                    bind(listenPort).sync().channel().closeFuture().sync();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Start server failed, exit process", e);
            System.exit(-1);
        }
    }

    private void shutdownServer() {
        try {
            LOGGER.log(Level.INFO, "HTTP server is shutting down");
            BOSS_GROUP.shutdownGracefully(1, 7, TimeUnit.SECONDS).await();
            WORKER_GROUP.shutdownGracefully(1, 7, TimeUnit.SECONDS).await();
            LOGGER.log(Level.INFO, "HTTP server has shut down");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Shutdown server failed", e);
        }
    }

    private static final class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        public void initChannel(final SocketChannel ch) {
            final ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 64));
            pipeline.addLast(new WebSocketHandler());
            pipeline.addLast(new ServerHandler());
        }
    }
}
