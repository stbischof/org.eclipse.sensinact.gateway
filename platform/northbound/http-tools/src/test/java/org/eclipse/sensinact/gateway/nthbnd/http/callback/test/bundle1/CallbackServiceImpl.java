/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.nthbnd.http.callback.test.bundle1;

import java.util.Dictionary;

import javax.servlet.http.HttpServletRequestWrapper;

import org.eclipse.sensinact.gateway.nthbnd.http.callback.CallbackContext;
import org.eclipse.sensinact.gateway.nthbnd.http.callback.CallbackService;
import org.osgi.test.common.dictionary.Dictionaries;

/**
 *
 */
public class CallbackServiceImpl implements CallbackService {
    /**
     *
     */
    public CallbackServiceImpl() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.sensinact.gateway.nthbnd.forward.http.ForwardingService#getPattern()
     */
    @Override
    public String getPattern() {
        return "/callbackTest1/*";
    }

    /* (non-Javadoc)
     * @see org.eclipse.sensinact.gateway.nthbnd.forward.http.ForwardingService#getProperties()
     */
    @Override
    public Dictionary<String, ?> getProperties() {
        return Dictionaries.dictionaryOf("pattern", getPattern());
    }

	@Override
	public void process(CallbackContext context) {
		try {
            context.getResponse().setContent(("[" + ((HttpServletRequestWrapper)context.getRequest()).getMethod() + "]" + context.getRequest().getRequestURI()).getBytes());
            context.getResponse().setResponseStatus(200);
            context.getResponse().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public int getCallbackType() {
		return CallbackService.CALLBACK_SERVLET;
	}
}
