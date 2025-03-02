/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.sthbnd.http.smpl;

import org.eclipse.sensinact.gateway.common.bundle.Mediator;
import org.eclipse.sensinact.gateway.common.execution.Executable;
import org.eclipse.sensinact.gateway.sthbnd.http.task.HttpTask;
import org.eclipse.sensinact.gateway.util.CastUtils;
import org.eclipse.sensinact.gateway.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DefaultHttpTaskProcessingContext implements HttpTaskProcessingContext {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultHttpTaskProcessingContext.class);
    protected Mediator mediator;
    protected Map<String, Executable<Void, String>> properties;
    private HttpTaskConfigurator httpTaskConfigurator;

    /**
     * @param mediator
     * @param task
     */
    public DefaultHttpTaskProcessingContext(Mediator mediator, HttpTaskConfigurator httpTaskConfigurator, final String endpointId, final HttpTask<?, ?> task) {
        this.mediator = mediator;
        this.httpTaskConfigurator = httpTaskConfigurator;

        this.properties = new HashMap<String, Executable<Void, String>>();
        this.properties.put("endpoint.id", new Executable<Void, String>() {
            @Override
            public String execute(Void parameter) throws Exception {
                return endpointId;
            }
        });
        this.properties.put("task.result", new Executable<Void, String>() {
            @Override
            public String execute(Void parameter) throws Exception {
                return CastUtils.cast(String.class, task.getResult());
            }
        });
        this.properties.put("task.path", new Executable<Void, String>() {
            @Override
            public String execute(Void parameter) throws Exception {
                return task.getPath();
            }
        });
        this.properties.put("task.profile", new Executable<Void, String>() {
            @Override
            public String execute(Void parameter) throws Exception {
                return task.getProfile();
            }
        });
        this.properties.put("task.serviceProvider", new Executable<Void, String>() {
            @Override
            public String execute(Void parameter) throws Exception {
                String[] uriElements = UriUtils.getUriElements(task.getPath());
                if (uriElements.length > 0) {
                    return uriElements[0];
                }
                return null;
            }
        });
        this.properties.put("task.service", new Executable<Void, String>() {
            @Override
            public String execute(Void parameter) throws Exception {
                String[] uriElements = UriUtils.getUriElements(task.getPath());
                if (uriElements.length > 1) {
                    return uriElements[1];
                }
                return null;
            }
        });
        this.properties.put("task.resource", new Executable<Void, String>() {
            @Override
            public String execute(Void parameter) throws Exception {
                String[] uriElements = UriUtils.getUriElements(task.getPath());
                if (uriElements.length > 2) {
                    return uriElements[2];
                }
                return null;
            }
        });
    }

    @Override
    public String resolve(String property) {
        Executable<Void, String> executable = this.properties.get(property);
        if (executable != null) {
            try {
                return executable.execute(null);

            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public HttpTaskConfigurator getHttpTaskConfigurator() {
        return this.httpTaskConfigurator;
    }
}
