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

import com.sun.datastorage.shell.highlighting.ShellHighlighter;
import java.io.PrintWriter;
import java.util.Vector;
import javax.script.ScriptEngine;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import org.openide.util.Cancellable;

public interface ShellDocument extends Document, Cancellable, ShellProvider {

    public static final String PROP_SHELL_PATH = "SHELL_PATH";
    public static final String PROP_SHELL_SCRIPT_ENGINE = "SHELL_SCRIPT_ENGINE";
    public static final String SCRIPT_ENGINE_DEFAULT = "ECMAScript";
    public static final String PROP_SHELL_LINE_LIMIT = "SHELL_LINE_LIMIT";
    public static final String PROP_SHELL_STARTUP_CMD = "SHELL_STARTUP_CMD";
    public static final String STARTUP_CMD_DEFAULT = "shell.js";
    public static final int LINE_LIMIT_DEFAULT = 1000;
    public static final ShellManager shellManager = new ShellManager();

    public JobManager getJobManager();

    public int getShellId();

    public void insertTextLater(String text);

    public void insertTextLater(String string, ShellHighlighter layer);

    public void insertPromptLater();

    public Vector<Commander> getCommanderList();

    public CommandHistory getCommandHistory();

    public PrintWriter getOutWriter();

    public PrintWriter getErrorWriter();

    public ScriptEngine getScriptEngine();

    public boolean getScriptMode();

    public PathManager getPathManager();

    public JEditorPane getEditorPane();

    public boolean processCmd(String cmd) throws Exception;

    public void runStartupCmd();

    public void setEditorPane(JEditorPane pane);

    public String getScriptExtension();

    public void addCommander(Commander commander);

    public void initCommanders();

    public void close();

    public int command(String cmd, boolean background, boolean waitTillDone);

    public boolean isCommandDone();

    public void setShellId(int id);

    public boolean waitTillCommandDone();

    public void setTitle(String name);

    public ShellProgress getProgress();

    public Object getAnswer();

    public String getDefaultPath();

    public void setDefaultPath(String string);

    public void setPathPreference();
}
