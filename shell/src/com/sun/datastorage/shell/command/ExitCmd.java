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
package com.sun.datastorage.shell.command;

import com.sun.datastorage.shell.Command;
import com.sun.datastorage.shell.Commander;
import java.awt.EventQueue;
import javax.swing.SwingUtilities;
import org.openide.windows.TopComponent;

class ExitCmd extends Command {

    public ExitCmd(Commander cmdr) {
        super(cmdr, "exit", "com.sun.datastorage.shell.command.exit", "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/docs/shell-command-exit.html");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: exit");
    }

    @Override
    public void execute(String[] args) {
        // doc.close();
        final TopComponent tc = (TopComponent) SwingUtilities.getAncestorOfClass(TopComponent.class, cmdr.getDoc().getEditorPane());
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                tc.close();
            }
        });
    }
}
