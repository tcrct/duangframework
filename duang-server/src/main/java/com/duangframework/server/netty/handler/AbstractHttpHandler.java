package com.duangframework.server.netty.handler;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public abstract class AbstractHttpHandler {

    private static Logger logger = LoggerFactory.getLogger(AbstractHttpHandler.class);

    private static final String JSON = new AsciiString("application/json;charset=utf-8").toString();
    private static final String TEXT = new AsciiString("text/html;charset=UTF-8").toString();
    private static final int HTTP_CACHE_SECONDS = 60;
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";

    protected void response(ChannelHandlerContext ctx, boolean keepAlive, IRequest reuqest, IResponse response) throws Exception {
        File downloadFile = response.getDownloadFile();
        if(ToolsKit.isNotEmpty(downloadFile)) {
            downloadFileHandle(ctx, keepAlive, reuqest, response);
        } else {
            // 构建请求返回对象，并设置返回主体内容结果
            HttpResponseStatus status = response.getStatus() == 200 ? HttpResponseStatus.OK : HttpResponseStatus.INTERNAL_SERVER_ERROR;
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(response.toString(), HttpConstants.DEFAULT_CHARSET));
            builderResponseHeader(fullHttpResponse, reuqest, response);
            HttpHeaders.setKeepAlive(fullHttpResponse, keepAlive);
            ChannelFuture channelFutureListener = ctx.channel().writeAndFlush(fullHttpResponse);
            //如果不支持keep-Alive，服务器端主动关闭请求
            if (!keepAlive) {
                channelFutureListener.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    // https://www.cnblogs.com/carl10086/p/6185095.html
    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    /**
     * 设置返回Header头信息
     * @param response
     * @param request
     */
    private void builderResponseHeader(FullHttpResponse fullHttpResponse, IRequest request, IResponse response) {
        HttpHeaders responseHeaders = fullHttpResponse.headers();
        String conentType = request.getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
        if(ToolsKit.isEmpty(conentType)) {
            conentType = JSON;
        }

        responseHeaders.set(HttpHeaderNames.CONTENT_TYPE.toString(), conentType); //设置返回结果格式

        Map<String,String> headersMap = response.getHeaders();
        if(ToolsKit.isNotEmpty(headersMap)) {
            for(Iterator<Map.Entry<String,String>> iterator = headersMap.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String,String> entry = iterator.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if(ToolsKit.isNotEmpty(key) && ToolsKit.isNotEmpty(value)) {
                    responseHeaders.set(key, value);
                }
            }
        }


        // 数据分块返回客户端，不能与CONTENT_LENGTH同时使用，一般用于图片或文件之类的stream
        // 功能待实现，暂不开启，如开启则返回数据会不显示
        /*
        String acceptEncoding = request.getHeader(HttpHeaderNames.ACCEPT_ENCODING.toString());
        if(ToolsKit.isNotEmpty( acceptEncoding)) {
            responseHeaders.set(HttpHeaderNames.CONTENT_ENCODING.toString(), acceptEncoding);
            responseHeaders.set(HttpHeaderNames.TRANSFER_ENCODING.toString(), HttpHeaderValues.CHUNKED.toString());
        }
        */
        responseHeaders.set(HttpHeaderNames.DATE.toString(), ToolsKit.getCurrentDateString());
        int readableBytesLength = 0;
        try {
            readableBytesLength = fullHttpResponse.content().readableBytes();
        } catch (Exception e) {}
        responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH.toString(), readableBytesLength);
    }

    private void downloadFileHandle(ChannelHandlerContext ctx, boolean keepAlive, IRequest request, IResponse response) throws Exception {
        File file = response.getDownloadFile();
        if(ToolsKit.isEmpty(file)) {
            throw new EmptyNullException("download file is null");
        }
        RandomAccessFile raf = null;
        long fileLength = 0L;
        try {
            raf = new RandomAccessFile(file, "r");
            fileLength = raf.length();
        } catch (Exception e) {
            throw new EmptyNullException(e.getMessage(), e);
        }
        HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        setDownloadFileContentHeader(httpResponse, file);
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH.toString(), fileLength);
        if (keepAlive) {
            httpResponse.headers().set(HttpHeaderNames.CONNECTION.toString(), HttpHeaderNames.KEEP_ALIVE.toString());
        }

        // Write the initial line and the header.
        ctx.write(httpResponse);
        // Write the content.
        ChannelFuture sendFileFuture = null;
        ChannelFuture lastContentFuture = null;
        if (ctx.pipeline().get(SslHandler.class) == null) {
//            sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            sendFileFuture =  ctx.write(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)), ctx.newProgressivePromise());;

            // Write the end marker.
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } else {
            try {
                sendFileFuture = ctx.writeAndFlush(
                        new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)),
                        ctx.newProgressivePromise());
                // HttpChunkedInput will write the end marker (LastHttpContent) for us.
                lastContentFuture = sendFileFuture;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sendFileFuture.addListener(ProgressiveFutureListener.build(raf, file, response.isDeleteDownloadFile()));
        // Decide whether to close the connection or not.
        if (!keepAlive) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


    private static void setDownloadFileContentHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));

        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(file.lastModified())));

        // 加了以下代码才会弹窗
        try {
            response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment; filename=" + new String(file.getName().getBytes("GBK"), "ISO8859-1"));
        } catch (UnsupportedEncodingException e) {
            response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        }
    }

}
