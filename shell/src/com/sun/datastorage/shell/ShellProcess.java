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
package com.sun.datastorage.shell;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.SwingWorker;

/**
 * A swingworker to parse commands in their own thread.
 */
public class ShellProcess extends SwingWorker<Void, Void> {

    static HashMap<Integer, ShellProcess> pidMap = new HashMap<Integer, ShellProcess>(10);
    public static Vector<ShellProcess> processList = new Vector<ShellProcess>(5);
    static final String CMD_SEPARATOR = ";";
    public String cmdStr;
    ShellDocument doc;
    boolean insertCommand;
    private boolean promptOnComplete;
    private boolean processComplete = false;
    private int pid;
    public int job = 0;
    Date time = new Date();
    boolean processFailed;
    boolean addToHistory;
    String errMsg = null;
    private boolean killed = false;

    public static ShellProcess getProcess(int pid) {
        for (ShellProcess proc : processList) {
            if (proc.pid == pid) {
                return proc;
            }
        }
        //return pidMap.get(pid);
        return null;
    }

    public static boolean isDone(int pid) {
        ShellProcess process = getProcess(pid);
        if (process == null) {
            return true;
        } else {
            return process.isProcessDone();
        }
    }

    public static boolean waitTillDone(int pid) {
        ShellProcess process = getProcess(pid);
        if (process == null) {
            return false;
        }

        //don't wait in the EDT, or you will deadlock!
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {

            while (process.isProcessDone() == false) {
                Thread.yield();
            }
        } else {
            System.out.println("WARNING: No waiting in EDT allowed! Ignoring ShellProcess.waitTillDone.");
            return false;
        }

        return true;
    }

    ShellProcess(ShellDocument doc, String cmdStr, boolean insertCommand, boolean promptOnComplete, boolean addToHistory) {
        this.doc = doc;
        this.cmdStr = cmdStr;
        this.insertCommand = insertCommand;
        this.promptOnComplete = promptOnComplete;
        this.addToHistory = addToHistory;
        this.processComplete = false;

        pid = (int) System.currentTimeMillis();
        pidMap.put(pid, this);
        processList.add(this);
        processFailed = true;
    }

//    /**
//     * Compares the input process pid with this pid. If the pids
//     * are equal then true is returned, otherwise false is returned.
//     *
//     * @param obj process object to compare this process to
//     * @return true if process has the same pid as this process. False otherwise.
//     */
//    @Override
//    public boolean equals(Object obj) {
//        ShellProcess proc = (ShellProcess) obj;
//
//       return pid == proc.pid;
//
//    }
    public int getPid() {
        return pid;
    }

