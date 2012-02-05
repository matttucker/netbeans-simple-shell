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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import javax.swing.JEditorPane;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;

public class ShellPlainDocument extends PlainDocument implements ShellDocument {

    JEditorPane editorPane;
    PathManager pathManager = new PathManager();
    CommandHistory cmdHistory;
    Preferences pref;
    ScriptEngineManager scriptMgr;
    public ScriptEngine scriptEngine;
    public SimpleScriptContext scriptContext;
    public PrintWriter shellOut;
    public PrintWriter shellError;
    boolean scriptMode = true;
    Vector<Commander> commanderList;
    public int promptEndIdx;
    public String prompt;
    public SimpleAttributeSet promptAttribute;
    public int lineCnt;
    public String lineDelimiter = "\\n";
    public String newline = "\n";
    public SimpleAttributeSet shellAttribute;
    public ShellProcess shellProcess;
    public ShellPlainProgress progress;

    public ShellPlainDocument() {
        commanderList = new Vector<Commander>();

        cmdHistory = new CommandHistory();


//        prompt = "> ";
//        promptEndIdx = -1;
//        lineCnt = -1;


        //create the script engine
        scriptMgr = new ScriptEngineManager();
        scriptEngine = scriptMgr.getEngineByName("JavaScript");

        if (scriptEngine == null) {
            System.out.println("\n\n\ncan't find script engine!!!\n\n\n");
            System.out.flush();
        }

        scriptContext = new SimpleScriptContext();
        scriptEngine.setContext(scriptContext);


        shellOut = new PrintWriter(System.out, true);
        setWriter(shellOut);

        shellError = new PrintWriter(System.out, true);
        setErrorWriter(shellError);


//        promptAttribute = new SimpleAttributeSet();
//        StyleConstants.setFontFamily(promptAttribute, "SansSerif");
//        StyleConstants.setFontSize(promptAttribute, 14);
//        StyleConstants.setForeground(promptAttribute, Color.black);




        shellProcess = null;


        editorPane = new JEditorPane();
        progress = new ShellPlainProgress();

        String defaultPath = getPathManager().getCwd().getAbsolutePath() + "/scripts/js";

        String pathStr = System.getenv("SHELL_PATH");
        if (pathStr != null) {
            setPath(pathStr);
        } else {
            setPath(".");
        }
//        pref = NbPreferences.forModule(ShellDocument.class);
//        
//        setPath(pref.get(PROP_SHELL_PATH, defaultPath));
//        // if we get the default, then write it to the preferences so the options dialog can see it.
//        pref.put(PROP_SHELL_PATH, getPathManager().toString());
//
//
//        pref.addPreferenceChangeListener(new PreferenceChangeListener() {
//
//            public void preferenceChange(PreferenceChangeEvent evt) {
//                if (evt.getKey().equals(PROP_SHELL_PATH)) {
//
//                    setPath(evt.getNewValue());
//
//                } else if (evt.getKey().equals(PROP_SHELL_SCRIPT_ENGINE)) {
//
//                    setScriptEngine(evt.getNewValue());
//
//                }
//            }
//        });
    }

    /**
     * 
     * @param engineName
     */
    void setScriptEngine(String engineName) {
        //create the script engine
        scriptMgr = new ScriptEngineManager();

        scriptEngine = scriptMgr.getEngineByName(engineName);

        if (scriptEngine != null) {
            System.out.println("Setting script engine to " + engineName);
            scriptContext = new SimpleScriptContext();
            scriptEngine.setContext(scriptContext);
            setWriter(shellOut);
            setErrorWriter(shellError);

            initCommanders();
        } else {
            // pref.put(PROP_SHELL_SCRIPT_ENGINE, SCRIPT_ENGINE_DEFAULT);
        }
    }

    private void setPath(String pathString) {
        String[] paths = pathString.split(";");
        boolean aPathNotFound = false;

        pathManager.clearPath();

        for (String aPath : paths) {
            try {
                pathManager.addPath(aPath);
            } catch (final FileNotFoundException ex) {
                shellError.println("Warning: Path could not be added. " + ex.getMessage());

                aPathNotFound = true;
            }
        }

        if (aPathNotFound) {
            // insertPrompt();
            setPath(".");
        }
    }

