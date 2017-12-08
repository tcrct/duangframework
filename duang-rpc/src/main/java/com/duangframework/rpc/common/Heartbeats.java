package com.duangframework.rpc.common;


public class Heartbeats {

    //    private static final ByteBuf HEARTBEAT_BUF;
    private static final RpcRequest heartbeatsRpcRequest;

    static {
//        ByteBuf buf = Unpooled.buffer(Const.HEAD_LENGTH);
//        buf.writeShort(Const.MAGIC);
//        buf.writeByte(Const.HEARTBEAT);
//        buf.writeByte(0);
//        buf.writeLong(0);
//        buf.writeInt(0);
//        HEARTBEAT_BUF = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(buf));

        heartbeatsRpcRequest = new RpcRequest();
//        heartbeatsRpcRequest.setRequestId(Const.DUANG_RPC_HEARTBEATS_REQUEST_ID);
//        heartbeatsRpcRequest.setVersion(Const.DUANG_RPC_HEARTBEATS_VERSION);
//        heartbeatsRpcRequest.setService(RpcUtils.getHost() + ":" + RpcUtils.getPort());
    }

    /**
     * Returns the shared heartbeat content.
     */
//    public static ByteBuf heartbeatContent() {
//        return HEARTBEAT_BUF.duplicate();
//    }
    public static RpcRequest heartbeatRpcRequest() {
        return heartbeatsRpcRequest;
    }
}
