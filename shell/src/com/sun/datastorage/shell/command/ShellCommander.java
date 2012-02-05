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

import com.sun.datastorage.shell.*;
import java.io.File;
import org.openide.filesystems.FileObject;

/**
 *
 * @author 
 */
public class ShellCommander extends Commander {

    EvalfCmd evalf;

    /**
     * Creates a new instance of ShellCommander
     */
    public ShellCommander() {
        super();
        name = "shell";

        // populate the command map.
        evalf = new EvalfCmd(this);


        cmdMap.put(new CdCmd(this));
        cmdMap.put(new EditCmd(this));
        cmdMap.put(evalf);
        ExitCmd exit = new ExitCmd(this);
        cmdMap.put(exit);
        cmdMap.put("quit", exit);
        cmdMap.put("bye", exit);
        cmdMap.put(new HelpCmd(this));
        cmdMap.put(new KillCmd(this));
        cmdMap.put(new LsCmd(this));
        cmdMap.put(new PathCmd(this));
        cmdMap.put(new PingCmd(this));
        cmdMap.put(new PwdCmd(this));
        cmdMap.put(new RmCmd(this));
        cmdMap.put(new WhichCmd(this));
        cmdMap.put(new WhoCmd(this));
        cmdMap.put(new JobsCmd(this));

        //cmdMap.put(new AudioCmd(this));
        //cmdMap.put(PrdCmd.NAME, prd);
        //cmdMap.put(new TestCmd(this));

    }

    @Override
    public void init(ShellDocument doc) {
        super.init(doc);
        doc.setDefaultPath(doc.getDefaultPath() + ";jar:com.sun.datastorage.shell.scripts.js");
        doc.getScriptEngine().put("shellCommander", this);
        doc.getScriptEngine().put("shellDoc", doc);
    }

    /**
     * Get current working directory.
     * (used by shell.js)
     */
    public File getCwd() {
        return doc.getPathManager().getCwd();
    }

    @Override
    public boolean parse(String cmdStr) throws Exception {

        boolean retVal = super.parse(cmdStr);

        if (retVal) {
            return true;
        } else {
            String cmdName = null;
            String[] cmdArgs = CommandTokenizer.ParseArgs(cmdStr);
            cmdName = cmdArgs[0];
            // Not a command, must be a script file to run.
            FileObject fo;
            String fileName = cmdName;
            String extension = (fileName.lastIndexOf(".") == -1) ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);

            // if the extension shows that it is a script file, then see if it
            // exists.
            if (extension.equals(doc.getScriptExtension())) {
                fo = doc.getPathManager().getFileObject(fileName);
            } else {
                // Try adding the default extension
                fo = doc.getPathManager().getFileObject(fileName + "." + doc.getScriptExtension());
            }


            if (fo != null && fo.isValid() && !fo.isFolder()) {
                evalf.evaluate(fo);
                return true;
            } else {
                return false;
            }

        }
    }
//    public static ShellCommander find(
//            Vector<Commander> list) {
//        if (list != null) {
//            for (Commander commander : list) {
//                if (commander instanceof ShellCommander) {
//                    return (ShellCommander) commander;
//                }
//
//            }
//        }
//        return null;
//    }
}
