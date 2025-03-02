/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.security.oauth2.osgi;

import java.util.Hashtable;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.eclipse.sensinact.gateway.common.bundle.AbstractActivator;
import org.eclipse.sensinact.gateway.common.bundle.Mediator;
import org.eclipse.sensinact.gateway.security.oauth2.OpenIDServer;
import org.eclipse.sensinact.gateway.security.oauth2.filter.SecurityFilter;
import org.eclipse.sensinact.gateway.security.oauth2.servlet.SecurityServlet;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;


/**
 * @see AbstractActivator
 */
@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class Activator extends AbstractActivator<Mediator> {

	public static final String AUTH_SECURITY_CONFIG = "org.eclipse.sensinact.security.oauth2.config";
	     
	/* (non-Javadoc)
	 * @see org.eclipse.sensinact.gateway.common.bundle.AbstractActivator#doStart()
	 */
	@Override
	public void doStart() throws Exception {
		String configfile = (String)super.mediator.getProperty(AUTH_SECURITY_CONFIG);		
		OpenIDServer oidcServer = new OpenIDServer(super.mediator.getContext(), configfile);		
		super.mediator.register(
			new SecurityFilter(oidcServer, oidcServer),
			Filter.class,
			new Hashtable<String, Object>() {
				private static final long serialVersionUID = 1L;
				{
				this.put(Constants.SERVICE_RANKING, 0);
				this.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/sensinact/*");	
				this.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,"("+HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME+"=default)");}}
			);		
	    super.mediator.register(
	    	new SecurityServlet(oidcServer, oidcServer), 
	    	Servlet.class,
	    	new Hashtable<String, Object>() {
	    		private static final long serialVersionUID = 1L;
	    		{
	    	    this.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/sensinact.auth");
				this.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,"("+HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME+"=default)");}}
	    	);
    }

	/* (non-Javadoc)
	 * @see org.eclipse.sensinact.gateway.common.bundle.AbstractActivator#doStop()
	 */
	@Override
    public void doStop() throws Exception {}

    /* (non-Javadoc)
     * @see org.eclipse.sensinact.gateway.common.bundle.AbstractActivator#doInstantiate(org.osgi.framework.BundleContext)
     */
    @Override
    public Mediator doInstantiate(BundleContext context) {
        Mediator mediator = new Mediator(context);
        return mediator;
    }
}
