/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.protocol.http.client;

import org.eclipse.sensinact.gateway.protocol.http.HeadersCollection;
import org.eclipse.sensinact.gateway.util.json.JsonProviderFactory;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A Request configuration data structure
 *
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public class ConnectionConfigurationImpl<RESPONSE extends Response, REQUEST extends Request<RESPONSE>> extends HeadersCollection implements ConnectionConfiguration<RESPONSE, REQUEST> {

    protected Object content;
    protected int readTimeout;
    protected int connectTimeout;
    protected String contentType;
    protected String acceptType;
    protected String uri;
    protected String httpMethod;
	private String serverCertificate;
	private String clientCertificate;
	private String clientCertificatePassword;
    protected ProxyConfiguration proxyConfiguration;
    protected Map<String, String> parameters;

    /**
     * Constructor
     */
    public ConnectionConfigurationImpl() {
        super();
        this.readTimeout = -1;
        this.connectTimeout = -1;
        this.parameters = new HashMap<String, String>();
        this.proxyConfiguration = new ProxyConfiguration();
    }

    /**
     * Constructor
     *
     * @param configuration JSON formated string describing the
     *                      ConnectionConfiguration to instantiate
     */
    public ConnectionConfigurationImpl(String configuration) {
        this();
        JsonObject jsonConfiguration = JsonProviderFactory.getProvider().createReader(new StringReader(configuration)).readObject();

        this.uri = jsonConfiguration.getString("uri", null);
        this.httpMethod = jsonConfiguration.getString("httpMethod", null);

        this.content = jsonConfiguration.getString("content", null);

        this.acceptType = jsonConfiguration.getString("acceptType", null);
        this.contentType = jsonConfiguration.getString("contentType", null);

        this.connectTimeout = jsonConfiguration.getInt("connectTimeout", -1);

        this.readTimeout = jsonConfiguration.getInt("readTimeout", -1);

        JsonArray params = jsonConfiguration.getJsonArray("parameters");
        int index = 0;
        int length = params == null ? 0 : params.size();

        for (; index < length; index++) {
            JsonObject object = params.getJsonObject(index);
            if (object != null) {
            	JsonValue value = object.get("value");
            	if(value != null && value.getValueType() != ValueType.NULL) {
            		queryParameter(object.getString("key", null), value.getValueType() == ValueType.STRING ?
            				((JsonString)value).getString() : value.toString());
            	}
            }
        }
    }

    @Override
    public ConnectionConfigurationImpl<RESPONSE, REQUEST> queryParameter(String key, String value) {
        if (key != null && key.length() > 0 && value != null) {
            this.parameters.put(key, value);
        }
        return this;
    }

    @Override
    public ConnectionConfigurationImpl<RESPONSE, REQUEST> setUri(String uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public String getUri() {
        if (!this.parameters.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append(this.uri);

            Iterator<Map.Entry<String, String>> iterator = this.parameters.entrySet().iterator();
            int index = 0;
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                builder.append(index == 0 ? "?" : "&");
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(entry.getValue());
                index++;
            }
            return builder.toString();
        }
        return this.uri;
    }

    @Override
    public ConnectionConfigurationImpl<RESPONSE, REQUEST> setContent(Object content) {
        this.content = content;
        return this;
    }

    @Override
    public Object getContent() {
        return this.content;
    }

    @Override
    public ConnectionConfigurationImpl<RESPONSE, REQUEST> setAccept(String acceptType) {
        this.acceptType = acceptType;
        return this;
    }

    @Override
    public String getAccept() {
        return this.acceptType;
    }

    @Override
    public ConnectionConfigurationImpl<RESPONSE, REQUEST> setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public ConnectionConfigurationImpl<RESPONSE, REQUEST> setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    @Override
    public String getHttpMethod() {
        if (this.httpMethod == null) {
            return ConnectionConfiguration.DEFAULT_HTTP_METHOD;
        }
        return this.httpMethod;
    }

    @Override
    public ConnectionConfigurationImpl<RESPONSE, REQUEST> setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    @Override
    public int getConnectTimeout() {
        if (this.connectTimeout <= 0) {
            return ConnectionConfiguration.DEFAULT_CONNECTION_TIMEOUT;
        }
        return this.connectTimeout;
    }

    @Override
    public ConnectionConfigurationImpl<RESPONSE, REQUEST> setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    @Override
    public int getReadTimeout() {
        if (this.readTimeout <= 0) {
            return ConnectionConfiguration.DEFAULT_READ_TIMEOUT;
        }
        return this.readTimeout;
    }

    @Override
    public ConnectionConfigurationImpl<RESPONSE, REQUEST> setProxyHost(String proxyHost) {
        this.proxyConfiguration.setProxyHost(proxyHost);
        return this;
    }

    @Override
    public ConnectionConfigurationImpl<RESPONSE, REQUEST> setProxyPort(int proxyPort) {
        this.proxyConfiguration.setProxyPort(proxyPort);
        return this;
    }

    @Override
	public void setServerSSLCertificate(String serverCertificate) {
		this.serverCertificate = serverCertificate;
	}

	@Override
	public String getServerSSLCertificate() {
		return this.serverCertificate;
	}

	@Override
	public void setClientSSLCertificate(String clientCertificate) {
		this.clientCertificate = clientCertificate;
	}
	
	@Override
	public String getClientSSLCertificate() {
		return this.clientCertificate;
	}

	@Override
	public String getClientSSLCertificatePassword() {
		return this.clientCertificatePassword;
	}

	@Override
	public void setClientSSLCertificatePassword(String clientCertificatePassword) {
		this.clientCertificatePassword = clientCertificatePassword;
		
	}
    @Override
    public Proxy getProxy() {
        return this.proxyConfiguration.getProxy();
    }

    @Override
    public HttpURLConnection connect() throws IOException {
        return ConnectionConfiguration.HttpURLConnectionBuilder.build(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP ");
        builder.append(this.getHttpMethod());
        builder.append(" Request [");
        builder.append(this.getUri());
        builder.append("]");
        builder.append("\n\tContent-Type:");
        builder.append(this.getContentType());
        builder.append("\n\tAccept:");
        builder.append(this.getAccept());
        builder.append("\n\tConnection Timeout:");
        builder.append(this.getConnectTimeout());
        builder.append("\n\tRead Timeout:");
        builder.append(this.getReadTimeout());
        builder.append(super.toString());
        return builder.toString();
    }

}
