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
import org.netbeans.api.javahelp.Help;
import org.openide.util.Lookup;

/**
 *
 * @author 
 */
class HelpCmd extends Command {

    public HelpCmd(Commander cmdr) {
        super(cmdr, "help",
                "com.sun.datastorage.shell.command.help",
                "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/docs/shell-command-help.html");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: help <command>");
    }

    @Override
    public void execute(String[] args) throws Exception {

        super.execute(args);
        
        if (otherArgs.length == 0) {
            Help help = Lookup.getDefault().lookup(Help.class);
            help.showHelp(null);
        } else if (otherArgs.length == 1) {
            String target = otherArgs[0];

            // find the command and show its help page
            for (Commander commander : cmdr.getDoc().getCommanderList()) {
                Command cmd = commander.findCmd(target);
                if (cmd != null) {
                    Help help = Lookup.getDefault().lookup(Help.class);
                    if (cmd.getHelpCtx() != null) {
                        help.showHelp(cmd.getHelpCtx(), true);
                    }
                    break;
                }
            }
        } else {
            printUsage();
        }
    }
}
