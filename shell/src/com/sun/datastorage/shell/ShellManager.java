/**
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original  Software is Simple Shell. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc.
 *
 * Matt Tucker, Sun Microsystems
 *
 */
package com.sun.datastorage.shell;

import java.util.Vector;

public class ShellManager {

    Vector<ShellDocument> shellList = new Vector<ShellDocument>(5);
    int lastShell = -1;

    public ShellManager() {
    }

    public boolean add(ShellDocument doc) {
        lastShell++;
        doc.setShellId(lastShell);
        return shellList.add(doc);
    }

    public boolean remove(ShellDocument doc) {

        if (doc.getShellId() == lastShell) {
            lastShell--;
        }
        return shellList.remove(doc);
    }

    public ShellDocument get(int i) {
        return shellList.get(i);
    }

    public int size() {
        return shellList.size();
    }
}
