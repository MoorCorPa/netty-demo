package com.linmo.nettydemo.netty.server;

import com.linmo.nettydemo.handler.SocketChannelInitHandler;
import com.linmo.nettydemo.utils.PortDefinition;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RefreshScope
public class NettyServer {

    @Autowired
    private PortDefinition portDefinition;

    ChannelFuture future = null;
    //连接处理group
    NioEventLoopGroup boss = null;
    //时间处理group
    NioEventLoopGroup worker = null;
    //serverBootStrap示例
    ServerBootstrap bootstrap = new ServerBootstrap();

    public void start() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        log.info(String.valueOf(portDefinition==null));
        //Map<Integer, String> ports = portDefinition.getPort();
        Map<Integer, String> ports = new HashMap<>();
        ports.put(6001, "http");
        ports.put(6002, "tcp");
        ports.put(6003, "mqtt");

        //1.绑定group
        bootstrap.group(boss, worker)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_RCVBUF, 10485760)
                //2.设置并绑定Reactor线程池：EventLoopGroup，EventLoop就是处理所有注册到本线程的Selector上面的Channel
                .channel(NioServerSocketChannel.class)
                //3.保存连接数
                .option(ChannelOption.SO_BACKLOG, 1024)
                //4.有数据立即发送
                .handler(new LoggingHandler(LogLevel.INFO))
                //5.保持连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //6.handler作用于boss -- childHandler作用于worker;
                .childHandler(new SocketChannelInitHandler(ports));
        log.info("netty服务器在{}端口启动监听", JSONObject.toJSONString(ports));
        try {
             /*
               绑定多个端口核心代码
             */
            for (Map.Entry<Integer, String> p : ports.entrySet()) {
                final int port = p.getKey();
                // 绑定端口
                ChannelFuture future1 = bootstrap.bind(new InetSocketAddress(port)).sync();
                future1.addListener(future -> {
                    if (future.isSuccess()) {
                        log.info("netty 启动成功，端口：{}", port);
                    } else {
                        log.info("netty 启动失败，端口：{}", port);
                    }
                });
                future1.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> future1.channel().close());
            }
        } catch (Exception e) {
            log.error("netty 启动时发生异常-------{}", e);
        }
    }

    @PreDestroy
    public void stop() {
        if (future != null) {
            future.channel().close().addListener(ChannelFutureListener.CLOSE);
            future.awaitUninterruptibly();
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            future = null;
            log.info(" 服务关闭 ");
        }
    }
}
