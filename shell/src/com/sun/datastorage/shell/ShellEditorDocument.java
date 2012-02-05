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
import com.sun.datastorage.shell.highlighting.ShellHighlightsLayerFactory;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.SimpleAttributeSet;
import org.netbeans.modules.editor.NbEditorDocument;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

public class ShellEditorDocument extends NbEditorDocument implements ShellDocument {

    JEditorPane editorPane;
    PathManager pathManager = new PathManager();
    JobManager jobManager = new JobManager();
    CommandHistory cmdHistory;
    Preferences pref;
    ScriptEngineManager scriptMgr;
    ScriptEngine scriptEngine;
    SimpleScriptContext scriptContext;
    public PrintWriter shellOut;
    public PrintWriter shellError;
    boolean scriptMode = true;
    Vector<Commander> commanderList;
    public int promptEndIdx;
    public String prompt;
    public SimpleAttributeSet promptAttribute;
    public int cmdNum;
    public String lineDelimiter = "\\n";
    public String newline = "\n";
    public SimpleAttributeSet shellAttribute;
    public ShellProcess shellProcess;
    public ShellEditorProgress progress;
    //private ShellErrorHighlighter outLayer;
    private int lineLimitBuffer;
    private int lineLimit;
    private int shellId;
    String defaultPath = "";

