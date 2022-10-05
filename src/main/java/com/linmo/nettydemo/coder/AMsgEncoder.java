package com.linmo.nettydemo.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AMsgEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
//        SendData message = (SendData) o;
//        //拼接需要发送的数据信息
//        log.info("----> 初始数据【{}】 ： {}", message.getCode(), msg);
//        //将字符类型转换成字节类型
//        byte[] bytes = DataTypeConvert.hexStringToBytes(msg.toString());
//        //写入字节缓冲区
//        byteBuf.writeBytes(bytes);
    }
}
