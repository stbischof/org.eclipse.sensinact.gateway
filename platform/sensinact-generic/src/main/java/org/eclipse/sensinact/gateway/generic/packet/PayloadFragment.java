/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.generic.packet;

import org.eclipse.sensinact.gateway.core.ServiceProviderProcessableData;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A embedded set of bytes targeting one {@link ServiceProvider}
 * in a communication {@link Packet}
 *
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public interface PayloadFragment extends ServiceProviderProcessableData<PayloadServiceFragment> {
	
	public static final PayloadFragment EOF_FRAGMENT = new PayloadFragment() {

		@Override
		public String getServiceProviderIdentifier() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public Iterator<PayloadServiceFragment> iterator() {
			return Collections.emptyListIterator();
		}

		@Override
		public String getProfileId() {
			return null;
		}

		@Override
		public boolean isHelloMessage() {
			return false;
		}

		@Override
		public boolean isGoodByeMessage() {
			return false;
		}

		@Override
		public List<TaskIdValuePair> getTaskIdValuePairs() {
			return null;
		}

		@Override
		public boolean treated(String taskIdentifier) {
			return true;
		}

		@Override
		public int size() {
			return 0;
		}
	};
	
    /**
     * Returns the string identifier of the profile of the
     * {@link ServiceProvider} targeted by this {@link PayloadFragment}
     *
     * @return the targeted {@link ServiceProvider}'s profile
     */
    String getProfileId();

    /**
     * Returns true if this SubPacket is an "Hello" message,
     * meaning that the targeted {@link ServiceProvider} is
     * connecting to the network; Returns false otherwise
     *
     * @return <ul><li>
     * true if this SubPacket is a "Hello" message
     * </li>
     * <li>false otherwise</li>
     * </ul>
     */
    public boolean isHelloMessage();

    /**
     * Returns true if this SubPacket is an "Goodbye" message,
     * meaning that the targeted {@link ServiceProvider} is
     * disconnecting from the network; Returns false otherwise
     *
     * @return <ul><li>
     * true if this SubPacket is a "Goodbye" message
     * </li>
     * <li>false otherwise</li>
     * </ul>
     */
    public boolean isGoodByeMessage();

    /**
     * Returns the list of all {@link TaskIdValuePair}s that
     * can be created for this SubPacket
     *
     * @return the list of this SubPacket's {@link TaskIdValuePair}s
     */
    public List<TaskIdValuePair> getTaskIdValuePairs();

    /**
     * This {@link PayloadFragment} is informed that the {@link Task}
     * whose identifier is passed as parameter has been treated. The
     * associated {@link PayloadResourceFragment} is removed from its
     * {@link PayloadServiceFragment} holder to avoid a redundant treatment
     *
     * @param taskIdentifier the String identifier of the treated {@link Task}
     * @return <ul>
     * <li>
     * true if the associated {@link PayloadResourceFragment}
     * has been deleted
     * </li>
     * <li>
     * false if no associated {@link PayloadResourceFragment}
     * can be found
     * </li>
     * </ul>
     */
    boolean treated(String taskIdentifier);

    /**
     * Returns the number of {@link PayloadServiceFragment} of
     * this SubPacket
     *
     * @return the number of {@link PayloadServiceFragment} of
     * this SubPacket
     */
    int size();
}
