/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.common.constraint;

import org.eclipse.sensinact.gateway.common.primitive.JSONable;

import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

/**
 * a Constraint which applies on an object
 *
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public interface Constraint extends JSONable {
    public static final String OPERATOR = "OPERATOR";

    public static final String OPERATOR_KEY = "operator";
    public static final String OPERAND_KEY = "operand";
    public static final String TYPE_KEY = "type";
    public static final String COMPLEMENT_KEY = "complement";

    /**
     * Defines whether the value argument complies to
     * this Constraint according to the value of its
     * predefined operand
     *
     * @param value the value to evaluate whether it complies
     *              to this Constraint
     * @return <ul>
     * <li>true if the value argument complies
     * to this Constraint</li>
     * <li>false otherwise</li>
     * </ul>
     */
    boolean complies(Object value);

    /**
     * Returns this Constraint's string operator
     *
     * @return the string operator of this
     * Constraint
     */
    String getOperator();

    /**
     * Returns true if this constraint is defined as a
     * logical complement ; returns false otherwise
     *
     * @return <ul>
     * <li>true if this constraint is defined
     * as a logical complement</li>
     * <li>false otherwise</li>
     * </ul>
     */
    boolean isComplement();

    /**
     * Returns this Constraint logical complement
     *
     * @return this Constraint logical complement
     */
    Constraint getComplement();
    
    static Object toConstantValue(JsonValue constant) {
    	if(constant == null) return null;
		switch(constant.getValueType()) {
	        case OBJECT:
			case ARRAY:
				return constant;
			case TRUE:
				return Boolean.TRUE;
			case FALSE:
				return Boolean.FALSE;
			case NULL:
				return null;
			case NUMBER:
				JsonNumber jn = (JsonNumber) constant;
				if(jn.isIntegral()) {
					long l = jn.longValueExact();
					if(l < Integer.MAX_VALUE && l > Integer.MIN_VALUE) {
						return jn.intValueExact();
					} else {
						return l;
					}
				} else {
					return jn.doubleValue();
				}
			case STRING:
				return ((JsonString)constant).getString();
			default:
				throw new IllegalArgumentException(constant.toString());
        }
	}

}
