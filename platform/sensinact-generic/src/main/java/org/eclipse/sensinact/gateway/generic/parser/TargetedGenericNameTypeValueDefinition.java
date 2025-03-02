/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.generic.parser;

import org.eclipse.sensinact.gateway.common.bundle.Mediator;
import org.eclipse.sensinact.gateway.core.ResourceConfig;
import org.xml.sax.Attributes;

/**
 * 
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
@XmlAttributes({@XmlAttribute(attribute = "target", field = "target")})
public abstract class TargetedGenericNameTypeValueDefinition extends GenericNameTypeValueDefinition {

    /**
     * the target attribute value of the associated XML node */
    protected String[] target;

    /**
     * Constructor
     *
     * @param mediator the associated Mediator
     * @param atts     the set of attributes data structure for the
     *                 XML node */
    TargetedGenericNameTypeValueDefinition(Mediator mediator, Attributes atts) {
        super(mediator, atts);
    }
    
    /**
     * Sets the name of the target
     *
     * @param target the target name to set
     */
    public void setTarget(String target) {
        String[] targets = target == null ? new String[0] : target.split(",");
        this.target = new String[targets.length];
        if (this.target.length > 0) {
            for (int index = 0; index < this.target.length; index++) {
                if ("ANY_TARGET".equals(targets[index])) {
                    this.target[index] = ResourceConfig.ALL_TARGETS;
                } else {
                    this.target[index] = targets[index];
                }
            }
        }
    }

    /**
     * Returns the name of the target
     *
     * @return the target name
     */
    public String getTarget() {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        int length = this.target == null ? 0 : this.target.length;
        for (; index < length; index++) {
            if (index > 0) {
                builder.append(",");
            }
            builder.append(this.target[0].trim());
        }
        return builder.toString();
    }

    public String[] getTargets() {
        return this.target;
    }

    /**
     * Returns the name of the target
     *
     * @return the target name
     */
    public boolean isTargeted(String serviceId) {
        for (int index = 0; index < this.target.length; index++) {
            if (target[index].equals(ResourceConfig.ALL_TARGETS) || target[index].equals(serviceId)) {
                return true;
            }
        }
        return false;
    }
}
