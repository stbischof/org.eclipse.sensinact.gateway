/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.app.manager.application;

import org.eclipse.sensinact.gateway.app.api.exception.ResourceNotFoundException;
import org.eclipse.sensinact.gateway.app.api.lifecycle.ApplicationStatus;
import org.eclipse.sensinact.gateway.app.manager.AppConstant;
import org.eclipse.sensinact.gateway.core.Attribute;
import org.eclipse.sensinact.gateway.core.DataResource;
import org.eclipse.sensinact.gateway.core.ResourceImpl;
import org.eclipse.sensinact.gateway.core.message.SnaErrorMessage;
import org.eclipse.sensinact.gateway.core.method.AccessMethodExecutor;
import org.eclipse.sensinact.gateway.core.method.AccessMethodResponseBuilder;
import org.eclipse.sensinact.gateway.util.json.JsonProviderFactory;

/**
 * @author Remi Druilhe
 * @see AccessMethodExecutor
 */
class AppStopExecutor implements AccessMethodExecutor {
    private final ApplicationService service;

    AppStopExecutor(ApplicationService service) {
        this.service = service;
    }

    /**
     * @see Executable#execute(java.lang.Object)
     */
    public Void execute(AccessMethodResponseBuilder jsonObjects) throws Exception {
        ApplicationStatus status = getApplicationState(service.getResource(AppConstant.STATUS));
        if (ApplicationStatus.ACTIVE.equals(status)) {
            Application application = service.getApplication();
            SnaErrorMessage message = application.stop();
            if (message.getType() == SnaErrorMessage.Error.NO_ERROR) {
                jsonObjects.push(JsonProviderFactory.getProvider().createObjectBuilder()
                		.add("message", "Application " + service.getName() + " stopped")
                		.build());
            } else {
                jsonObjects.registerException(new ResourceNotFoundException("Unable to stop the application: " + message.getJSON()));
            }
        } else {
            jsonObjects.push(JsonProviderFactory.getProvider().createObjectBuilder()
            		.add("message", "Unable to stop the application " + service.getName())
            		.build());
        }
        return null;
    }

    /**
     * Gets the current state of the application.
     */
    private ApplicationStatus getApplicationState(ResourceImpl state) {
        if (state != null) {
            Attribute stateAttribute = state.getAttribute(DataResource.VALUE);
            if (stateAttribute != null) {
                return (ApplicationStatus) stateAttribute.getValue();
            }
        }
        return null;
    }
}
