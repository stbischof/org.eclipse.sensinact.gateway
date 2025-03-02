/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.core.security;

import org.eclipse.sensinact.gateway.core.SensiNactResourceModel;
import org.eclipse.sensinact.gateway.core.Session;
import org.eclipse.sensinact.gateway.datastore.api.DataStoreException;
import org.eclipse.sensinact.gateway.util.tree.PathTree;

/**
 * A secured {@link Session}s provider service
 * 
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public interface SecuredAccess {
	/**
	 * 
	 * @param bundleIdentifier
	 * 
	 * @return
	 * @throws SecuredAccessException
	 * @throws DataStoreException
	 */
	String getAgentPublicKey(String bundleIdentifier) throws SecuredAccessException, DataStoreException;

	/**
	 * @param privateKey
	 * @return
	 * @throws SecuredAccessException
	 */
	String getApplicationPublicKey(String privateKey) throws SecuredAccessException;

	/**
	 * Creates the {@link AccessNode}s hierarchy for the
	 * {@link SensiNactResourceModel} whose holding bundle's identifier and name are
	 * passed as parameters, and attaches it to the parent root node also passed as
	 * parameter
	 * 
	 * @param identifier
	 *            the String identifier of the Bundle holding the specified
	 *            {@link SensiNactResourceModel} for which to create the
	 *            {@link AccessNode}s hierarchy
	 * 
	 * @param name
	 *            the name of the {@link SensiNactResourceModel} for which to create
	 *            the {@link AccessNode}s hierarchy
	 * 
	 * @param accessTree
	 *            the {@link AccessNode}s {@link PathTree} on which to attach the
	 *            {@link AccessNode} to be created
	 * 
	 * @throws SecuredAccessException
	 */
	void buildAccessNodesHierarchy(String identifier, String name,
			MutableAccessTree<? extends MutableAccessNode> accessTree) throws SecuredAccessException;

	/**
	 * Returns the {@link AccessTreeImpl} for the Bundle whose identifier is passed
	 * as parameter
	 * 
	 * @param identifier
	 *            the String identifier of the Bundle for which to return the
	 *            corresponding {@link AccessTreeImpl}
	 * 
	 * @return the {@link AccessTree} of {@link AccessNode}s for the specified the
	 *         Bundle
	 * 
	 * @throws SecuredAccessException
	 */
	MutableAccessTree<? extends MutableAccessNode> getAccessTree(String identifier) throws SecuredAccessException;

	/**
	 * Returns the {@link AccessTreeImpl} for the user whose String public key is
	 * passed as parameter
	 * 
	 * @param publicKey
	 *            the String public key of the user for who to return the
	 *            corresponding {@link AccessTree}
	 * 
	 * @return the {@link AccessTree} of {@link AccessNode}s for the specified the
	 *         user
	 * 
	 * @throws SecuredAccessException
	 */
	AccessTree<? extends AccessNode> getUserAccessTree(String publicKey) throws SecuredAccessException;

	/**
	 * Returns the {@link AccessTreeImpl} for the Application whose String public
	 * key is passed as parameter
	 * 
	 * @param publicKey
	 *            the String public key of the Application for which to return the
	 *            corresponding {@link AccessTreeImpl}
	 * 
	 * @return the {@link AccessTree} of {@link AccessNode}s for the specified the
	 *         Application
	 * 
	 * @throws SecuredAccessException
	 */
	AccessTree<? extends AccessNode> getApplicationAccessTree(String publicKey) throws SecuredAccessException;

}
