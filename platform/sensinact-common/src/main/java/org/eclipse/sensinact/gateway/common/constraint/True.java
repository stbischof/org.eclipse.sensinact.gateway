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

import org.eclipse.sensinact.gateway.util.JSONUtils;

/**
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public class True implements Constraint {
    public static final String TRUE = "true";

    private final boolean complement;

    /**
     * @param complement
     */
    public True() {
        this.complement = false;
    }

    /**
     * @param complement
     */
    public True(boolean complement) {
        this.complement = complement;
    }

    /**
     * @inheritDoc
     * @see JSONable#getJSON()
     */
    @Override
    public String getJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append(JSONUtils.OPEN_BRACE);
        builder.append(JSONUtils.QUOTE);
        builder.append(OPERATOR_KEY);
        builder.append(JSONUtils.QUOTE);
        builder.append(JSONUtils.COLON);
        builder.append(JSONUtils.QUOTE);
        builder.append(this.getOperator());
        builder.append(JSONUtils.QUOTE);
        builder.append(JSONUtils.COMMA);
        builder.append(JSONUtils.QUOTE);
        builder.append(COMPLEMENT_KEY);
        builder.append(JSONUtils.QUOTE);
        builder.append(JSONUtils.COLON);
        builder.append(this.isComplement());
        builder.append(JSONUtils.CLOSE_BRACE);
        return builder.toString();
    }

    /**
     * @inheritDoc
     * @see Constraint#complies(java.lang.Object)
     */
    @Override
    public boolean complies(Object value) {
        return true ^ isComplement();
    }

    /**
     * @inheritDoc
     * @see Constraint#getOperator()
     */
    @Override
    public String getOperator() {
        return True.TRUE;
    }

    /**
     * @inheritDoc
     * @see Constraint#isComplement()
     */
    @Override
    public boolean isComplement() {
        return this.complement;
    }

    /**
     * @inheritDoc
     * @see Constraint#getComplement()
     */
    @Override
    public Constraint getComplement() {
        True complement = null;
        complement = new True(!this.complement);
        return complement;
    }
}
