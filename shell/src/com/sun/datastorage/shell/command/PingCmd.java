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
import java.net.InetAddress;
import java.net.UnknownHostException;

class PingCmd extends Command {

    public PingCmd(Commander cmdr) {
        super(cmdr, "ping", "com.sun.datastorage.shell.command.ping", "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/docs/shell-command-ping.html");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: ping <hostname>");
    }

    @Override
    public void execute(String[] args) throws Exception {
        super.execute(args);
        if (otherArgs.length == 1) {
            String host = otherArgs[0];
            InetAddress address = null;
            try {
                address = InetAddress.getByName(host);
            } catch (UnknownHostException ex) {
                ex.printStackTrace(cmdr.getDoc().getErrorWriter());
            }
            String ip = address.getHostAddress();
            String hostName = address.getHostName();
            try {
                if (address.isReachable(5000)) {
                    cmdr.getDoc().getOutWriter().println(host + " is ALIVE.");
                } else {
                    cmdr.getDoc().getOutWriter().println(host + " is not reachable.");
                }
            } catch (IOException ex) {
                ex.printStackTrace(cmdr.getDoc().getErrorWriter());
            }
        }
    }
}
