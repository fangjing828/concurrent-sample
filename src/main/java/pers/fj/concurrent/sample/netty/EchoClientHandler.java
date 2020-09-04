package pers.fj.concurrent.sample.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by fang_j on 2020/08/25.
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private final ByteBuf message;

    public EchoClientHandler() {
        this.message = Unpooled.buffer(EchoClient.size);
        for (int i = 0; i < message.capacity(); i++) {
            message.writeByte((byte)i);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
