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
package com.sun.datastorage.shell.server;

import com.sun.datastorage.shell.ShellDocument;

/**
 *
 * @author Matt Tucker
 */
public class Connection {

    String clientId;
    int shellId;
    ShellDocument doc;

    Connection(String clientId) {
        this.clientId = clientId;
        if (clientId.contains(":")) {
            shellId = Integer.parseInt(clientId.substring(clientId.indexOf(":") + 1));
        } else {
            shellId = 0;
        }
    }

    @Override
    public boolean equals(Object ob) {
        Connection c = (Connection) ob;

        return clientId.equals(c.clientId);

    }

    void setDoc(ShellDocument doc) {
        this.doc = doc;

    }
}
