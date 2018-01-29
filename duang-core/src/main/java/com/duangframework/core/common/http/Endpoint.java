/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.duangframework.core.common.http;

import java.net.URI;

public class Endpoint {

    /**
     * 产品名称
     */
    private String productName;
    /**
     * 产品代号
     */
    private String productCode;
    /**
     * 请求协议
     */
    private ProtocolType protocolType;
    /**
     * 请求域名
     */
    private String host;
    /**
     * 请求端口
     */
    private int port = 80;

    public Endpoint() {
    }

    /**
     * 构造函数
     * @param productName       产品名称
     * @param productCode       产品代码
     * @param endpoint              请求域名,如有端口,包括端口, 但不包括URI
     */
    public Endpoint(String productName, String productCode, String endpoint) {
        this.productName = productName;
        this.productCode = productCode;
        initEndpoint(endpoint);
    }

    private void initEndpoint(String endpoint) {
        try {
            URI uri = new URI(endpoint);
            this.host = uri.getHost();
            this.port = uri.getPort();
            if (ProtocolType.HTTP.toString().equalsIgnoreCase(uri.getScheme())) {
                this.protocolType = ProtocolType.HTTP;
            } else {
                this.protocolType = ProtocolType.HTTPS;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
