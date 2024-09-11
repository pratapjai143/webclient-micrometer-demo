package com.javahabit.parentservice;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.context.ContextSnapshotFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ContextInboundHandler extends ChannelInboundHandlerAdapter {

    private static final ContextSnapshotFactory SNAPSHOT_FACTORY = ContextSnapshotFactory.builder()
            .contextRegistry(ContextRegistry.getInstance())
            .build();

    private static ContextSnapshot.Scope setThreadLocals(ChannelHandlerContext ctx) {
        return SNAPSHOT_FACTORY.setThreadLocalsFrom(ctx.channel());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        try (final ContextSnapshot.Scope s = setThreadLocals(ctx)) {
            super.channelRegistered(ctx);
        }
    }
}
