/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.app.manager.json;

import org.eclipse.sensinact.gateway.common.primitive.JSONable;
import org.eclipse.sensinact.gateway.util.json.JsonProviderFactory;

import jakarta.json.JsonObject;

/**
 * This class wraps the information for the initialization of the application
 *
 * @author Rémi Druilhe
 */
public class AppInitialize implements JSONable {
    private final AppOptions options;

    /**
     * Java constructor without options
     */
    public AppInitialize() {
        this(new AppOptions.Builder().build());
    }

    /**
     * Java constructor
     *
     * @param options the options of the application
     */
    public AppInitialize(AppOptions options) {
        this.options = options;
    }

    /**
     * JSON constructor
     *
     * @param initialize the initialize as JSON
     */
    public AppInitialize(JsonObject initialize) {
        AppOptions.Builder appOptionBuilder = new AppOptions.Builder();
        if (initialize.containsKey(AppJsonConstant.INIT_OPTIONS)) {
            JsonObject optionsJson = initialize.getJsonObject(AppJsonConstant.INIT_OPTIONS);
            if (optionsJson.containsKey(AppJsonConstant.INIT_OPTIONS_AUTORESTART)) {
                appOptionBuilder.autorestart(optionsJson.getBoolean(AppJsonConstant.INIT_OPTIONS_AUTORESTART));
            }
            if (optionsJson.containsKey(AppJsonConstant.INIT_OPTIONS_RESETONSTOP)) {
                appOptionBuilder.resetOnStop(optionsJson.getBoolean(AppJsonConstant.INIT_OPTIONS_RESETONSTOP));
            }
        }
        this.options = appOptionBuilder.build();
    }

    /**
     * Gets the options of the application
     *
     * @return the options of the application
     */
    public AppOptions getOptions() {
        return options;
    }

    /**
     * @see JSONable#getJSON()
     */
    public String getJSON() {
        return JsonProviderFactory.getProvider().createObjectBuilder()
        	.add(AppJsonConstant.INIT_OPTIONS, options.getJSON())
        	.build().toString();
    }
}
