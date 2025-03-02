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

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.sensinact.gateway.commands.gogo.internal.CommandServiceMediator;
import org.eclipse.sensinact.gateway.core.method.AccessMethodResponse;
import org.eclipse.sensinact.gateway.core.method.DescribeResponse;
import org.eclipse.sensinact.gateway.core.security.InvalidCredentialException;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundAccess;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundEndpoint;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundMediator;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequest;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequestBuilder;
import org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequestWrapper.QueryKey;
import org.eclipse.sensinact.gateway.util.json.JsonProviderFactory;

import jakarta.json.JsonObject;

/**
 * Extended {@link NorthboundAccess} dedicated to shell access request
 * processing
 *
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public class ShellAccess extends NorthboundAccess<ShellAccessRequest> {
    public static void proceed(CommandServiceMediator mediator, JsonObject object) {
        try {
            ShellAccessRequest request = new ShellAccessRequest(mediator, object);
            new ShellAccess(request).proceed();

        } catch (InvalidCredentialException | IOException e) {
            mediator.getOutput().outputError(520, e.getMessage());
        }
    }

    protected NorthboundEndpoint endpoint;

    /**
     * Constructor
     *
     * @param request the {@link ShellAccessRequest} that will be treated
     *                by the ShellAccess to be instantiated
     * @throws IOException
     * @throws InvalidCredentialException
     */
    public ShellAccess(ShellAccessRequest request) throws IOException, InvalidCredentialException {
        super(request);
        this.endpoint = ((CommandServiceMediator) super.mediator).getEndpoint();
    }

    /**
     * @inheritDoc
     * @see org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundAccess#
     * respond(org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundMediator, org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundRequestBuilder)
     */
    @Override
    protected boolean respond(NorthboundMediator mediator, NorthboundRequestBuilder builder) throws IOException {
        NorthboundRequest nthbndRequest = builder.build();
        if (nthbndRequest == null) {
            this.sendError(500, "Internal server error");
            return false;
        }
        AccessMethodResponse<?> cap = this.endpoint.execute(nthbndRequest);
        String resultStr = null;
        List<String> rawList = null;
        
        Map<QueryKey, List<String>> queryMap = super.request.getQueryMap();
        Iterator<QueryKey> iterator = queryMap.keySet().iterator();
        while(iterator.hasNext()) {
        	QueryKey queryKey = iterator.next();
        	if("rawDescribe".equals(queryKey.name)) {
        		rawList = queryMap.get(queryKey);        	
        		break;
        	}
        }
        if (rawList != null && (rawList.contains("true") || rawList.contains("True") || rawList.contains("yes") || rawList.contains("Yes")) && DescribeResponse.class.isAssignableFrom(cap.getClass())) {
            resultStr = ((DescribeResponse<?>) cap).getJSON(true);
        } else {
            resultStr = cap.getJSON();
        }
        JsonObject result = JsonProviderFactory.getProvider().createReader(new StringReader(resultStr)).readObject();
        if (result == null) {
            this.sendError(500, "Internal server error");
            return false;
        }
        ((CommandServiceMediator) super.mediator).getOutput().output(result, 0);
        return true;
    }

    /**
     * @inheritDoc
     * @see org.eclipse.sensinact.gateway.nthbnd.endpoint.NorthboundAccess#
     * sendError(int, java.lang.String)
     */
    @Override
    protected void sendError(int i, String message) throws IOException {
        ((CommandServiceMediator) super.mediator).getOutput().outputError(i, message);
    }
}
