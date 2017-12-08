package com.duangframework.rpc.serializable;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.duangframework.core.exceptions.RpcException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializableUtil {
    /**
     * 序列化
     * @param object
     * @param <T>
     * @return
     */
     public static <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new NullPointerException("Hessian序列化对象(" + obj.getClass() + ")为null");
        }
        if(!(obj instanceof  java.io.Serializable)){
            throw new RuntimeException("Hessian序列化对象(" + obj.getClass() + ")必须实现java.io.Serializable");
        }
        byte[] results = null;
        ByteArrayOutputStream os = null;
         HessianOutput hessianOutput = null;
        try {
            os = new ByteArrayOutputStream();
            hessianOutput = new HessianOutput(os);
            //write本身是线程安全的
            hessianOutput.writeObject(obj);
            results = os.toByteArray();
        } catch (Exception e) {
            throw new RpcException("Hessian序列化(" + obj.getClass() + ")对象(" + obj + ")发生异常!", e);
        } finally {
            try {
                if (null != os)  os.close();
                if (null !=  hessianOutput)  hessianOutput.close();
            } catch (IOException e) {
                throw new RpcException(e);
            }
        }
        return results;
    }

    /**
     * 反序列化
     * @param resultClass
     * @param bytes
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("The byte[] must not be null");
        }
        T result = null;
        ByteArrayInputStream is = null;
        HessianInput hessianInput = null;
        try {
            is = new ByteArrayInputStream(bytes);
            hessianInput = new HessianInput(is);
            result = (T) hessianInput.readObject();
        } catch (Exception e) {
            throw new RpcException(e);
        } finally {
            try {
                if (null != is) is.close();
                if (null != hessianInput) hessianInput.close();
            } catch (IOException e) {
                throw new RpcException(e);
            }
        }
        return result;
    }
}
