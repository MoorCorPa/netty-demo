package com.linmo.nettydemo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 在解析完数据后可以得到当前设备的一些信息，如ip 唯一标示等。可以用来绑定当前通道的唯一id。
     * 业务不同这个步骤操作也不同，在初始化通道的时候，用了ChannelId 来绑定对应的通道，其实是
     * 可以使用远端连接ip来进行绑定的，那么这里就不用再绑定通道id一次。这就需要根据各自的具体业务了。
     */
    private static final Map<String, ChannelId> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static Map<String, ChannelId> getChannelMap() {
        return CHANNEL_MAP;
    }

    /**
     * 当从客户端接收到一个消息时被调用
     * msg 解析后的数据信息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        log.error(msg.method().toString());
//        String gwCode = message.getCode();
//        ChannelId channelId = ctx.channel().id();
//        CHANNEL_MAP.put(gwMac, channelId);
//        /*下面就是业务模块了。需要注意的是Spring框架默认@Socpe是singleton单例的，如果你不想当前处理
//          器被共用那么你就该指定该类为多例或者手动去new
//        */

        //获取请求体&方法
        log.info(msg.content().toString());

        String rspMsg = "<h1>hello world</h1>";

        //返回响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                msg.protocolVersion(),
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(rspMsg, CharsetUtil.UTF_8));


        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

        //写回响应
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 通道关闭，清除该网关记录
     *
     * @param gwCode 网关
     */
    public static void closeChannel(String gwCode) {
        CHANNEL_MAP.remove(gwCode);
    }

    /**
     * 在与客户端的连接已经建立之后将被调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("新增网关设备连接成功，远端地址为：{}", ctx.channel().remoteAddress());
    }

    /**
     * 主动发送信息
     *
     * @param code code
     * @param msg  信息
     */
    public void send(String code, String msg) {
        if (CHANNEL_MAP.containsKey(code)) {
            ChannelPipeline pipeline = SocketChannelInitHandler.CHANNEL_MAP.get(CHANNEL_MAP.get(code));
            if (pipeline == null) {
                //
                SocketChannelInitHandler.CHANNEL_MAP.remove(CHANNEL_MAP.get(code));
            }
            assert pipeline != null;
            pipeline.writeAndFlush(msg);
        } else {
            System.out.println("-------设备已经断开连接-------");
        }
    }


    /**
     * 客户端与服务端断开连接时调用
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
        ChannelId id = ctx.channel().id();
        //关闭
        SocketChannelInitHandler.CHANNEL_MAP.remove(id);
        log.info("网关: {} 服务端连接关闭...", ctx.channel().remoteAddress());
    }

    /**
     * 服务端接收客户端发送过来的数据结束之后调用
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 在处理过程中引发异常时被调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("remoteAddress: {},连接异常：{}", ctx.channel().remoteAddress(), cause);
    }

}
