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

import com.sun.datastorage.shell.CmdLineParser;
import com.sun.datastorage.shell.Command;
import com.sun.datastorage.shell.Commander;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

class WhichCmd extends Command {

    CmdLineParser.Option verbose;

    public WhichCmd(Commander cmdr) {
        super(cmdr, "which", "com.sun.datastorage.shell.command.which", "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/docs/shell-command-which.html");
        verbose = parser.addBooleanOption('v', "verbose");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: which  <script file>");
    }

    @Override
    public void execute(String[] args) throws Exception {
       super.execute(args);
        if (otherArgs.length == 1) {
            String fileName = otherArgs[0];
            FileObject fo = cmdr.getDoc().getPathManager().getFileObject(fileName);
            if (fo != null) {
                cmdr.getDoc().getOutWriter().println(FileUtil.getFileDisplayName(fo));
            } else {
                cmdr.getDoc().getOutWriter().printf("\"%s\" not found.\n", fileName);
            }
        }
    }
}