    @Override
    public Void doInBackground() {
        boolean cmdProcessed = false;
        String[] cmdLines = cmdStr.split("\\n");
        boolean lastCmdLineIsPartial = !cmdStr.endsWith("\n");

        processComplete = false;
        if (cmdLines != null) {

            for (int i = 0; i < cmdLines.length; i++) {

                if (i == cmdLines.length - 1 && lastCmdLineIsPartial && insertCommand) {
                    doc.insertTextLater(cmdLines[cmdLines.length - 1]);
                    break;
                }

                if (cmdLines[i] != null) {

                    if (addToHistory) {
                        doc.getCommandHistory().add(cmdLines[i]);
                    }

                    if (insertCommand) {

                        doc.insertTextLater(cmdLines[i] + "\n");
                    }

                    String[] subCmds = cmdLines[i].split(CMD_SEPARATOR);
                    int subCmdIdx;
                    String cmd;
                    for (subCmdIdx = 0; subCmdIdx < subCmds.length; subCmdIdx++) {
                        cmdProcessed = false;
                        cmd = subCmds[subCmdIdx];


                        try {
                            cmdProcessed = doc.processCmd(cmd);
                        } catch (NumberFormatException ex) {
                            cmdProcessed = true;
                            doc.getErrorWriter().printf("NumberFormatException: %s\n", ex.getMessage());
                        } catch (Exception ex) {
                            cmdProcessed = true;
                            if (ex.getMessage() != null) {
                                doc.getErrorWriter().println(ex.getClass().toString().replace("class ", "") + ":  " + ex.getMessage());
                            } else {
                                ex.printStackTrace(doc.getErrorWriter());
                            }
                        }

                        if (cmdProcessed == false) {
                            //command could not be processed, so if script evaluation is allowed we
                            //evaluate the remainder of the commands in the script engine. Otherwise, we
                            //print out an error message and ignore the rest of the commands.
                            if (doc.getScriptMode() == false) {
                                doc.getErrorWriter().printf("Command '%s' not found.\n", cmdLines[i]);
                            } else {
                                String remainingCmds = subCmds[subCmdIdx];
                                for (int j = subCmdIdx + 1; j < subCmds.length; j++) {
                                    remainingCmds += CMD_SEPARATOR + subCmds[j];
                                }
                                //Note: in script mode, commander commands after script commands
                                //on the same cmd line are not allowed. They will be processed by
                                //the scriptengine and most likely cause an error.
                                try {
                                    // Add one more binding using a special reserved variable name
                                    // to tell the script engine the name of the file it will be executing.
                                    // This allows it to provide better error messages.
                                    doc.getScriptEngine().put(ScriptEngine.FILENAME, "Shell Evaluation");

                                    doc.getScriptEngine().eval(remainingCmds);

                                } catch (ScriptException ex) {

                                    doc.getErrorWriter().println(ex.getMessage());

                                    break;
                                }
                            }

                            break;
                        }
                    }
                }

                doc.getOutWriter().flush();
                doc.getErrorWriter().flush();

                //always insert a prompt on complete
                if (promptOnComplete && (i < cmdLines.length || !lastCmdLineIsPartial)) {
                    doc.insertPromptLater();
                }
            } // end of for (int i = 0; i < cmdLines.length; i++) {


        } // end of if (cmdLines != null)

        processFailed = false;

        return null;
    }

    /*
     * Executed in event dispatching thread
     *
     * Note: if the process gets cancelled done() will be called before
     * doInBackground() throws an interruptedexception.
     */
    @Override
    public void done() {

        if (processFailed && !isCancelled()) {
            doc.getErrorWriter().println("Process failed unexpectedly!!!");
            doc.insertPromptLater();
        }

        //the process is done so remove it from the jobs list and the process
        //list
        doc.getJobManager().remove(this);
        processList.remove(this);

        //it is important that we only set processComplete in the EDT as document
        //insertions only get evaluted in the EDT and we need to wait till they are
        // completed before stating that the command has finished.
        //TODO: can this be removed, and just use isDone()?
        processComplete = true;

    }

    public boolean isProcessDone() {
        if (false) {
            isCancelled();
            return processComplete;
        } else {
            return isDone();
        }
    }

    /**
     * Cancel the command being processed.
     */
    public void cancel() {
        //if cancelling with true, this method is bad as it closes all
        //I/0 Channels, which the user probably wants to keep open. Use kill()
        //if you want to do this. We don't use cancel(false) as then the process
        // will be considered as cancelled, and then any kill() requests will be
        // ignored.
        //boolean value = cancel(false);

        for (Commander commander : doc.getCommanderList()) {
            commander.cancel();
        }

    }

    @Override
    protected void finalize() throws Throwable {
        pidMap.remove(pid);
        super.finalize(); //not necessary if extending Object.
    }

//    public boolean isComplete() {
//        //if the process fails unexpectedly, it will appear done without
//        // completing the cleanup in the done() method. Use this method instead of isDone().
//        return processComplete;
//    }
    boolean kill() {
        killed = true;
        //harder cancel as it closes all I/0 Channels
        return cancel(true);

    }

    private boolean isKilled() {
        return killed;
    }
}
