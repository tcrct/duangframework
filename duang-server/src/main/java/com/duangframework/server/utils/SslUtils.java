//package com.duangframework.server.utils;
//
//import com.duangframework.core.exceptions.EmptyNullException;
//import io.netty.handler.ssl.SslContext;
//
//
//import javax.net.ssl.TrustManagerFactory;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//
///**
// * Created by laotang on 2017/11/3.
// */
//public class SslUtils {
//
//    private static SslUtils sslUtils = new SslUtils();
//    private static File cert;
//    private static File privateKey;
//
//    private static SslUtils duang() {
//        return sslUtils;
//    }
//
//    private SslUtils cert(File file) {
//        cert = file;
//        return this;
//    }
//
//    private SslUtils privatekey(File file) {
//        privateKey = file;
//        return this;
//    }
//
//
//
//    public SslContext context() {
//        try {
//            InputStream certStream = new FileInputStream(cert);
//            if (certStream == null) {
//                throw new EmptyNullException("Certification file(" + cert.getName() + ") not exists");
//            }
//
//            InputStream privateKeyStream = new FileInputStream(privateKey);
//            if (privateKeyStream == null) {
//                certStream.close();
//                throw new EmptyNullException("Certification file(" + privateKey.getName() + ") not exists");
//            }
//            SslContext context = buildServerSsl(certStream, privateKeyStream);
//        } catch (Exception e) {
//
//        }
//    }
//
//
//    private SslContext buildServerSsl(InputStream certStream, InputStream privateKeyStream) {
//        try {
//            SslContext.newClientContext(cert);
//            SslContextBuilder builder = SslContextBuilder.forServer(certStream, privateKeyStream);
//            return builder.build();
//        } catch (Exception e) {
//            throw new IllegalArgumentException(e.getMessage(), e);
//        }
//    }
//
//
//}
