/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.app.api.persistence.listener;

public interface ApplicationAvailabilityListener {
    void serviceOffline();

    void serviceOnline();

    void applicationFound(String applicationName, String content);

    void applicationChanged(String applicationName, String content);

    void applicationRemoved(String applicationName);
}
