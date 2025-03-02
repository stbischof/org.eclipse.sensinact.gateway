/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.nthbnd.http.forward.internal;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.eclipse.sensinact.gateway.common.bundle.Mediator;
import org.eclipse.sensinact.gateway.common.execution.Executable;
import org.eclipse.sensinact.gateway.nthbnd.http.forward.ForwardingService;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.annotations.RequireHttpWhiteboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ForwardingFactory is in charge of creating the {@link ForwardingFilter}s attached
 * to one specific {@link ExtHttpService}, and configured by the {@link ForwardingService}s
 * registered in the OSGi host environment
 *
 * @author <a href="mailto:cmunilla@kentyou.com">Christophe Munilla</a>
 */
@RequireHttpWhiteboard
public class ForwardingFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(ForwardingFactory.class);
    private Mediator mediator;
    private String appearingKey;
    private String disappearingKey;

    private Map<String, ServiceRegistration<?>[]> registrations;

    private final AtomicBoolean running;

    /**
     * Constructor
     *
     * @param mediator       the {@link Mediator} allowing the ForwardingFactory
     *                       to be instantiated to interact with the OSGi host environment
     */
    public ForwardingFactory(Mediator mediator) {
        this.mediator = mediator;
        this.registrations = Collections.synchronizedMap(new HashMap<String, ServiceRegistration<?>[]>());
        this.running = new AtomicBoolean(false);
    }

    /**
     * Starts this ForwardingFactory and starts to observe the registration and
     * the unregistration of the {@link ForwardingService}s
     */
    public void start() {
        if (this.running.get()) {
            return;
        }
        this.running.set(true);
        attachAll();
        this.appearingKey = mediator.attachOnServiceAppearing(ForwardingService.class, (String) null, new Executable<ForwardingService, Void>() {
            @Override
            public Void execute(ForwardingService forwardingService) throws Exception {
                attach(forwardingService);
                return null;
            }
        });
        this.disappearingKey = mediator.attachOnServiceDisappearing(ForwardingService.class, (String) null, new Executable<ForwardingService, Void>() {
            @Override
            public Void execute(ForwardingService forwardingService) throws Exception {
                detach(forwardingService);
                return null;
            }
        });
    }

    /**
     * Stops this ForwardingFactory and stops to observe the registration and
     * the unregistration of the {@link ForwardingService}s
     */
    public void stop() {
        if (!this.running.get()) {
            return;
        }
        this.running.set(false);
        mediator.detachOnServiceAppearing(ForwardingService.class, (String) null, appearingKey);
        mediator.detachOnServiceDisappearing(ForwardingService.class, (String) null, disappearingKey);
        detachAll();
    }

    /**
     * Detaches all the {@link ForwardingService}s registered into the
     * OSGi host environment
     */
    public void detachAll() {
        mediator.callServices(ForwardingService.class, new Executable<ForwardingService, Void>() {
            /**
             * @inheritDoc
             *
             * @see org.eclipse.sensinact.gateway.common.execution.Executable#
             * execute(java.lang.Object)
             */
            @Override
            public Void execute(ForwardingService forwardingService) throws Exception {
                detach(forwardingService);
                return null;
            }
        });
    }

    /**
     * Attaches all the {@link ForwardingService}s registered into the
     * OSGi host environment
     */
    public void attachAll() {
        mediator.callServices(ForwardingService.class, new Executable<ForwardingService, Void>() {
            /**
             * @inheritDoc
             *
             * @see org.eclipse.sensinact.gateway.common.execution.Executable#
             * execute(java.lang.Object)
             */
            @Override
            public Void execute(ForwardingService forwardingService) throws Exception {
                attach(forwardingService);
                return null;
            }
        });
    }

    /**
     * Attaches the {@link ForwardingService} passed as parameter by
     * registering a newly created {@link ForwardingFilter} based on it
     *
     * @param forwardingService the {@link ForwardingService} to be attached
     */
	public final void attach(ForwardingService forwardingService) {
        if (forwardingService == null || !this.running.get()) {
            return;
        }
        String endpoint = forwardingService.getPattern();
        if (endpoint == null || endpoint.length() == 0 || "/".equals(endpoint)) {
            LOG.error("Invalid endpoint '{}' - expected '^|/([^/]+)(/([^/]+)*'", endpoint);
            return;
        }
        if (!endpoint.startsWith("/")) {
            endpoint = "/".concat(endpoint);
        }
        if (registrations.containsKey(endpoint)) {
            LOG.error("A forwarding service is already registered at '{}'", endpoint);
            return;
        }
        ForwardingFilter forwardingFilter = new ForwardingFilter(forwardingService);
        
        Dictionary<String, Object> propsGiven = forwardingService.getProperties();

        Dictionary<String, Object>props = new Hashtable<>();
        Enumeration<String> enumKeys=propsGiven.keys();
        while(enumKeys.hasMoreElements()) {
        	String key=enumKeys.nextElement();
        	props.put(key, propsGiven.get(key));
        }
        props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, endpoint);
        props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,"("+HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME+"=default)");
        
	    ServiceRegistration<?>[] registrations = new ServiceRegistration[2];
	    registrations[0] = mediator.getContext().registerService(Filter.class, forwardingFilter, props);
	    
        props = new Hashtable<>();
        props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, endpoint);
        props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,"("+HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME+"=default)");
        
	    registrations[1] = mediator.getContext().registerService(Servlet.class, new ForwardingServlet(), props);
	    
	    this.registrations.put(endpoint,registrations);
    }
	    

    /**
     * Detaches the {@link ForwardingService} passed as parameter by
     * unregistering the {@link ForwardingFilter} that is based on it
     *
     * @param forwardingService the {@link ForwardingService} to be detached
     */
    public final void detach(ForwardingService forwardingService) {
        if (forwardingService == null) {
            return;
        }
        String endpoint = forwardingService.getPattern();
        ServiceRegistration<?>[] registrations = this.registrations.remove(endpoint);
    	if(registrations != null) {
    		for(ServiceRegistration<?> registration : registrations) {
	    		try {
	    			registration.unregister();
	    		}catch(IllegalStateException e) {
	    			//do nothing
	    		}
    		}
    		LOG.info("Forwarding filter and servlet  for '{}' pattern are unregistered", endpoint);
    	}	        
    }
}