    public ShellEditorDocument(String mimeType) {
        super(mimeType);
        shellManager.add(this);
        commanderList = new Vector<Commander>();

        cmdHistory = new CommandHistory();

        prompt = "> ";
        promptEndIdx = -1;
        cmdNum = -1;

        pref = NbPreferences.forModule(ShellDocument.class);

        //create shell writer and context so that scripts and controllers
        //can display messages in the shell.
        //shellOut = new PrintWriter(new ShellWriter(this, ShellHighlightsLayerFactory.getShellOutHighlighter(this)), false);
        shellOut = new PrintWriter(new BufferedWriter(new ShellWriter(this)));
        shellError = new PrintWriter(new BufferedWriter(new ShellWriter(this, ShellHighlightsLayerFactory.getShellErrorHighlighter(this))), false);

        //outLayer = ShellHighlightsLayerFactory.getShellErrorHighlighter(this);
        setScriptEngine(pref.get(PROP_SHELL_SCRIPT_ENGINE, SCRIPT_ENGINE_DEFAULT));

        progress = new ShellEditorProgress();

        shellProcess = null;

//        String defaultPath = getPathManager().getCwd().getAbsolutePath() + "/scripts/js";



        lineLimit = pref.getInt(PROP_SHELL_LINE_LIMIT, LINE_LIMIT_DEFAULT);
        lineLimitBuffer = (int) (0.10 * lineLimit);
        if (lineLimitBuffer < 5) {
            lineLimitBuffer = 5;
        }
        pref.addPreferenceChangeListener(new PreferenceChangeListener() {

            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (evt.getKey().equals(PROP_SHELL_PATH)) {

                    setPath(evt.getNewValue());

                } else if (evt.getKey().equals(PROP_SHELL_SCRIPT_ENGINE)) {

                    setScriptEngine(evt.getNewValue());

                } else if (evt.getKey().equals(PROP_SHELL_LINE_LIMIT)) {

                    lineLimit = Integer.parseInt(evt.getNewValue());
                    lineLimitBuffer = (int) (0.10 * lineLimit);
                    if (lineLimitBuffer < 5) {
                        lineLimitBuffer = 5;
                    }
                }
            }
        });
    }

    ShellEditorDocument() {
        this("text/x-shell");
    }

    @Override
    public void setPathPreference() {
        setPath(pref.get(PROP_SHELL_PATH, defaultPath));
        // if we get the default, then write it to the preferences so the options dialog can see it.
        pref.put(PROP_SHELL_PATH, getPathManager().toString());

    }

    private void setPath(String pathString) {
        String[] paths = pathString.split(";");
        boolean aPathNotFound = false;

        pathManager.clearPath();

        for (String aPath : paths) {
            try {
                pathManager.addPath(aPath);
            } catch (FileNotFoundException ex) {
                // shellError.println("Warning: Path could not be added. " + ex.getMessage());
                // aPathNotFound = true;
            }
        }

        if (aPathNotFound) {
            insertPromptLater();
        }
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
            pref.put(PROP_SHELL_SCRIPT_ENGINE, SCRIPT_ENGINE_DEFAULT);
        }
    }

    @Override
    public void runStartupCmd() {
        command(pref.get(PROP_SHELL_STARTUP_CMD, STARTUP_CMD_DEFAULT), false, true);
    }

    /**
     * Inserts a string into the shell document and parses it for a shell insertCommand. 
     * Only insert after a insertCommand has been finished and past the prompt.
     * Commands can be pasted in as well.
     *
     * @param offset
     * @param string
     * @param attribute
     */
    @Override
    public synchronized void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {

        // Only allow an insertion for the following cases:
        //   - str is not null
        //   - cursor location is after the prompt
        //   - command parser has completed its job.
        if (str != null && (shellProcess == null || shellProcess.isProcessDone())) {

            if (offset <= promptEndIdx) {
                offset = getLength();
                editorPane.setCaretPosition(offset);
            }
            if (str.contains("\n")) {


                org.netbeans.api.editor.completion.Completion.get().hideCompletion();


                // a command needs processing so go get it.
                String cmdStr = getCommandString();

                cmdStr = cmdStr + str;

                if (cmdStr.equals("\n")) {
                    // this is the inserted new line only case
                    //superInsertStringLater(offset, "\n", attr);
                    superInsertString(offset, "\n", attr);
                    insertPrompt();
                   
                } else {
                    if (str.equals("\n")) {
                        // single command line. Allow new line to enter the whole 
                        //command line thus we don't use offset but getLength().
                        superInsertString(getLength(), "\n", attr);
                        //superInsertString(offset, str, attr);
                        lineCheck();

                        //don't insert the command string as it has already been inserted.
                        shellProcess = new ShellProcess(this, cmdStr, false, true, true);
                        shellProcess.execute();
                    } else {
                        //insert the command string.
                        shellProcess = new ShellProcess(this, cmdStr, true, true, true);
                        shellProcess.execute();
                    }
                }

            } else {
                //partial command so don't process it just yet.
                //insertLayeredString(offset, str, outLayer);
                //superInsertStringLater(offset, str, attr);
                superInsertString(offset, str, attr);

            }
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void superInsertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        super.insertString(offset, str, attr);

    }

    //for calling from within a nested class.
    public void superInsertStringLater(final int offset, final String str, final AttributeSet attr) throws BadLocationException {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                try {
                    superInsertString(offset, str, attr);
                    lineCheck();

                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }


            }
        });
    }

    public void insertLayeredString(final int offset, final String text, final ShellHighlighter layer) throws BadLocationException {
        if (text.length() > 0) {
            try {
                int endOffset = offset + text.length();

                superInsertString(offset, text, null);

                if (layer != null && !text.equals("\r\n")) {
                    layer.addHighlight(offset, endOffset);
                }
                lineCheck();

            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public void appendLayeredString(final String text, final ShellHighlighter layer) throws BadLocationException {
        insertLayeredString(getLength(), text, layer);
    }

    @Override
    public void remove(int offs, int length) throws BadLocationException {

        if (offs > promptEndIdx) {
            super.remove(offs, length);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }

    }

    synchronized public int getPromptEndIdx() {
        return promptEndIdx;
    }

    @Override
    public void setTitle(String title) {
        ((ShellEditorPane) editorPane).setTitle(title);
    }

    /**
     *  Clear a partial insertCommand.
     */
    void resetCmd() throws BadLocationException {

        remove(getPromptEndIdx() + 1, (getLength() - 1) - getPromptEndIdx());
        editorPane.setCaretPosition(getPromptEndIdx() + 1);
    }

    @Override
    public ShellEditorProgress getProgress() {
        return progress;
    }

    /**
     * Insert text directly into the document.
     * (assumed to be called outside EDT)
     */
    @Override
    public void insertTextLater(final String text) {
        insertTextLater(text, null);
    }

    /**
     * Insert text directly into the document.
     * (assumed to be called outside EDT)
     */
    @Override
    public void insertTextLater(final String text, final ShellHighlighter layer) {
        if (shellProcess == null || (shellProcess.isProcessDone() && !shellProcess.isCancelled())) {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    insertTextBeforePrompt(text, layer);
                }
            });
        } else {

            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {

                    try {
                        appendLayeredString(text, layer);
                        editorPane.setCaretPosition(getLength());

                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }


                }
            });

        }
    }

    /**
     * Insert text directly into the document.
     * (assumed to be called outside EDT)
     */
    public void insertTextBeforePrompt(final String text, final ShellHighlighter layer) {

        int cmdLength;

        String insertText = text;
//                if (!insertText.endsWith("\n")) {
//                   insertText += "\n";
//               }

        try {
            int offsetCmdLine = getOffsetOfCommandLine();

            //cmdLine = getText(offsetCmdLine, getLength() - offsetCmdLine);
            cmdLength = getLength() - promptEndIdx;

            //insert the text before the command line.
            if (offsetCmdLine < 1) {
                offsetCmdLine = 1;
                insertText += "\n";
            }

            insertLayeredString(offsetCmdLine - 1, insertText, layer);

            //adjust for the new promptEndIdx
            promptEndIdx = getLength() - cmdLength;
            editorPane.setCaretPosition(getLength());

        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    /**
     * Get the offset for the start of the command line.
     * @return
     * @throws javax.swing.text.BadLocationException
     */
    public int getOffsetOfCommandLine() throws BadLocationException {
        int offset = promptEndIdx;
        String str;

        do {

            offset--;
            if (offset < 0) {
                offset = -1;
                break;
            }
            str = getText(offset, 1);
        } while (str.equals("\n") == false);
        offset++;

        return offset;
    }

    /**
     * Insert a prompt into the document.
     * (assumed to be called outside EDT)
     */
    @Override
    public void insertPromptLater() {

        // final ShellOutHighlighter layer = ShellHighlightsLayerFactory.getShellOutHighlighter(this);
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                insertPrompt();
            }
        });
    }

    public void insertPrompt() {

        try {
            cmdNum += 1;

            //Ensure that prompt starts at the start of the line.
            if (getLength() - 1 > 0 && getText(getLength() - 1, 1).equals("\n") == false) {
                superInsertString(getLength(), "\n", null);
            }
            String text = getPromptString();

            //insertLayeredString(getLength(), text, layer);
            insertLayeredString(getLength(), text, null);
            editorPane.setCaretPosition(getLength());
            promptEndIdx = getLength() - 1;
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }


    }

    public String getPromptString() {
        return new String(java.lang.Integer.toString(cmdNum) + prompt);
    }

    /**
     * Run a command in the background.
     *
     * @param command command string to be executed in this shell.
     * @parm background
     * @param waitForComplete  if true waits for the commmand to complete before returning.
     */
    @Override
    public int command(String cmd, boolean background, boolean waitTillDone) {

        if (background) {
            return command(cmd, background, false, false, waitTillDone);
        } else {
            return command(cmd, background, true, true, waitTillDone);
        }
    }

    /**
     * Run a command in the background.
     *
     * @param command command string to be executed in this shell.
     * @param insertCommand
     * @param promptOnComplete
     * @param addToHistory
     * @param waitForComplete  if true waits for the commmand to complete before returning.
     */
    public int command(String cmd, boolean background, boolean promptOnComplete,
            boolean addToHistory, boolean waitTillDone) {
        ShellProcess process;

        cmd += "\n";

        if (background == false && (shellProcess != null && shellProcess.isProcessDone() == false)) {
            //wait for the previous command to complete
            if (waitTillCommandDone() == false) {
                return -1;
            }
        }

        if (background) {
            process = new ShellProcess(this, cmd, !background, promptOnComplete, addToHistory);
            jobManager.add(process);
        } else {

            process = new ShellProcess(this, cmd, !background, promptOnComplete, addToHistory);
            setShellProcess(process);

        }


        process.execute();

        if (waitTillDone) {
            ShellProcess.waitTillDone(process.getPid());
        }

        return process.getPid();
    }

    @Override
    public boolean isCommandDone() {
        return ShellProcess.isDone(shellProcess.getPid());
    }

    @Override
    public boolean waitTillCommandDone() {
        return ShellProcess.waitTillDone(shellProcess.getPid());
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
//TODO is this the best mechanism? should lookup be used.

    @Override
    public void setEditorPane(JEditorPane pane) {
        this.editorPane = pane;
    }

    @Override
    public JEditorPane getEditorPane() {
        return editorPane;
    }

    @Override
    public PathManager getPathManager() {
        return pathManager;
    }

    @Override
    public CommandHistory getCommandHistory() {
        return cmdHistory;
    }

    @Override
    public Vector<Commander> getCommanderList() {
        return commanderList;
    }

    @Override
    public void addCommander(Commander commander) {
        commanderList.add(commander);
    }

    void setCommanderList(Vector<Commander> commanderList) {
        this.commanderList = commanderList;
    }

    /**
     *
     *  @return the extension of script files for the current script engine. For instance,
     * if the scriptengine is javascript, "js" is returned.
     */
    @Override
    public String getScriptExtension() {

        List<String> extensions = scriptEngine.getFactory().getExtensions();
        return extensions.get(0);

    }

    @Override
    public void initCommanders() {
        if (commanderList != null) {
            for (Commander commander : commanderList) {
                commander.init(this);
            }
        }
    }

    /**
     * Send cancel request to current process.
     */
    @Override
    public boolean cancel() {
        if (shellProcess != null && !shellProcess.isProcessDone()) {
            shellProcess.cancel();
        }
        return true;
    }

    /**
     * Send kill request to current process.
     */
    public boolean kill() {
        System.out.println("Killing process.");
        if (shellProcess != null && !shellProcess.isProcessDone()) {
            return shellProcess.kill();
        }
        return true;
    }

    /**
     * Gracefully close the shell document. Close all registered commanders.
     */
    @Override
    public void close() {
        shellManager.remove(this);
        scriptContext = null;
        scriptEngine = null;
        if (commanderList != null) {
            for (Commander commander : commanderList) {
                commander.close();
            }
        }

        commanderList.clear();
        commanderList = null;

//        final TopComponent tc = (TopComponent) SwingUtilities.getAncestorOfClass(TopComponent.class,
//                editorPane);
//
//        EventQueue.invokeLater(
//                new Runnable() {
//
//                    @Override
//                    public void run() {
//                        tc.close();
//                    }
//                });

    }

    @Override
    public PrintWriter getOutWriter() {
        return shellOut;
    }

    @Override
    public PrintWriter getErrorWriter() {
        return shellError;
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    @Override
    public boolean getScriptMode() {
        return scriptMode;
    }

    @Override
    public ShellDocument getShellDocument() {
        return this;
    }

    private synchronized void setShellProcess(ShellProcess process) {
        shellProcess = process;
    }

    public String getCommandString() throws BadLocationException {
        int length;

        length = getLength() - 1 - promptEndIdx;
        if (promptEndIdx > 0 && length > 0) {
            return getText(promptEndIdx + 1, length);
        } else {
            return "";
        }

    }

    /**
     * 
     * @param offset
     * @return
     */
    public String getCmd(int offset) throws BadLocationException {
        String cmdStr = getCommandString();
        String[] args = cmdStr.split("\\s+");

        if (args.length == 0) {
            return null;
        } else {
            return args[0];
        }
    }

    /**
     *  Get the argument number. 0 = cmd, 1 = 1st argument, ...
     * @param offset
     * @return
     * @throws javax.swing.text.BadLocationException
     */
    public int getArgNum(int offset) throws BadLocationException {
        String cmdStr = getCommandString();
        int roffset = offset - (promptEndIdx + 1);
        if (cmdStr.isEmpty()) {
            return 0;
        }
        cmdStr = cmdStr.substring(0, roffset);
        char[] line = cmdStr.toCharArray();

        int i = line.length - 1;
        
        int argNum = 0;
        int optNum = 0;
        boolean firstTransition = true;
        boolean isOption = false;
        char prev = line[i];
        if (prev == ' ') {
            firstTransition = false;
            argNum++;
        }
        while (--i > -1) {
            char c = line[i];

            // count transitions from white to nonwhite.
            if (Character.isWhitespace(c) && !Character.isWhitespace(prev)) {

                if (prev == '-') {
                    optNum--;
                    if (firstTransition) {
                        firstTransition = false;
                        isOption = true;
                    }
                }else {
                    argNum++;
                     if (firstTransition) {
                        firstTransition = false;
                    }
                }
                
            }
            prev = c;
        }

        if (isOption) {
            return optNum;
        } else {
            return argNum;
        }
        
    }

    public void lineCheck() throws BadLocationException {

        int lineNumber = NbDocument.findLineNumber(this, getLength());
        if (lineNumber > lineLimit + lineLimitBuffer) {
            int length = NbDocument.findLineOffset(this, lineLimitBuffer);
            super.remove(0, length);
            promptEndIdx -= length;

            //TODO: better way then wacking all the highlights?
            ShellHighlightsLayerFactory.getShellOutHighlighter(this).getHighlightsBag().clear();
            ShellHighlightsLayerFactory.getShellErrorHighlighter(this).getHighlightsBag().clear();
        }

    }

    @Override
    public JobManager getJobManager() {
        return jobManager;
    }

    @Override
    public boolean processCmd(String cmd) throws Exception {
        boolean cmdProcessed = false;
        // go through the commanders and try to find one
        // that can process the command.
        if (getCommanderList() != null) {
            for (Commander commander : getCommanderList()) {

                commander.setCancel(false);
                cmdProcessed = commander.parse(cmd);

                //if a commander processes the command then exit the loop
                if (cmdProcessed) {
                    break;
                }
            }
        }
        return cmdProcessed;
    }

    @Override
    public int getShellId() {
        return shellId;
    }

    @Override
    public void setShellId(int id) {
        shellId = id;
    }

    @Override
    public Object getAnswer() {
        try {
            return scriptEngine.get("ANSWER");
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String getDefaultPath() {
        return defaultPath;
    }

    @Override
    public void setDefaultPath(String path) {
        defaultPath = path;
    }
}
