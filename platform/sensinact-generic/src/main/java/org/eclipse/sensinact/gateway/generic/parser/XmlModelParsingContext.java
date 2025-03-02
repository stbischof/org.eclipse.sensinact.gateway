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
import org.xml.sax.SAXException;

/**
 * XML parsing context related to an XML node , and  providing the methods
 * allowing to treat embedded XML sub element (which can itself also be 
 * an XmlModelParsingContext) 
 * 
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public class XmlModelParsingContext extends XmlDefinition {
	
	private static final Logger LOG = LoggerFactory.getLogger(XmlModelParsingContext.class);
	
	private XmlModelParsingContext next;
	protected StringBuilder textContent;

	/**
     * @param mediator the {@link Mediator} allowing the XmlModelParsingContext
     * to be instantiated to interact with the OSGi host environment
     * @param atts the {@link Attributes} data structure of the current xml 
     * element
	 */
	public XmlModelParsingContext(Mediator mediator, Attributes atts) {
		super(mediator,atts);
		this.textContent = new StringBuilder();
    }
	
	/**
	 * Returns the array  of inner XML sub element's name to not be treated
	 * 
	 * @return the untreated XML sub elements' name array
	 */
	public String[] escaped() {
		XmlEscaped escaped= getClass().getAnnotation(XmlEscaped.class);
		if(escaped != null) {
			return escaped.value();
		}
		return new String[0];
	}

    /**
     * Receive notification of character data. The root {@link XmlResourceConfigHandler} 
     * will call this method to report each chunk of character data.
     * 
     * @param chrs the characters from the XML document
     * @param start the start position in the array
     * @param length the number of characters to read from the array
     * @throws SAXException
     */
    public void characters(char[] chrs, int start, int length) throws SAXException {
        this.textContent.append(chrs, start, length);
    }

    /**
     * Receive notification of ignorable whitespace in element content from the root
     *  {@link XmlResourceConfigHandler}.
     * 
     * @param chrs the characters from the XML document
     * @param start the start position in the array
     * @param length the number of characters to read from the array
     * @throws SAXException
     */
    public void ignorableWhitespace(char[] chrs, int start, int length) throws SAXException {
        textContent.append(chrs, start, length);
    }

    /**
     * Receive notification of a processing instruction from the root {@link 
     * XmlResourceConfigHandler}
     * 
     * @param target the processing instruction target
     * @param data the processing instruction data, or null if none was supplied. The 
     * data does not include any whitespace separating it from the target
     * 
     * @throws SAXException
     */
    public void processingInstruction(String target, String data) throws SAXException {
    	 LOG.debug("processing instruction : \n\t" + target + " \n\t " + data);
    }

    /**
     * Receive notification of a skipped entity from the root {@link 
     * XmlResourceConfigHandler}. This is not called for entity references within 
     * markup constructs such as element start tags or markup declarations.
     * 
     * @param name the skipped entity name
     */
    public void skippedEntity(String name) throws SAXException {
    	 LOG.debug("skipped entity : " + name);
    }
    
	/**
	 * Returns the inner {@link XmlModelParsingContext} that is 
	 * currently parsed
	 * 
	 * @return the currently parsed inner {@link XmlModelParsingContext}
	 */
	public XmlModelParsingContext next() {
		return next;
	}
	
	/**
	 * Defines the inner {@link XmlModelParsingContext} that is 
	 * currently parsed
	 * 
	 * @param next the currently parsed inner {@link XmlModelParsingContext}
	 */
	protected void setNext(XmlModelParsingContext next) {
		this.next = next;
	}
	
	/**
	 * the XML node represented by this {@link XmlModelParsingContext}
	 * is ended
	 */
	public void end() {
		super.mapTag(this.textContent.toString());
	}
}
