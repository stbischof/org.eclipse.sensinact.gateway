/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.core.security.dao.directive;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.sensinact.gateway.core.security.dao.SnaDAO;
import org.eclipse.sensinact.gateway.core.security.entity.SnaEntity;
import org.eclipse.sensinact.gateway.core.security.entity.annotation.Column;
import org.eclipse.sensinact.gateway.core.security.entity.annotation.PrimaryKey;
import org.eclipse.sensinact.gateway.core.security.entity.annotation.Table;

/**
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public class UpdateDirective extends Directive {

	// ********************************************************************//
	// NESTED DECLARATIONS //
	// ********************************************************************//

	// ********************************************************************//
	// ABSTRACT DECLARATIONS //
	// ********************************************************************//

	// ********************************************************************//
	// STATIC DECLARATIONS //
	// ********************************************************************//

	/**
	 * @param mediator
	 * @param entityType
	 * @param fields
	 * 
	 * @return
	 */
	public static <E extends SnaEntity> UpdateDirective getUpdateDirective(E entity) {
		Class<? extends SnaEntity> entityType = entity.getClass();

		Table table = entityType.getAnnotation(Table.class);
		PrimaryKey primaryKey = entityType.getAnnotation(PrimaryKey.class);
		Map<Field, Column> fields = SnaEntity.getFields(entityType);

		KeyDirective keyDirective = KeyDirective.createKeyDirective(table, primaryKey, fields);
		keyDirective.assign(entity);

		UpdateDirective updateDirective = new UpdateDirective(table.value(), keyDirective);
		Iterator<Map.Entry<Field, Column>> iterator = fields.entrySet().iterator();

		String[] keys = primaryKey == null ? null : primaryKey.value();
		int index = 0;
		int length = keys == null ? 0 : keys.length;

		while (iterator.hasNext()) {
			Map.Entry<Field, Column> entry = iterator.next();

			index = 0;
			for (; index < length; index++) {
				if (keys[index].equals(entry.getValue().value())) {
					break;
				}
			}
			if (index < length) {
				continue;
			}
			updateDirective.update(entry.getValue().value(), entity.getFieldValue(entry.getKey()));
		}
		return updateDirective;
	}

	// ********************************************************************//
	// INSTANCE DECLARATIONS //
	// ********************************************************************//

	protected Map<String, Object> update;
	protected KeyDirective keyDirective;

	/**
	 * Constructor
	 */
	public UpdateDirective(String table, KeyDirective keyDirective) {
		super(table);
		this.update = new HashMap<String, Object>();
		this.keyDirective = keyDirective;
	}

	/**
	 * @param column
	 */
	public void update(String column, Object value) {
		if (column == null || column.length() == 0) {
			return;
		}
		this.update.put(column, value);
	}

	/**
	 * @inheritDoc
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (this.update.isEmpty()) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(SnaDAO.UPDATE_DIRECTIVE).append(SnaDAO.SPACE);
		builder.append(super.table).append(SnaDAO.SPACE);

		Iterator<Map.Entry<String, Object>> iterator = this.update.entrySet().iterator();

		Map.Entry<String, Object> entry = null;
		if (iterator.hasNext()) {
			entry = iterator.next();
			builder.append(SnaDAO.SET_DIRECTIVE).append(SnaDAO.SPACE);
			builder.append(entry.getKey());
			builder.append(SnaDAO.EQUALS_OPERATOR);
			builder.append(super.getStringValue(entry.getValue()));
		}
		while (iterator.hasNext()) {
			entry = iterator.next();
			builder.append(SnaDAO.COMMA);
			builder.append(entry.getKey());
			builder.append(SnaDAO.EQUALS_OPERATOR);
			builder.append(super.getStringValue(entry.getValue()));
		}
		builder.append(SnaDAO.SPACE);
		builder.append(SnaDAO.WHERE_DIRECTIVE);
		builder.append(SnaDAO.SPACE);
		builder.append(keyDirective.getValueDirective());
		String updateDirective = builder.toString();
		// System.out.println("**************************************");
		// System.out.println(updateDirective);
		// System.out.println("**************************************");
		return updateDirective;
	}

}
