/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.core.method.builder;

import org.eclipse.sensinact.gateway.common.bundle.Mediator;
import org.eclipse.sensinact.gateway.common.execution.Executable;
import org.eclipse.sensinact.gateway.core.ResourceImpl;
import org.eclipse.sensinact.gateway.core.method.DynamicParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of a {@link DynamicParameterValue}
 * 
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public abstract class AbstractDynamicParameterValue implements DynamicParameterValue {
	private static final Logger LOG=LoggerFactory.getLogger(AbstractDynamicParameterValue.class);

	protected final String resourceName;
	protected final String parameterName;

	protected final Mediator mediator;
	private Executable<Void, Object> resourceValueExtractor;

	/**
	 * Constructor
	 * 
	 * @param mediator
	 * @param parameterName
	 * @param resourceName
	 */
	protected AbstractDynamicParameterValue(Mediator mediator, String parameterName, String resourceName) {
		this.mediator = mediator;
		this.parameterName = parameterName;
		this.resourceName = resourceName;
	}

	/**
	 * @InheritedDoc
	 *
	 * @see DynamicParameterValue#getResource()
	 */
	@Override
	public String getResource() {
		return this.resourceName;
	}

	/**
	 * Returns the Object value of the linked {@link ResourceImpl}
	 * 
	 * @return the linked {@link ResourceImpl}'s Object value
	 */
	public Object getResourceValue() {
		if (this.resourceValueExtractor != null) {
			try {
				return this.resourceValueExtractor.execute(null);
			} catch (Exception e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(),e);
				}
			}
		}
		return null;
	}

	/**
	 * @InheritedDoc
	 *
	 * @see DynamicParameterValue# setResourceValueExtractor(Executable)
	 */
	public void setResourceValueExtractor(Executable<Void, Object> resourceValueExtractor) {
		this.resourceValueExtractor = resourceValueExtractor;
	}
}
