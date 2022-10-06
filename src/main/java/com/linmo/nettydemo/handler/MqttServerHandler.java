package com.linmo.nettydemo.handler;

import com.linmo.nettydemo.utils.MqttMsgBack;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class MqttServerHandler extends SimpleChannelInboundHandler<MqttMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        if (msg != null) {
            Channel channel = ctx.channel();
            MqttFixedHeader fh = msg.fixedHeader();
            log.info(fh.messageType().toString());

            switch (fh.messageType()) {
                case PUBLISH:
                    MqttMsgBack.puback(channel, new MqttMessage(fh));
                    break;
                default:
                    break;
            }
        }
    }
}
