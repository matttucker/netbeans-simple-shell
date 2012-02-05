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
import java.io.IOException;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

class EditCmd extends Command {

    EditCmd(Commander cmdr) {
        super(cmdr, "edit", "com.sun.datastorage.shell.command.edit", "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/docs/shell-command-edit.html");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: edit <fileName>");
    }

    @Override
    public void execute(String[] args) throws Exception {
        super.execute(args);
        if (otherArgs.length == 1) {
            if (cmdr.getDoc() instanceof ShellEditorDocument) {
                String fileName = otherArgs[0];
                FileObject fo = null;
                try {
                    fo = cmdr.getDoc().getPathManager().getFileObject(fileName);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                if (fo != null) {
                    try {
                        DataObject dob = DataObject.find(fo);
                        OpenCookie open = dob.getCookie(OpenCookie.class);
                        open.open();
                    } catch (DataObjectNotFoundException ex) {
                        cmdr.getDoc().getErrorWriter().println("File not found.");
                    }
                } else {
                    cmdr.getDoc().getErrorWriter().println("File not found.");
                }
            } else {
                cmdr.getDoc().getErrorWriter().println("Command not supported here.");
            }
        } else {
            printUsage();
        }
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
            String[] exts = {"js"};
            addFileCompletionItemsExt(completionResultSet, doc, filter, startOffset, caretOffset, exts);
        }
    }
}
