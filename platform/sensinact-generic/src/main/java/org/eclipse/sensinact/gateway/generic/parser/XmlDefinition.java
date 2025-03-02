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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract XML node definition
 *
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public abstract class XmlDefinition {
	
	private static final Logger LOG = LoggerFactory.getLogger(XmlDefinition.class);

    protected Mediator mediator;
    protected Method tagged;

    /**
     * Constructor
     *
     * @param mediator the {@link Mediator} allowing the XmlDefinition to be
     * instantiated to interact with the OSGi host environment
     * @param atts     the {@link Attributes} data structure of the current 
     * XML node */
    public XmlDefinition(Mediator mediator, Attributes atts) {
        this.mediator = mediator;        
        XmlElement element = this.getClass().getAnnotation(XmlElement.class);

        if (element != null) {
            String name = element.tag();
            String field = element.field();
            if (!name.equals(XmlElement.DEFAULT_ELEMENT_VALUE) && !field.equals(XmlElement.DEFAULT_ELEMENT_VALUE)) {
                char[] fieldChars = field.toCharArray();
                fieldChars[0] = Character.toUpperCase(fieldChars[0]);

                String methodName = new StringBuilder("set").append(new String(fieldChars)).toString();

                this.tagged = this.getMethod(methodName);
            }
        }
        if (atts != null) {
            for (XmlAttribute annotation : this.buildAttributeAnnotationsSet()) {
                String name = annotation.attribute();
                String field = annotation.field();
                String value = null;

                if (name.equals(XmlAttribute.DEFAULT_ATTRIBUTE_VALUE) 
                		|| field.equals(XmlAttribute.DEFAULT_ATTRIBUTE_VALUE) 
                		|| (value = atts.getValue(name)) == null 
                		|| value.length() == 0) {
                    continue;
                }
                char[] fieldChars = field.toCharArray();
                fieldChars[0] = Character.toUpperCase(fieldChars[0]);

                String methodName = new StringBuilder("set").append(new String(fieldChars)).toString();
                Method method = this.getMethod(methodName);

                if (method != null) {
                    this.invoke(method, value);
                }
            }
        }
    }

    /**
     * Sets the value of the field mapped to the XML node whose 
     * name is specified by the {@link XmlElement} annotation annotating
     * this XmlDefinition if any
     *
     * @param value the string value to set
     */
    public void mapTag(String value) {
        this.invoke(tagged, value);
    }

    /**
     * Invoke the {@link Method} object passed as parameter
     * using the String value argument to parameterize
     * the call
     *
     * @param method the method to invoke
     * @param value  the string value used to parameterize
     *               the invocation
     */
    private void invoke(Method method, String value) {
        if (method != null) {
            method.setAccessible(true);
            try {
                method.invoke(this, value);

            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Builds and returns an array of all declared XmlAttribute
     * annotations held by an XmlAttributes annotating this
     * XmlDefinition and its super classes
     *
     * @return the array of {@link XmlAttribute} annotations
     * for this XmlDefinition
     */
    private XmlAttribute[] buildAttributeAnnotationsSet() {
        List<XmlAttribute> tags = new ArrayList<XmlAttribute>();
        Class<?> currentClass = this.getClass();

        while (currentClass != null) {
            XmlAttributes attributes = currentClass.getAnnotation(XmlAttributes.class);

            if (attributes != null) {
                for (XmlAttribute attribute : attributes.value()) {
                    tags.add(attribute);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return tags.toArray(new XmlAttribute[tags.size()]);
    }

    /**
     * Get the method whose name is passed as parameter and whose
     * signature contains only one single String argument for this 
     * XmlDefinition
     * 
     * @param methodName the name of the method
     * 
     * @return the {@link Method} with the specified name if found
     */
    private Method getMethod(String methodName) {
        Class<?> currentClass = this.getClass();
        Method method = null;

        while (XmlDefinition.class.isAssignableFrom(currentClass)) {
            try {
                method = currentClass.getDeclaredMethod(methodName, String.class);
                break;

            } catch (Exception e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return method;
    }
}
