/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.commands.gogo.internal.shell;

import org.eclipse.sensinact.gateway.commands.gogo.internal.CommandServiceMediator;
import org.eclipse.sensinact.gateway.core.method.Parameter;
import org.eclipse.sensinact.gateway.core.security.Authentication;
import org.eclipse.sensinact.gateway.core.security.SessionToken;
import org.eclipse.sensinact.gateway.core.security.Credentials;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundMediator;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRecipient;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequest;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Extended {@link NorthboundRequestWrapper} dedicated to shell access
 * request wrapper
 *
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public class ShellAccessRequest implements NorthboundRequestWrapper {
	
	private static final Logger LOG = LoggerFactory.getLogger(ShellAccessRequest.class);
    private NorthboundMediator mediator;
    private JsonObject request;
    private Authentication<?> authentication;
    private String content;

    public ShellAccessRequest(NorthboundMediator mediator, JsonObject request) {
        this.request = request;
        this.mediator = mediator;
    }

    /**
     * @inheritDoc
     * @see org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequestWrapper#getMediator()
     */
    @Override
    public NorthboundMediator getMediator() {
        return this.mediator;
    }

    /**
     * @inheritDoc
     * @see org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequestWrapper#getRequestURI()
     */
    @Override
    public String getRequestURI() {

        String uri = request.getString("uri");
        String[] uriElements = uri.split("\\?");
        return uriElements[0];
    }

    /**
     * @inheritDoc
     * @see org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequestWrapper#getQueryMap()
     */
    @Override
    public Map<QueryKey, List<String>> getQueryMap() {
        String uri = request.getString("uri");
        String[] uriElements = uri.split("\\?");
        if (uriElements.length == 2) {
            try {
                return NorthboundRequest.processRequestQuery(uriElements[1]);
            } catch (UnsupportedEncodingException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return Collections.<QueryKey, List<String>>emptyMap();
    }

    /**
     * @inheritDoc
     * @see org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequestWrapper#getContent()
     */
    @Override
    public String getContent() {
        if (this.content == null) {
            JsonArray parameters = request.getJsonArray("parameters");
            if (parameters == null) {
                this.content = "[]";
            } else {
            	this.content = parameters.toString();
            }
        }
        return this.content;
    }

    /**
     * @inheritDoc
     * @see org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequestWrapper#getAuthentication()
     */
    @Override
    public Authentication<?> getAuthentication() {
        if (this.authentication == null) {
            String tokenHeader = request.getString("token", null);
            String login = request.getString("login", null);
            String password = request.getString("password", null);

            if (tokenHeader != null) {
                this.authentication = new SessionToken(tokenHeader);

            } else if (login != null && password != null) {
                this.authentication = new Credentials(login, password);
            }
        }
        return this.authentication;
    }

	@Override
	public String getRequestIdProperty() {
		return null;
	}

	@Override
	public String getRequestId() {
		return null;
	}

	@Override
	public NorthboundRecipient createRecipient(List<Parameter> parameters) {
        NorthboundRecipient recipient = new ShellRecipient((CommandServiceMediator) mediator);
        return recipient;
	}
}
