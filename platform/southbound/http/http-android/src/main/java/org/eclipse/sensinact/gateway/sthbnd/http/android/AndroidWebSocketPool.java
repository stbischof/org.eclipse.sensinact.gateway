/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.sthbnd.http.android;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.sensinact.gateway.common.bundle.Mediator;
import org.eclipse.sensinact.gateway.generic.local.LocalProtocolStackEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AndroidWebSocketPool implements WebSocketCreator {
	
	private static final Logger LOG = LoggerFactory.getLogger(AndroidWebSocketPool.class);
    private final LocalProtocolStackEndpoint<DevGenPacket> endpoint;
    private Mediator mediator;
    private List<AndroidWebSocketWrapper> sessions;

    public AndroidWebSocketPool(Mediator mediator, LocalProtocolStackEndpoint<DevGenPacket> endpoint) {
        this.mediator = mediator;
        this.endpoint = endpoint;
        this.sessions = new ArrayList<AndroidWebSocketWrapper>();
    }

    public void removeSensinactSocket(AndroidWebSocketWrapper wsWrapper) {
        LOG.info(String.format("Removing session for client address %s.", wsWrapper.getClientAddress()));
        Boolean removedSuccessfully = true;
        synchronized (this.sessions) {
            if ((removedSuccessfully = this.sessions.remove(wsWrapper))) {
                wsWrapper.close();
            }
        }
        LOG.warn(String.format("Session removal %s executed.", removedSuccessfully ? "successfully" : "not"));
    }

    /**
     *
     */
    public void close() {
        synchronized (this.sessions) {
            while (!this.sessions.isEmpty()) {
                this.sessions.remove(0).close();
            }
        }
    }

    /**
     * @inheritDoc
     * @see org.eclipse.jetty.websocket.servlet.WebSocketCreator#
     * createWebSocket(org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest,
     * org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse)
     */
    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        AndroidWebSocketWrapper wrapper = new AndroidWebSocketWrapper(this.mediator, endpoint);
        if (wrapper != null) {
            synchronized (this.sessions) {
                this.sessions.add(wrapper);
            }
        }
        return wrapper;
    }
}
