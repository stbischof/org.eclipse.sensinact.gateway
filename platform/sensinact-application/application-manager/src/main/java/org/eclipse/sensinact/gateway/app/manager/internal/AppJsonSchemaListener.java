/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.app.manager.internal;

import org.eclipse.sensinact.gateway.app.api.plugin.PluginInstaller;
import org.eclipse.sensinact.gateway.app.manager.osgi.AppServiceMediator;
import org.eclipse.sensinact.gateway.common.primitive.InvalidValueException;
import org.eclipse.sensinact.gateway.core.DataResource;
import org.eclipse.sensinact.gateway.core.ResourceImpl;
import org.eclipse.sensinact.gateway.util.json.JsonProviderFactory;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.spi.JsonProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * This class provides the JSON Schema of the function in the plugin.
 *
 * @author Remi Druilhe
 */
class AppJsonSchemaListener implements ServiceListener {
    private final AppServiceMediator mediator;
    private final ResourceImpl resource;
    private static final String APP_INSTALL_HOOK_FILTER = "(objectClass=" + PluginInstaller.class.getCanonicalName() + ")";

    /**
     * Constructor
     *
     * @param mediator the mediator
     * @param resource the resource containing the json schemas
     */
    AppJsonSchemaListener(AppServiceMediator mediator, ResourceImpl resource) {
        this.mediator = mediator;
        this.resource = resource;
        try {
            this.resource.getAttribute(DataResource.VALUE).setValue(getJsonSchema());
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
        this.mediator.addServiceListener(this, APP_INSTALL_HOOK_FILTER);
    }

    /**
     * Stop the JsonSchemaListener by removing the ServiceListener
     */
    public void stop() {
        this.mediator.removeServiceListener(this);
    }

    /**
     * Monitor the appearance or disappearance of a plugin
     *
     * @param event the appearance or disappearance of a plugin
     * @see ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
     */
    public void serviceChanged(ServiceEvent event) {
        try {
            this.resource.getAttribute(DataResource.VALUE).setValue(getJsonSchema());
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

    private JsonArray getJsonSchema() {
        ServiceReference<?>[] serviceReferences = this.mediator.getServiceReferences(APP_INSTALL_HOOK_FILTER);
        JsonProvider provider = JsonProviderFactory.getProvider();
		JsonArrayBuilder pluginsKeywords = provider.createArrayBuilder();
        if (serviceReferences != null) {
            for (ServiceReference<?> serviceReference : serviceReferences) {
                Enumeration enumFile = serviceReference.getBundle().findEntries("/", "*.json", false);
                if (enumFile != null) {
                    while (enumFile.hasMoreElements()) {
                        try {
                            InputStream is = ((URL) enumFile.nextElement()).openStream();
                            int bufferLength = 1024;
                            int encodedLength = 0;
                            byte[] buffer = new byte[bufferLength];
                            byte[] encoded = new byte[encodedLength];
                            while (true) {
                                int read = is.read(buffer);
                                if (read <= 0) {
                                    break;
                                }
                                encoded = Arrays.copyOfRange(encoded, 0, encodedLength + read);
                                System.arraycopy(buffer, 0, encoded, encodedLength, read);
                                encodedLength += read;
                                if (read < bufferLength) {
                                    break;
                                }
                            }
                            pluginsKeywords.add(JsonProviderFactory.readObject(provider,
                            		new String(encoded, Charset.defaultCharset())));
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return pluginsKeywords.build();
    }
}
