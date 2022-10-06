package com.linmo.nettydemo.handler;

import com.linmo.nettydemo.utils.GatewayType;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketChannelInitHandler extends ChannelInitializer<SocketChannel> {

    /**
     * 用来存储每个连接上来的设备
     */
    public static final Map<ChannelId, ChannelPipeline> CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 端口信息，用来区分这个端口属于哪种类型的连接 如：6001 属于 A
     */
    Map<Integer, String> ports;

    public SocketChannelInitHandler(Map<Integer, String> ports) {
        this.ports = ports;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //每次连接上来 对通道进行保存
        CHANNEL_MAP.put(socketChannel.id(), socketChannel.pipeline());
        ChannelPipeline pipeline = socketChannel.pipeline();
        int port = socketChannel.localAddress().getPort();
        String type = ports.get(port);
        //不同类型连接，处理链中加入不同处理协议
        switch (type) {
            case GatewayType.HTTP:
                // http 消息聚合器 512*1024为接收的最大 contentlength
                // 把单个http请求转为FullHttpReuest或FullHttpResponse
                pipeline.addLast("aggregator", new HttpObjectAggregator(10*1024*1024));
                pipeline.addLast(
                        new HttpServerCodec(),
                        new HttpServerHandler(),
                        new LoggingHandler(LogLevel.DEBUG));
                break;
            case GatewayType.TCP:
                break;
            case GatewayType.MQTT:
                pipeline.addLast(
                        new IdleStateHandler(600, 600,1200),
                        MqttEncoder.INSTANCE,
                        new MqttDecoder(),
                        new MqttServerHandler(),
                        new LoggingHandler(LogLevel.DEBUG));
                break;
            default:
                log.error("当前网关类型并不存在于配置文件中，无法初始化通道");
                break;
        }
        pipeline.addLast(
                new StringEncoder(StandardCharsets.UTF_8),
                new StringDecoder(StandardCharsets.UTF_8));
    }
}
