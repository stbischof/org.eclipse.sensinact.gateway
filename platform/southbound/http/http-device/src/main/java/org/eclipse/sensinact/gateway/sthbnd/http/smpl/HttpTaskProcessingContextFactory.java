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

import org.eclipse.sensinact.gateway.sthbnd.http.task.HttpTask;

/**
 * A factory of {@link HttpTaskProcessingContext} for {@link HttpTask}s
 *
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public interface HttpTaskProcessingContextFactory {
    /**
     * Creates and returns a {@link HttpTaskProcessingContext} for the
     * {@link HttpTask} passed as parameter
     *
     * @param httpTaskConfigurator
     * @param endpointId           the string identifier of the {@link
     *                             ProtocolStackEndpoint} that instantiated the {@link HttpTask}
     *                             for which to create a  new {@link HttpTaskProcesingContext}
     * @param task                 the {@link HttpTask} for which to create
     *                             a new {@link HttpTaskProcessingContext}
     * @return a new {@link HttpTaskProcessingContext} for the
     * specified {@link HttpTask}
     */
    HttpTaskProcessingContext newInstance(HttpTaskConfigurator httpTaskConfigurator, String endpointId, HttpTask<?, ?> task);
}
