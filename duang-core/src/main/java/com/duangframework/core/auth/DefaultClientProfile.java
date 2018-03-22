package com.duangframework.core.auth;

import com.duangframework.core.common.enums.FormatTypeEnums;
import com.duangframework.core.interfaces.ICredentials;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Created by laotang
 * @date createed in 2018/1/27.
 */
public class DefaultClientProfile implements IClientProfile {

    protected Endpoint endpoint;
    protected FormatTypeEnums formatTypeEnums;
    protected ICredentials credentials;
    protected Map<String,String> credentialsMap = new HashMap<>();

    public DefaultClientProfile(Endpoint endpoint, FormatTypeEnums formatTypeEnums, ICredentials credentials) {
        this.endpoint = endpoint;
        this.formatTypeEnums = formatTypeEnums;
        this.credentials = credentials;
        credentialsMap.put(ICredentials.APPID_FIELD, credentials.getAccessKeyId());
        credentialsMap.put(ICredentials.APPSECRET_FIELD, credentials.getAccessKeySecret());
    }

    @Override
    public String getEndPoint() {
        String endpointString = endpoint.getProtocolType() + "://" + endpoint.getHost();
        if(endpoint.getPort() != 80 && endpoint.getPort() != 443) {
            endpointString = endpointString.endsWith("/") ? endpointString.substring(0, endpointString.length()-1) : endpointString;
            endpointString += ":" + endpoint.getPort();
        }
        return endpointString;
    }

    @Override
    public FormatTypeEnums getFormat() {
        return formatTypeEnums;
    }

    @Override
    public ICredentials getCredential() {
        return credentials;
    }

    public Map<String,String> getCredentialsMap() {
        return credentialsMap;
    }
}
