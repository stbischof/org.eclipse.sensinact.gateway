/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.test;

import java.io.File;
import java.util.Enumeration;
import java.util.Stack;

public class FilesEnumerator implements Enumeration<File> {
    Stack<Enumeration<File>> stack = null;

    public FilesEnumerator(File sourceDirectory) {
        stack = new Stack<Enumeration<File>>();
        stack.push(newEnumeration(sourceDirectory));
    }

    @Override
    public boolean hasMoreElements() {
        if (stack.isEmpty()) {
            return false;
        }
        Enumeration<File> current = null;
        while (!stack.isEmpty() && !(current = stack.pop()).hasMoreElements()) {
            current = null;
        }
        if (current != null) {
            stack.push(current);
            return true;
        }
        return false;
    }

    @Override
    public File nextElement() {
        if (stack.isEmpty()) {
            return null;
        }
        Enumeration<File> current = stack.pop();
        File file = current.nextElement();
        stack.push(current);
        if (file.isDirectory()) {
            stack.push(newEnumeration(file));
        }
        return file;
    }

    private static Enumeration<File> newEnumeration(final File file) {
        return new Enumeration<File>() {
            int position = 0;
            File[] files = file.isDirectory() ? file.listFiles() : new File[]{file};

            @Override
            public boolean hasMoreElements() {
                return position < files.length;
            }

            @Override
            public File nextElement() {
                if (hasMoreElements()) {
                    return files[position++];
                }
                return null;
            }
        };
    }
}