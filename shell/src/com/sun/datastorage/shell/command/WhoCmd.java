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
import java.util.Map;
import java.util.Set;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;

class WhoCmd extends Command {

    WhoCmd(Commander cmdr) {
        super(cmdr, "who", "com.sun.datastorage.shell.command.who", "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/docs/shell-command-who.html");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: who");
    }

    @Override
    public void execute(String[] args) {
        String className;
        Object obj;
        String whoName;
        SimpleBindings bindings = (SimpleBindings) cmdr.getDoc().getScriptEngine().getContext().getBindings(ScriptContext.ENGINE_SCOPE);
        Set<Map.Entry<String, Object>> items = bindings.entrySet();
        //TODO: sort and clean up printed output
        for (Map.Entry<String, Object> item : items) {
            whoName = item.getKey();
            obj = item.getValue();
            if (obj == null) {
                className = "null";
            } else {
                className = obj.getClass().toString();
            }
            cmdr.getDoc().getOutWriter().printf("%20s : %-20s\n", whoName, className);
        }
    }
}
