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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.sensinact.gateway.core.security.entity.ObjectEntity;
import org.eclipse.sensinact.gateway.datastore.api.DataStoreException;
import org.eclipse.sensinact.gateway.datastore.api.DataStoreService;
import org.eclipse.sensinact.gateway.util.UriUtils;
import org.eclipse.sensinact.gateway.util.tree.PathNode;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Method DAO
 * 
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public class ObjectDAO extends AbstractMutableSnaDAO<ObjectEntity> {
	// ********************************************************************//
	// NESTED DECLARATIONS //
	// ********************************************************************//

	// ********************************************************************//
	// ABSTRACT DECLARATIONS //
	// ********************************************************************//

	// ********************************************************************//
	// STATIC DECLARATIONS //
	// ********************************************************************//
	private static final Logger LOG = LoggerFactory.getLogger(ObjectDAO.class);
	// ********************************************************************//
	// INSTANCE DECLARATIONS //
	// ********************************************************************//

	/**
	 * Constructor
	 * 
	 * @throws DAOException
	 */
	public ObjectDAO(DataStoreService dataStoreService) throws DAOException {
		this(dataStoreService,FrameworkUtil.getBundle(ObjectDAO.class).getResource("script/getObjectFromPath.sql"));
	}
	
	public ObjectDAO(DataStoreService dataStoreService,URL url) throws DAOException {
		super(ObjectEntity.class, dataStoreService);

		super.registerUserDefinedSelectStatement("getObjectFromPath",
				new UserDefinedSelectStatement(url));
	}

	/**
	 * Returns the {@link ObjectEntity}s from the datastore matching the given
	 * String path, otherwise null.
	 * 
	 * @param path
	 *            The String path of the {@link ObjectEntity} to be returned.
	 * 
	 * @return the {@link ObjectEntity}s list from the datastore matching the given
	 *         String path, otherwise null.
	 * 
	 * @throws DAOException
	 * @throws DataStoreException
	 */
	public List<ObjectEntity> find(String path) throws DAOException, DataStoreException {
		return find(path, false);
	}

	/**
	 * Returns the {@link ObjectEntity} from the datastore matching the given String
	 * path, otherwise null.
	 * 
	 * @param path
	 *            The String path of the {@link ObjectEntity} to be returned.
	 * @param exact
	 *            returns the last {@link ObjectEntity} matching the path if false
	 *            or the {@link ObjectEntity} matching the entire path only
	 *            otherwise
	 * 
	 * @return the {@link ObjectEntity} from the datastore matching the given String
	 *         path, otherwise null.
	 * @throws DAOException
	 * @throws DataStoreException
	 * 
	 */
	public List<ObjectEntity> find(String path, boolean exact) throws DAOException, DataStoreException {
		List<ObjectEntity> filtered = new ArrayList<ObjectEntity>();
		if (path == null) {
			return filtered;
		}
		List<ObjectEntity> objectEntities = super.select("getObjectFromPath", path);

		while (!objectEntities.isEmpty()) {
			ObjectEntity tmpEntity = objectEntities.remove(0);
			if (tmpEntity == null) {
				continue;
			}
			if (!exact || path.equals(tmpEntity.getPath())) {
				filtered.add(tmpEntity);
				continue;
			}
			String[] uriElements = UriUtils.getUriElements(path);

			String objectPath = tmpEntity.getPath();

			String[] objectPathElements = (objectPath != null)
					? UriUtils.getUriElements(objectPath.replace(PathNode.ANY_REGEX, PathNode.ANY_REPLACEMENT))
					: new String[0];

			if (uriElements.length == objectPathElements.length) {
				String lastSearched = uriElements[uriElements.length - 1];
				String lastFound = objectPathElements[uriElements.length - 1];
				try {
					if (lastFound.equals(PathNode.ANY_REPLACEMENT) || lastSearched.equals(lastFound)
							|| Pattern.matches(lastFound, lastSearched)) {
						filtered.add(tmpEntity);
					}
				} catch (PatternSyntaxException e) {
					LOG.debug("exact path '{}' not found", path);
				}
			}
		}
		return filtered;
	}

	/**
	 * Returns the list of {@link ObjectEntity}s from the datastore whose parent
	 * {@link ObjectEntity} matches the long identifier passed as parameter.
	 * 
	 * @param identifier
	 *            the long identifier of the parent {@link ObjectEntity} of those to
	 *            be returned.
	 * 
	 * @return the list of {@link ObjectEntity}s from the datastore whose parent
	 *         {@link ObjectEntity} matches the given long identifier.
	 * 
	 * @throws DAOException
	 * @throws DataStoreException
	 */
	public List<ObjectEntity> findChildren(final long identifier) throws DAOException, DataStoreException {
		return super.select(Collections.singletonMap("PARENT", identifier));
	}
}
