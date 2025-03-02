/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.nthbnd.rest.internal.http;

import org.eclipse.sensinact.gateway.common.bundle.Mediator;
import org.eclipse.sensinact.gateway.core.message.SnaMessage;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRecipient;
import org.eclipse.sensinact.gateway.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class is a wrapper for long polling callback subscription
 */
public class LongPollingRecipient extends NorthboundRecipient {
	
	private static final Logger LOG = LoggerFactory.getLogger(LongPollingRecipient.class);
    private HttpServletResponse response;

    /**
     * Constructor
     *
     * @param response
     * @param mediator
     */
    public LongPollingRecipient(HttpServletResponse response, Mediator mediator) {
        super(mediator);
        this.response = response;
    }

    /**
     * @inheritDoc
     * @see Recipient#callback(String, SnaMessage[])
     */
    public void callback(String callbackId, SnaMessage<?>[] messages) {
        int index = 0;
        int length = messages == null ? 0 : messages.length;

        StringBuilder builder = new StringBuilder();
        builder.append(JSONUtils.OPEN_BRACKET);
        for (; index < length; index++) {
            builder.append(index == 0 ? "" : ",");
            builder.append(messages[index].getJSON());
        }
        builder.append(JSONUtils.CLOSE_BRACKET);

        try {
            this.response.setStatus(200);
            this.response.getWriter().println(builder.toString());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            ;
        }
    }
}
