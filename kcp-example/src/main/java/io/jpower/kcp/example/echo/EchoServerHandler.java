package io.jpower.kcp.example.echo;

import io.jpower.kcp.netty.UkcpChannel;
import io.jpower.kcp.netty.UkcpServerChildChannel;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Handler implementation for the echo server.
 *
 * @author <a href="mailto:szhnet@gmail.com">szh</a>
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    Channel channel = null;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UkcpChannel kcpCh = (UkcpChannel) ctx.channel();
        kcpCh.conv(EchoServer.CONV);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.write(msg);
        if(channel == null){
            channel = ctx.channel();
        }
        UkcpServerChildChannel childChannel = (UkcpServerChildChannel) channel;
        System.out.println(childChannel.remoteAddress());

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(()->{
            try {
                channel.writeAndFlush(Unpooled.copiedBuffer("test".getBytes()));
            }catch (Exception e){
                e.printStackTrace();
            }

        },0,10, TimeUnit.SECONDS);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}
