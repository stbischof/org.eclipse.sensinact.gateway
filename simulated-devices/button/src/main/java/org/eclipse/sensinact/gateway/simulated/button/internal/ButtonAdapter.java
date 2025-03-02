/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.simulated.button.internal;

import org.eclipse.sensinact.gateway.generic.local.LocalProtocolStackEndpoint;
import org.eclipse.sensinact.gateway.generic.packet.InvalidPacketException;

/**
 *
 */
public class ButtonAdapter {
    private final LocalProtocolStackEndpoint<ButtonPacket> connector;

    /**
     * @param connector
     */
    public ButtonAdapter(LocalProtocolStackEndpoint<ButtonPacket> connector) {
        this.connector = connector;
    }

    /**
     * @param value
     */
    public void mouseReleased(boolean value) {
        try {
            connector.process(new ButtonPacket(value));
        } catch (InvalidPacketException e) {
            e.printStackTrace();
        }
    }
}
