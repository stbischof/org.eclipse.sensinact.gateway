/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.nthbnd.endpoint;

import org.eclipse.sensinact.gateway.common.execution.DefaultErrorHandler;
import org.eclipse.sensinact.gateway.common.execution.ErrorHandler;
import org.eclipse.sensinact.gateway.core.message.AbstractMidAgentCallback;
import org.eclipse.sensinact.gateway.core.message.MidCallbackException;
import org.eclipse.sensinact.gateway.core.message.SnaErrorMessageImpl;
import org.eclipse.sensinact.gateway.core.message.SnaFilter;
import org.eclipse.sensinact.gateway.core.message.SnaLifecycleMessageImpl;
import org.eclipse.sensinact.gateway.core.message.SnaMessage;
import org.eclipse.sensinact.gateway.core.message.SnaResponseMessage;
import org.eclipse.sensinact.gateway.core.message.SnaUpdateMessageImpl;
import org.eclipse.sensinact.gateway.util.UriUtils;

public class RegisterAgentRequest extends NorthboundRequest {
	
    private NorthboundRecipient recipient;
    private String serviceProvider;
    private String service;
    private SnaFilter filter;
	private String policy ;

    /**
     * Constructor
     *
     * @param mediator
     * @param serviceProvider
     * @param service
     * @param recipient
     * @param constraints
     */
    public RegisterAgentRequest(String requestIdentifier, String serviceProvider,
    		String service, NorthboundRecipient recipient, SnaFilter filter, String policy) {
        super(requestIdentifier, null);
        this.serviceProvider = serviceProvider;
        this.service = service;
        this.recipient = recipient;
        this.filter = filter;
        this.policy = policy;
        if (this.recipient == null) {
            throw new NullPointerException("Recipient missing");
        }
    }

    /**
     * @inheritDoc
     * @see ServiceProvidersRequest#getExecutionArguments()
     */
    @Override
    protected Argument[] getExecutionArguments() {
        AbstractMidAgentCallback callback = new AbstractMidAgentCallback() {
            @Override
            public void doHandle(SnaLifecycleMessageImpl message) throws MidCallbackException  {
                try {
					RegisterAgentRequest.this.recipient.callback(
							this.getName(), new SnaMessage[] {message});
				} catch (Exception e) {
					throw new MidCallbackException(e);
				}
            }

            @Override
            public void doHandle(SnaUpdateMessageImpl message) throws MidCallbackException {
            	try {
					RegisterAgentRequest.this.recipient.callback(
							this.getName(), new SnaMessage[] {message});
				} catch (Exception e) {
					throw new MidCallbackException(e);
				}
            }

            @Override
            public void doHandle(SnaErrorMessageImpl message) throws MidCallbackException {
            	try {
					RegisterAgentRequest.this.recipient.callback(
							this.getName(), new SnaMessage[] {message});
				} catch (Exception e) {
					throw new MidCallbackException(e);
				}
            }

            @Override
            public void doHandle(SnaResponseMessage<?, ?> message) throws MidCallbackException {
            	try {
					RegisterAgentRequest.this.recipient.callback(
							this.getName(), new SnaMessage[] {message});
				} catch (Exception e) {
					throw new MidCallbackException(e);
				}
            }
        };
        int intPolicy = 0x000000;
        try {
			intPolicy = Integer.parseInt(policy);
		} catch(NumberFormatException e) {			
			String[] policies = policy.split("|");
			for(int index = 0;index < policies.length;index++) {
				switch((policies[index]).trim()) {
				case "CONTINUE" : intPolicy |= ErrorHandler.Policy.CONTINUE;
				break;
				case "STOP" : intPolicy |= ErrorHandler.Policy.STOP;
				break;
				case "ROLLBACK" : intPolicy |= ErrorHandler.Policy.ROLLBACK;
				break;
				case "IGNORE" : intPolicy |= ErrorHandler.Policy.IGNORE;
				break;
				case "ALTERNATIVE" : intPolicy |= ErrorHandler.Policy.ALTERNATIVE;
				break;
				case "LOG" : intPolicy |= ErrorHandler.Policy.LOG;
				break;
				}								
			}
		}
        callback.setErrorHandler(new DefaultErrorHandler(intPolicy));
        Argument[] superArguments = super.getExecutionArguments();
        int length = superArguments == null ? 0 : superArguments.length;
        Argument[] arguments = new Argument[length + 2];
        if (length > 0) {
            System.arraycopy(superArguments, 0, arguments, 0, length);
        }
        arguments[length] = new Argument(AbstractMidAgentCallback.class, callback);
        arguments[length + 1] = new Argument(SnaFilter.class, filter);
        return arguments;
    }

    /**
     * @inheritDoc
     * @see ResourceRequest#getMethod()
     */
    @Override
    protected String getMethod() {
        return "registerAgent";
    }

    /**
     * @inheritDoc
     * @see org.eclipse.sensinact.gateway.common.primitive.Nameable#getName()
     */
    @Override
    public String getName() {
        return UriUtils.PATH_SEPARATOR;
    }
}
