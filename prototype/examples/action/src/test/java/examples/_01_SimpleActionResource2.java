/*********************************************************************
* Copyright (c) 2022 Contributors to the Eclipse Foundation.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*   Kentyou - initial implementation 
**********************************************************************/
package examples;

import java.util.List;

import org.eclipse.sensinact.prototype.annotation.propertytype.ProviderName;
import org.eclipse.sensinact.prototype.annotation.propertytype.WhiteboardResource;
import org.eclipse.sensinact.prototype.annotation.verb.ACT;
import org.osgi.service.component.annotations.Component;


/**
 * ${snippet title=_01_SimpleActionResource2, description="Service properties define the provider that this resource is for.", groupName=group2} 
 */
@WhiteboardResource
@ProviderName("pull_based")
@Component(service = _01_SimpleActionResource2.class)
public class _01_SimpleActionResource2 {

    

    /**
     * ${snippet title=myTitle2, description="the description here",
     * parentGroup=group2, includeDeclaration=false} 
     *
     * A GET method for a service and resource
     * 
     * @return
     */
    @ACT(service = "example", resource = "default")
    public List<Long> doAction() {
        // Run the action and return the result
        return null;
    }
}
