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
package com.sun.datastorage.shell.example;

import com.sun.datastorage.shell.CmdLineParser;
import com.sun.datastorage.shell.CmdLineParser.IllegalOptionValueException;
import com.sun.datastorage.shell.CmdLineParser.UnknownOptionException;
import com.sun.datastorage.shell.Command;
import com.sun.datastorage.shell.Commander;
import com.sun.datastorage.shell.ShellEditorDocument;
import com.sun.datastorage.shell.completion.CommandCompletionItem;
import com.sun.datastorage.shell.completion.FileCompletionFilter;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.util.Exceptions;

/**
 *
 * @author Matt Tucker
 */
class AdbCmd extends Command {

    CmdLineParser.Option deviceOpt;
    final static String[] SUB_CMDS = new String[]{"push", "pull"};

    public AdbCmd(Commander commander) {
        super(commander, "adb", "com.sun.datastorage.shell.example.adb",
                "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/example/docs/android-command-adb.html");
        deviceOpt = parser.addBooleanOption('d', "");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: adb");
    }

    @Override
    public void execute(String[] args) throws Exception {
        super.execute(args);

        Boolean device = (Boolean) parser.getOptionValue(deviceOpt, Boolean.FALSE);

        cmdr.getDoc().getOutWriter().println("adb:");


        if (device) {
            cmdr.getDoc().getOutWriter().println("---Directing command to the only attached USB device.");
        }
        if (otherArgs.length > 0) {
            String subCmd = otherArgs[0];
            if (subCmd.equals("push")) {
                if (otherArgs.length != 3) {
                    cmdr.getDoc().getOutWriter().println("Usage: adb push <local> <remote>");
                }
                String local = otherArgs[1];
                String remote = otherArgs[2];
                cmdr.getDoc().getOutWriter().println("---pushing from " + local + " to " + remote);
            } else if (subCmd.equals("pull")) {
                if (otherArgs.length != 3) {
                    cmdr.getDoc().getOutWriter().println("Usage: adb pull <remote> <local>");
                }
                String remote = otherArgs[1];
                String local = otherArgs[2];
                cmdr.getDoc().getOutWriter().println("---pushing from " + remote + " to " + local);
            }
        }
    }

    @Override
    public void addArgCompletionItems(final CompletionResultSet completionResultSet,
            ShellEditorDocument doc, String filter, int startOffset, int caretOffset, String[] cmdArgs, int argNum) {

        super.addArgCompletionItems(completionResultSet, doc, filter, startOffset, caretOffset, cmdArgs, argNum);
        // 0 = command
        // 1,2,3 = 1st, 2nd, 3rd args respectively
        // -1, -2, -3 = 1st, 2nd, 3rd options respectively
        if (argNum > 0) {
            if (argNum == 1) {
                for (String subcmd : SUB_CMDS) {
                    if (subcmd.startsWith(filter)) {
                        completionResultSet.addItem(new CommandCompletionItem(subcmd, this, startOffset, caretOffset));
                    }
                }
            } else {
                try {
                    parser.parse(cmdArgs);
                } catch (IllegalOptionValueException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (UnknownOptionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                String[] args = parser.getRemainingArgs();
                //otherArgs[0] = adb
                if (args[1].equals(SUB_CMDS[0]) || args[1].equals(SUB_CMDS[1])) {
                    addFileCompletionItems(completionResultSet, doc, filter, startOffset, caretOffset, FileCompletionFilter.FILES_ONLY);
                }
            }
        }
    }
}


