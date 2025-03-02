/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.core.security.impl;

import org.eclipse.sensinact.gateway.core.security.AccessNode;
import org.eclipse.sensinact.gateway.core.security.AccessProfileOption;
import org.eclipse.sensinact.gateway.core.security.AccessTree;
import org.eclipse.sensinact.gateway.core.security.AccessTreeImpl;
import org.eclipse.sensinact.gateway.core.security.ImmutableAccessNode;
import org.eclipse.sensinact.gateway.core.security.ImmutableAccessTree;
import org.eclipse.sensinact.gateway.core.security.MutableAccessNode;
import org.eclipse.sensinact.gateway.core.security.MutableAccessTree;
import org.eclipse.sensinact.gateway.core.security.SecuredAccess;
import org.eclipse.sensinact.gateway.core.security.SecuredAccessException;
import org.eclipse.sensinact.gateway.core.security.UserManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SecuredAccessImpl implements SecuredAccess {
	
	private static final Logger LOG = LoggerFactory.getLogger(SecuredAccessImpl.class);

	/**
	 * Constructor
	 * 
	 */
	@Activate
	public SecuredAccessImpl(BundleContext ctx) {
	}

	@Override
	public String getAgentPublicKey(String bundleIdentifier) throws SecuredAccessException {
		return UserManager.ANONYMOUS_PKEY;
	}

	@Override
	public AccessTree<? extends AccessNode> getUserAccessTree(String publicKey) throws SecuredAccessException {
		ImmutableAccessNode root = new ImmutableAccessNode(null, "/", false, null,
				AccessProfileOption.DEFAULT.getAccessProfile());
		ImmutableAccessTree accessTree = new ImmutableAccessTree(root);
		return accessTree;
	}

	@Override
	public String getApplicationPublicKey(String privateKey) throws SecuredAccessException {
		return UserManager.ANONYMOUS_PKEY;
	}

	@Override
	public AccessTree<? extends AccessNode> getApplicationAccessTree(String publicKey) throws SecuredAccessException {
		ImmutableAccessNode root = new ImmutableAccessNode(null, "/", false, null,
				AccessProfileOption.DEFAULT.getAccessProfile());
		ImmutableAccessTree accessTree = new ImmutableAccessTree(root);
		return accessTree;
	}

	@Override
	public MutableAccessTree<? extends MutableAccessNode> getAccessTree(String identifier)
			throws SecuredAccessException {
		MutableAccessTree<? extends MutableAccessNode> accessTree = new AccessTreeImpl<>()
				.withAccessProfile(AccessProfileOption.ALL_ANONYMOUS);
		return accessTree;
	}
	
	@Override
	public void buildAccessNodesHierarchy(String identifier, String name,
			MutableAccessTree<? extends MutableAccessNode> tree) throws SecuredAccessException {
		// do nothing
	}

	@Deactivate
	void close() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("closing sensiNact secured access");
		}
	}
}