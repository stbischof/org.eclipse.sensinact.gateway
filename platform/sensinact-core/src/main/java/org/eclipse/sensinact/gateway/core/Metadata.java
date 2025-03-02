/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.core;

import org.eclipse.sensinact.gateway.common.bundle.Mediator;
import org.eclipse.sensinact.gateway.common.constraint.Constraint;
import org.eclipse.sensinact.gateway.common.constraint.InvalidConstraintDefinitionException;
import org.eclipse.sensinact.gateway.common.primitive.DescribablePrimitive;
import org.eclipse.sensinact.gateway.common.primitive.InvalidValueException;
import org.eclipse.sensinact.gateway.common.primitive.InvalidValueTypeException;
import org.eclipse.sensinact.gateway.common.primitive.Modifiable;
import org.eclipse.sensinact.gateway.common.primitive.Primitive;

import jakarta.json.JsonObject;

/**
 * Extended {@link Primitive} defining a Metadata
 * 
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public class Metadata extends DescribablePrimitive {
	/*
	 * Metadata constants
	 */
	public static final String HIDDEN = "hidden";
	public static final String MODIFIABLE = "modifiable";
	public static final String TIMESTAMP = "timestamp";
	public final static String DEFAULT = "default";
	public final static String DESCRIPTION = "description";
	public final static String UNIT = "unit";
	public final static String MIN = "min";
	public final static String MAX = "max";
	public static final String CONSTRAINTS = "constraints";
	public static final String NOTIFY = "notify";
	public static final String LOCKED = "locked";

	private Modifiable modifiable = Modifiable.UPDATABLE;

	/**
	 * Constructs a metadata with the given name, type and value.
	 * 
	 * @param name
	 *            the metadata name
	 * @param type
	 *            the metadata type
	 * @param value
	 *            the metadata value
	 * @param modifiable
	 *            is the Metadata dynamic or not ?
	 * 
	 * @throws InvalidConstraintDefinitionException
	 */
	public Metadata(Mediator mediator, String name, Class<?> type, Object value, Modifiable modifiable)
			throws InvalidValueException {
		super(mediator, name, type, value);
		this.modifiable = modifiable;
	}

	/**
	 * Constructs a Metadata using the given {@link JsonObject}
	 * 
	 * @param metadata the {@link JsonObject} describing the Metadata to instantiate
	 * 
	 * @throws InvalidConstraintDefinitionException
	 */
	public Metadata(Mediator mediator, JsonObject metadata) throws InvalidValueException {
		super(mediator, metadata);
		try {
			this.modifiable = Modifiable.valueOf(metadata.getString(Metadata.MODIFIABLE));
		} catch (Exception e) {
			this.modifiable = Modifiable.FIXED;
		}
	}

	@Override
	public Modifiable getModifiable() {
		return this.modifiable;
	}

	@Override
	protected MetadataDescription createDescription() {
		return new MetadataDescription(this);
	}

	@Override
	protected void checkType(Class<?> type) throws InvalidValueTypeException {
		if ((!type.isArray() || !Constraint.class.isAssignableFrom(type.getComponentType()))) {
			super.checkType(type);
		}
	}
}
