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

import java.util.Arrays;

public abstract class Commander  {

    protected String name;
    protected boolean enabled;
    protected ShellDocument doc;
    protected CommandMap cmdMap;
    private boolean canceled;

    public Commander() {
        cmdMap = new CommandMap();
        enabled = true;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public String name() {
        return name;
    }

    public void init() {
        init(doc);
    }

    public void init(ShellDocument doc) {
        this.doc = doc;
    }

    public CommandMap getCmdMap() {
        return cmdMap;
    }

    public void cancel() {
        setCancel(true);
    }

    public void setCancel(boolean value) {
        canceled = value;
    }

     
    public void checkCancelled() throws InterruptedException {
        if (Thread.currentThread().isInterrupted() || canceled) {
            throw new InterruptedException("process interrupted");
        }
    }

    /**
     * Method that will be closed when a shell document is closed.
     */
    public void close() {
    };

    public Command findCmd(String target) {
        return cmdMap.get(target);
    }

    public ShellDocument getDoc() {
        return doc;
    }

   
    public Commander copy() throws InstantiationException, IllegalAccessException {
        Commander cmdr = (Commander) this.getClass().newInstance();
        return cmdr;
    }

    public boolean parse(String cmdStr) throws Exception {

        String[] cmdArgs = CommandTokenizer.ParseArgs(cmdStr);
        String cmdName = cmdArgs[0];

        String[] args = null;
        if (cmdArgs.length > 1) {
            args = Arrays.copyOfRange(cmdArgs, 1, cmdArgs.length);
        }
        Command cmd = cmdMap.get(cmdName);

        if (cmd != null) {
            cmd.execute(args);
            return true;
        } else {
            return false;
        }
    }
}
