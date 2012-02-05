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
import com.sun.datastorage.shell.ShellEditorDocument;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.filesystems.FileObject;

/**
 *
 * @author 
 */
class EvalfCmd extends Command {

    EvalfCmd(Commander cmdr) {
        super(cmdr, "evalf", "com.sun.datastorage.shell.command.evalf", "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/docs/shell-command-evalf.html");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: evalf <fileName>");
    }

    @Override
    public void execute(String[] args) throws Exception {

        super.execute(args);
        
        if (otherArgs.length == 1) {
            String fileName = otherArgs[0];
            FileObject scriptFileObject = null;

            scriptFileObject = cmdr.getDoc().getPathManager().getFileObject(fileName);

            evaluate(scriptFileObject);


        } else {
            printUsage();
        }
    }

    public void evaluate(File scriptFile) throws FileNotFoundException, ScriptException, IOException {
        FileInputStream scriptStream = new FileInputStream(scriptFile);
        evaluate(scriptFile.getCanonicalPath(), scriptStream);
    }

    public void evaluate(FileObject fileObject) throws FileNotFoundException, ScriptException, IOException {
        InputStream scriptStream = fileObject.getInputStream();
        evaluate(fileObject.getName(), scriptStream);
        // evaluate(FileUtil.getFileDisplayName(fileObject), scriptStream);
    }

    public void evaluate(String fileName, InputStream stream) throws ScriptException, IOException {
        Reader reader = new InputStreamReader(stream);

        // Add one more binding using a special reserved variable name
        // to tell the script engine the name of the file it will be executing.
        // This allows it to provide better error messages.
        cmdr.getDoc().getScriptEngine().put(ScriptEngine.FILENAME, fileName);

        // Run the script
        cmdr.getDoc().getScriptEngine().eval(reader);
        reader.close();
    }

    @Override
    public void addArgCompletionItems(final CompletionResultSet completionResultSet,
            ShellEditorDocument doc, String filter, int startOffset,
            int caretOffset, String[] cmdArgs, int argNum) {

        if (argNum <= 1) {
            //
            // add file names that match filter
            //
            //TODO: get this from preferences.
            String[] exts = {".js"};
            addFileCompletionItemsExt(completionResultSet, doc, filter, startOffset, caretOffset, exts);
        }
    }
}
