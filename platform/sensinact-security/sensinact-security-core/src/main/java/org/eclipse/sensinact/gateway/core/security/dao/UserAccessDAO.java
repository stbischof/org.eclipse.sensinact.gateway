/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.core.security.dao;

import java.util.Collections;
import java.util.List;

import org.eclipse.sensinact.gateway.core.security.entity.UserAccessEntity;
import org.eclipse.sensinact.gateway.datastore.api.DataStoreException;
import org.eclipse.sensinact.gateway.datastore.api.DataStoreService;

/**
 * Method DAO
 * 
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public class UserAccessDAO extends AbstractImmutableSnaDAO<UserAccessEntity> {
	// ********************************************************************//
	// NESTED DECLARATIONS //
	// ********************************************************************//

	// ********************************************************************//
	// ABSTRACT DECLARATIONS //
	// ********************************************************************//

	// ********************************************************************//
	// STATIC DECLARATIONS //
	// ********************************************************************//

	// ********************************************************************//
	// INSTANCE DECLARATIONS //
	// ********************************************************************//

	/**
	 * Constructor
	 * 
	 */
	UserAccessDAO(DataStoreService dataStoreService) throws DAOException {
		super(UserAccessEntity.class, dataStoreService);
	}

	/**
	 * Returns the {@link UserAccessEntity} from the datastore matching the given
	 * String publicKey, otherwise null.
	 * 
	 * @param identifier
	 *            the long identifier of the {@link UserAccessEntity} to be
	 *            returned.
	 * @return the {@link UserAccessEntity} from the datastore matching the given
	 *         long identifier, otherwise null.
	 * @throws DAOException
	 * @throws DataStoreException
	 */
	public UserAccessEntity find(final long identifier) throws DAOException, DataStoreException {
		List<UserAccessEntity> userAccessEntities = super.select(Collections.singletonMap("UAID", identifier));
		if (userAccessEntities.size() != 1) {
			return null;
		}
		return userAccessEntities.get(0);
	}

	/**
	 * Returns the {@link UserAccessEntity} from the datastore matching the given
	 * String name, otherwise null.
	 * 
	 * @param name
	 *            the String name of the {@link UserAccessEntity} to be returned.
	 * @return the {@link UserAccessEntity} from the datastore matching the given
	 *         String name, otherwise null.
	 * @throws DAOException
	 * @throws DataStoreException
	 */
	public UserAccessEntity find(final String name) throws DAOException, DataStoreException {
		List<UserAccessEntity> userAccessEntities = super.select(Collections.singletonMap("UANAME", name));
		if (userAccessEntities.size() != 1) {
			return null;
		}
		return userAccessEntities.get(0);
	}

	/**
	 * Returns the {@link UserAccessEntity} from the datastore matching the given
	 * integer access level, otherwise null.
	 * 
	 * @param level
	 *            the integer access level of the {@link UserAccessEntity} to be
	 *            returned.
	 * @return the {@link UserAccessEntity} from the datastore matching the given
	 *         integer access level, otherwise null.
	 * @throws DataStoreException
	 */
	public UserAccessEntity find(final int level) throws DAOException, DataStoreException {
		List<UserAccessEntity> userAccessEntities = super.select(Collections.singletonMap("UALEVEL", level));
		if (userAccessEntities.size() != 1) {
			return null;
		}
		return userAccessEntities.get(0);
	}
}
