package com.duangframework.rpc.serializable;


import com.duangframework.core.exceptions.RpcException;

import java.io.*;

public class JdkSerializableUtil {

    public static final int MAX_STREAM_SIZE = 512;

    private JdkSerializableUtil() {

    }


    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(final T obj) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(MAX_STREAM_SIZE);
        serialize(obj, baos);
        return baos.toByteArray();

    }

    private static <T> void serialize(final T obj, final OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException("The OutputStream must not be null");
        }
        if(!(obj instanceof  Serializable)){
            throw new RpcException("序列化对象(" + obj.getClass() + ")必须实现java.io.Serializable");
        }
        ObjectOutputStream out = null;
        try {
            // stream closed in the finally
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static <T> T deserialize(final byte[] objectData) {
        if (objectData == null) {
            throw new NullPointerException("The byte[] must not be null");
        }
        return deserialize(new ByteArrayInputStream(objectData));
    }

    private static <T> T deserialize(final InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(inputStream);
            @SuppressWarnings("unchecked")
            final T obj = (T) in.readObject();
            return obj;
        } catch (final ClassCastException ex) {
            throw new RpcException(ex);
        } catch (final ClassNotFoundException ex) {
            throw new RpcException(ex);
        } catch (final IOException ex) {
            throw new RpcException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
