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
import com.sun.datastorage.shell.VersionManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.openide.util.Exceptions;

class TestCmd extends Command {

    public TestCmd(Commander cmdr) {
        super(cmdr, "testit", "com.sun.datastorage.shell.command.audio", "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/docs/shell-command-audio.html");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: testit");
    }

    @Override
    public void execute(String[] args) {
        CmdLineParser.Option verboseOpt = parser.addBooleanOption('v', "verbose");
        if (args != null) {
            try {
                parser.parse(args);
            } catch (CmdLineParser.OptionException e) {
                cmdr.getDoc().getErrorWriter().println(e.getMessage());
                printUsage();
            }
        }
        Boolean verbose = (Boolean) parser.getOptionValue(verboseOpt, Boolean.FALSE);
//            String[] otherArgs = parser.getRemainingArgs();
//            for (int i = 0; i < otherArgs.length; i++) {
//                doc.getOutWriter().println(otherArgs[i]);
//            }
        StringBuilder str = null;
        try {
            str = new StringBuilder(VersionManager.getVersionProperty("app_version"));
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        cmdr.getDoc().getOutWriter().println("Version: " + str);
    }
}
