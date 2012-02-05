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
import java.io.File;
import java.io.FileNotFoundException;

class PathCmd extends Command {

    CmdLineParser.Option setPathOpt;
    CmdLineParser.Option addPathOpt;
    CmdLineParser.Option clearPathOpt;
    CmdLineParser.Option verboseOpt;

    public PathCmd(Commander cmdr) {
        super(cmdr, "path", "com.sun.datastorage.shell.command.path", "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/docs/shell-command-path.html");
        setPathOpt = parser.addStringOption('s', "setPath");
        addPathOpt = parser.addStringOption('a', "addPath");
        clearPathOpt = parser.addBooleanOption('c', "clearPath");
        verboseOpt = parser.addBooleanOption('v', "verbose");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: path");
    }

    @Override
    public void execute(String[] args) throws Exception {
        super.execute(args);
      
        String setPath = (String) parser.getOptionValue(setPathOpt, "");
        String addPath = (String) parser.getOptionValue(addPathOpt, "");
        Boolean clearPath = (Boolean) parser.getOptionValue(clearPathOpt, Boolean.FALSE);
        Boolean verbose = (Boolean) parser.getOptionValue(verboseOpt, Boolean.FALSE);
        if (verbose) {
            cmdr.getDoc().getOutWriter().println(name + argsToString(args));
        }

        if (otherArgs.length != 0) {
            if (clearPath) {
                cmdr.getDoc().getPathManager().setPath("");
            } else if (!setPath.isEmpty()) {
                File fil = new File(setPath);
                if (fil.exists()) {
                    cmdr.getDoc().getPathManager().setPath(fil.getAbsolutePath());
                }
            } else if (!addPath.isEmpty()) {
                File fil = new File(addPath);
                if (fil.exists()) {
                    try {
                        cmdr.getDoc().getPathManager().addPath(fil.getAbsolutePath());
                    } catch (FileNotFoundException ex) {
                        cmdr.getDoc().getErrorWriter().println("Directory does not exist.");
                    }
                }
            }
        }
        cmdr.getDoc().getPathManager().print(cmdr.getDoc().getOutWriter(), cmdr.getDoc().getErrorWriter());
    }
}