    public void setWriter(PrintWriter out) {
        shellOut = out;
        scriptContext.setWriter(shellOut);
        scriptEngine.put("shellOut", shellOut);
    }

    public void setErrorWriter(PrintWriter error) {
        shellError = error;
        scriptContext.setErrorWriter(shellError);
        scriptEngine.put("shellError", shellError);
    }

    public CommandHistory getCommandHistory() {
        return cmdHistory;
    }

    public Vector<Commander> getCommanderList() {
        return commanderList;
    }

    /**
     * Gracefully close the shell document. Close all registered commanders.
     */
    public void close() {
        scriptContext = null;
        scriptEngine = null;
        if (commanderList != null) {
            for (Commander commander : commanderList) {
                commander.close();
            }
        }

        commanderList.clear();
    }

    public PrintWriter getOutWriter() {
        return shellOut;
    }

    public PrintWriter getErrorWriter() {
        return shellError;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public boolean getScriptMode() {
        return scriptMode;
    }

    public ShellPlainProgress getProgress() {
        return progress;
    }

    public void setEditorPane(JEditorPane pane) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addCommander(Commander commander) {
        commanderList.add(commander);
    }

    public void initCommanders() {
        if (commanderList != null) {
            for (Commander commander : commanderList) {
                commander.init((ShellDocument) this);
            }
        }
    }

    public PathManager getPathManager() {
        return pathManager;
    }

    public String getScriptExtension() {
        return "js";
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public boolean cancel() {
        //     throw new UnsupportedOperationException("Not supported yet.");
        return false;
    }

    public void setTitle(String name) {
    }

    /**
     * Run a command in the background.
     *
     * @param command command string to be executed in this shell.
     * @parm background
     * @param waitForComplete  if true waits for the commmand to complete before returning.
     */
    public int command(String cmd, boolean background, boolean waitTillDone) {
        ShellProcess process;

        cmd += "\n";
        if (background) {
            //don't insert, don't prompt on complete
            process = new ShellProcess(this, cmd, false, false, false);
        } else {
            process = new ShellProcess(this, cmd, true, true, true);
            setShellProcess(process);
        }

        process.execute();

        if (waitTillDone) {
            ShellProcess.waitTillDone(process.getPid());
        }

        return process.getPid();
    }

    public boolean isCommandDone() {
        return ShellProcess.isDone(shellProcess.getPid());
    }

    private synchronized void setShellProcess(ShellProcess process) {
        shellProcess = process;
    }

    @Override
    public ShellDocument getShellDocument() {
        return this;
    }

    @Override
    public void insertTextLater(String string, ShellHighlighter layer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    public void displayLines(int offs, String str, AttributeSet attr) throws BadLocationException {
//        int endIdx;
//
//        super.insertString(offs, str, attr);
//
//        endIdx = getLength() - 1;
//        promptEndIdx = endIdx;
//
//        editorPane.setCaretPosition(endIdx + 1);
//    }
    @Override
    public void insertTextLater(String text) {
//        try {
//            super.insertString(getLength(), text, null);
//        } catch (BadLocationException ex) {
//            ex.printStackTrace(shellError);
//        }
    }

    @Override
    public void insertPromptLater() {
//        try {
//
//            int length;
//            lineCnt += 1;
//
//            java.lang.String promptString = new java.lang.String(java.lang.Integer.toString(lineCnt) + prompt);
//
//            length = getLength();
//
//            displayLines(length, promptString, promptAttribute);
//        } catch (BadLocationException ex) {
//            ex.printStackTrace(shellError);
//        }
    }

    @Override
    public void runStartupCmd() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JobManager getJobManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean processCmd(String cmd) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getShellId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setShellId(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean waitTillCommandDone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getAnswer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDefaultPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDefaultPath(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPathPreference() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
