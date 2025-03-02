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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.sensinact.gateway.common.bundle.Mediator;
import org.eclipse.sensinact.gateway.common.primitive.InvalidValueException;
import org.eclipse.sensinact.gateway.common.primitive.Modifiable;
import org.eclipse.sensinact.gateway.core.ResourceConfig;
import org.eclipse.sensinact.gateway.core.ResourceImpl;
import org.eclipse.sensinact.gateway.core.ServiceImpl;
import org.eclipse.sensinact.gateway.core.method.AccessMethod;
import org.eclipse.sensinact.gateway.core.method.Parameter;
import org.eclipse.sensinact.gateway.core.method.Shortcut;
import org.eclipse.sensinact.gateway.core.method.Signature;

/**
 * Act method
 *
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public class SignatureDefinition implements Comparable<SignatureDefinition> {
    
	protected List<ReferenceDefinition> references;
    private List<ParameterDefinition> parameters;
    private final AccessMethod.Type type;

    /**
     * Constructor
     *
     * @param type {@link AccessMethod.Type} of the {@link Signature}
     *             described by the SignatureDefinition to instantiate
     */
    public SignatureDefinition(AccessMethod.Type type) {
        this.type = type;
        this.parameters = new ArrayList<ParameterDefinition>();
        this.references = new ArrayList<ReferenceDefinition>();
    }

    /**
     * @param referenceDefinition
     */
    public void addReferenceDefinition(ReferenceDefinition referenceDefinition) {
       this.references.add(referenceDefinition);
    }

    /**
     * Returns the list of {@link ReferenceDefinition} defined for this
     * ACTION typed {@link ResourceConfig}
     *
     * @return the list of {@link ReferenceDefinition} defined for this
     * ACTION typed {@link ResourceConfig}
     */
    public List<ReferenceDefinition> getReferenceDefinitions() {
        return Collections.unmodifiableList(this.references);
    }


    /**
     * @param parameter
     */
    public void addParameter(ParameterDefinition parameter) {
        if (parameter != null) {
            this.parameters.add(parameter);
        }
    }

    /**
     * @param resourceImpl 
     * @return
     * @throws InvalidValueException
     */
    public Signature getSignature(Mediator mediator, ResourceImpl resource, ServiceImpl service) throws InvalidValueException {

        Map<Integer, Parameter> fixedParameters = new HashMap<Integer, Parameter>();
        List<Parameter> parameters = new ArrayList<Parameter>();

        Signature signature = null;

        Iterator<ParameterDefinition> parameterIterator = this.parameters.iterator();

        int position = 0;

        while (parameterIterator.hasNext()) {
            ParameterDefinition parameterDefinition = parameterIterator.next();
            Parameter parameter = parameterDefinition.getParameter(resource, service);

            if (Modifiable.FIXED.equals(parameter.getModifiable()))
                fixedParameters.put(position, parameter);
            else
                parameters.add(parameter);
            position++;
        }
        Parameter[] parametersArray = parameters.toArray(new Parameter[parameters.size()]);
        if (fixedParameters.isEmpty()) {
            signature = new Signature(mediator, type.name(), parametersArray);
        } else {
            signature = new Shortcut(mediator, type.name(), parametersArray, fixedParameters);
        }
        return signature;
    }

    /**
     * @return
     */
    public int size() {
        return this.parameters.size();
    }

    /**
     * @return
     */
    public int fixedSize() {
        Iterator<ParameterDefinition> parameterIterator = this.parameters.iterator();
        int count = 0;
        while (parameterIterator.hasNext()) {
            count += parameterIterator.next().isModifiable() ? 0 : 1;
        }
        return count;
    }
    
    @Override
    public int compareTo(SignatureDefinition definition) {
        int compare = new Integer(this.size()).compareTo(definition.size());

        if (compare != 0) {
            return compare;
        }
        return new Integer(this.fixedSize()).compareTo(definition.fixedSize());
    }

    /**
     * Returns the list of registered {@link ParameterDefinition}s
     * of this SignatureDefinition
     *
     * @return this SignatureDefinition's list of registered {@link
     * ParameterDefinition}s
     */
    public List<ParameterDefinition> getParameters() {
        return Collections.unmodifiableList(this.parameters);
    }
}
