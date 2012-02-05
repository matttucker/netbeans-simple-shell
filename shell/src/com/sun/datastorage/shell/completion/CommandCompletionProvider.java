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
package com.sun.datastorage.shell.completion;

import java.util.Set;
import java.io.File;
import java.util.HashMap;
import javax.swing.text.BadLocationException;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import com.sun.datastorage.shell.*;
import java.io.IOException;
import org.openide.filesystems.FileObject;

public class CommandCompletionProvider extends ShellCompletionProvider {

    private Commander commander;

    /**
     * Creates a new instance of ShellCompletionProvider
     */
    public CommandCompletionProvider(String CommanderClassName) throws ClassNotFoundException {
        Lookup lkp = MimeLookup.getLookup(MimePath.get("text/x-shell"));
        Class clazz = Class.forName(CommanderClassName, false, Lookup.getDefault().lookup(ClassLoader.class));
        commander = (Commander) lkp.lookup(clazz);
    }

    public CommandCompletionProvider() throws ClassNotFoundException {
        this("com.sun.datastorage.shell.command.ShellCommander");
    }

    public static CommandCompletionProvider create(FileObject inst) throws IOException, ClassNotFoundException {
        String className = (String) inst.getAttribute("CommanderClass");

        return new CommandCompletionProvider(className);
    }

    /**
     *
     */
    @Override
    public void addCompletionItems(final CompletionResultSet completionResultSet, ShellEditorDocument doc, String filter, int startOffset, int caretOffset) {
        try {

            File dirAbsolute = null;


            int argNum = doc.getArgNum(caretOffset);
            String[] cmdArgs = CommandTokenizer.ParseArgs(doc.getCommandString());

            // add commands that match filter
            HashMap<String, Command> cmdMap = commander.getCmdMap();

            if (argNum == 0) {
                //the filter is part of a command so go lookfor it.
                Set<String> cmdKeys = cmdMap.keySet();
                for (String cmdKey : cmdKeys) {
                    if (cmdKey.startsWith(filter)) {
                        completionResultSet.addItem(new CommandCompletionItem(cmdKey, cmdMap.get(cmdKey), startOffset, caretOffset));
                    }
                }
            } else {
                // the filter is part of an argument. If the cmdKey is a shell command then let it supply the completion item as it sees fit.
                String cmdKey = doc.getCmd(caretOffset);
                Command cmd = cmdMap.get(cmdKey);
                if (cmd != null) {
                    cmd.addArgCompletionItems(completionResultSet, doc, filter, startOffset, caretOffset, cmdArgs, argNum );
                }

            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    } // end add completion items
}
