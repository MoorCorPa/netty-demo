package com.linmo.nettydemo.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.xml.transform.Result;
import java.util.List;
import java.util.Locale;

@Slf4j
public class AMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        StringBuffer msg = new StringBuffer();
        for (byte aByte : data) {
            msg.append(String.format("%02x", aByte).toUpperCase());
        }
        if (StringUtils.hasText(msg.toString())) {
            log.error("接收的报文为空！");
        }
        log.info("<----初始数据: {} ", msg);
        assemblyResult(ctx, msg.toString(), list);
    }

    private void assemblyResult(ChannelHandlerContext ctx, String data, List<Object> list) {
        List<Result> messageList = analysisMessage(data);
//        list.addAll(data);
    }

    /**
     * 解析报文信息
     *
     * @param data 未解析数据
     */
    private List<Result> analysisMessage(String data) {
        // 解析报文，具体解析方式跟硬件约定协议有关。
       /* 对于数据包会不会出现粘包或拆包，需要根据实际情况进行处理。netty中常用的方法是根据包头上
       的长度字节来判断当前包是否出现粘包或拆包
       */
        return null;
    }
}
