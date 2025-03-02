/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.common.execution;

/**
 * Execution Context encapsulates an {@link Executable}, provides
 * necessary data allowing its execution, handles potentially hired
 * exception and collects its execution's result
 *
 * @param <P> the wrapped executor parameter type
 * @param <V> the wrapped executor returned type
 * @author <a href="mailto:christophe.munilla@cea.fr">Christophe Munilla</a>
 */
public interface ExecutionContext<P, V> {
    /**
     * Returns the {@link ExecutionErrorHandler} handling
     * the exception(s) potentially thrown by this
     * ExecutionContext's {@link Executable}
     *
     * @return this ExecutionContext's  {@link ExecutionErrorHandler}
     */
    ErrorHandler getErrorHandler();

    /**
     * Returns the {@link Executable} wrapped by this
     * ExecutionContext
     *
     * @return this ExecutionContext's {@link Executable}
     */
    Executable<P, V> getExecutor();

    /**
     * Returns the appropriate <code>&lt;P&gt;</code> typed
     * parameter parameterizing this ExecutionContext's
     * {@link Executable}
     *
     * @return the <code>&lt;P&gt;</code> typed parameter to
     * pass to this ExecutionContext's {@link Executable}
     */
    P getParameter();

    /**
     * Returns the <code>&lt;V&gt;</code> typed object result
     * of this ExecutionContext's {@link Executable} execution
     *
     * @return this ExecutionContext's {@link Executable}
     * execution result
     */
    V getResult();
}
