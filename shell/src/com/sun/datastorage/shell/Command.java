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

import com.sun.datastorage.shell.CmdLineParser.OptionException;
import com.sun.datastorage.shell.completion.OptionCompletionItem;
import com.sun.datastorage.shell.completion.ShellFileCompletionItem;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.Vector;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public abstract class Command implements HelpCtx.Provider {

    public Commander cmdr;
    public String name;
    public String helpId;
    public String helpUrl;
    public CmdLineParser parser;
    public String[] otherArgs = null;

    public Command(Commander cmdr, String name, String helpId, String helpUrl) {
        this.cmdr = cmdr;
        this.name = name;
        this.helpId = helpId;
        this.helpUrl = helpUrl;

        parser = new CmdLineParser();
    }

    public Command(String name, String helpId, String helpUrl) {
        this(null, name, helpId, helpUrl);
    }

    public Command() {
        name = "";
        helpId = "";
        helpUrl = "";
    }

    public void printUsage() {
        printUsage(null);
    }

    public void execute() throws Exception {
        execute(null);
    }

    void parseArgs(String[] args) throws OptionException, Exception {
        try {
            parser.parse(args);
        } catch (CmdLineParser.OptionException e) {
            printUsage(args);
            throw e;
        }

        otherArgs = parser.getRemainingArgs();
    }

    public void addArgCompletionItems(final CompletionResultSet completionResultSet,
            ShellEditorDocument doc, String filter, int startOffset,
            int caretOffset, String[] cmdArgs, int argNum ) {
        //the filter is part of a command so go lookfor it.
        if (filter.startsWith("-")) {
            Set<String> optionList = parser.options.keySet();
            if (optionList != null) {
                for (String option : optionList) {
                    if (option.startsWith(filter)) {
                        completionResultSet.addItem(new OptionCompletionItem(option, this, startOffset, caretOffset));
                    }
                }
            }
        }
    }

    public static void addFileCompletionItemsExt(final CompletionResultSet completionResultSet,
            ShellEditorDocument doc, String filter, int startOffset, int caretOffset, String[] exts) {
        try {
            //
            // add file names that match filter
            //
            PathManager pathManager = doc.getPathManager();

            Vector<FileMatch> fileMatches = pathManager.getFileListExt(filter, exts);

            for (FileMatch fileMatch : fileMatches) {
                completionResultSet.addItem(new ShellFileCompletionItem(filter, fileMatch, startOffset, caretOffset));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * 
     * fileType :
     * DIRS_AND_FILES = 0;
     * FILES_ONLY = 1;
     * DIRS_ONLY = 2;
     * 
     *
     */
    public static void addFileCompletionItems(final CompletionResultSet completionResultSet, ShellEditorDocument doc, String filter, int startOffset, int caretOffset, int fileType) {
        try {
            //
            // add file names that match filter
            //
            PathManager pathManager = doc.getPathManager();

            Vector<FileMatch> fileMatches = pathManager.getFileList(filter, fileType);

            for (FileMatch fileMatch : fileMatches) {
                completionResultSet.addItem(new ShellFileCompletionItem(filter, fileMatch, startOffset, caretOffset));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(helpId);
    }

    public URL getHelpUrl() {
        try {
            return new URL(helpUrl);
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    /**
     *
     * @param args  array of args to print.
     */
    public static String argsToString(String[] args) {
        String str = " ";
        if (args != null) {
            for (String aArg : args) {
                str = str + aArg + " ";
            }
        }

        return str;
    }

    abstract public void printUsage(String[] args);

    public void execute(String[] args) throws Exception {
        parseArgs(args);
    }
}
