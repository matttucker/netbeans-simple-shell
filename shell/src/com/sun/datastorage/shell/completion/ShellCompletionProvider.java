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

import com.sun.datastorage.shell.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

public class ShellCompletionProvider implements CompletionProvider {

    /**
     * Creates a new instance of ShellCompletionProvider
     */
    public ShellCompletionProvider() {
    }

    @Override
    public CompletionTask createTask(int type, JTextComponent jTextComponent) {

        if (type != CompletionProvider.COMPLETION_QUERY_TYPE) {
            return null;
        }
        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            @Override
            protected void query(final CompletionResultSet completionResultSet, Document document, final int caretOffset) {
                final StyledDocument bDoc = (StyledDocument) document;
                final ShellEditorDocument shellDoc = (ShellEditorDocument) document;


                class Operation implements Runnable {

                    boolean showCompletion = false;
                    String filter = null;
                    int startOffset = caretOffset - 1;

                    @Override
                    public void run() {

                        try {
                            final int lineStartOffset = getOffsetFirstNonWhiteOnRow(bDoc, caretOffset);
                            if (lineStartOffset > -1 && caretOffset > lineStartOffset) {
                                final char[] line = bDoc.getText(lineStartOffset, caretOffset - lineStartOffset).toCharArray();
                                final int whiteOffset = getIndexOfLastWhite(line);
                                filter = new String(line, whiteOffset + 1, line.length - whiteOffset - 1);
                                if (whiteOffset > 0) {
                                    startOffset = lineStartOffset + whiteOffset + 1;
                                } else {
                                    startOffset = lineStartOffset;
                                }
                            } else {
                                showCompletion = true;
                            }
                        } catch (BadLocationException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }

                    public void finishWork() {

                        //if (startOffset > -1 && caretOffset > startOffset && filter != null) {
                        if (startOffset > -1 && filter != null) {
                            addCompletionItems(completionResultSet, shellDoc, filter, startOffset, caretOffset);
                        }

                        completionResultSet.setAnchorOffset(startOffset);
                        completionResultSet.finish();
                    }
                }

                Operation oper = new Operation();
                try {
                    NbDocument.runAtomicAsUser(bDoc, oper);
                    oper.finishWork();
                } catch (BadLocationException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }, jTextComponent);
    }

    @Override
    public int getAutoQueryTypes(JTextComponent jTextComponent, String string) {
        //This means that the code completion box will never appear unless the user explicitly asks for it.
        return 0;
    }

    /**
     * Get the offset of first non-white character on the row specified by offset.
     * 
     * @param doc
     * @param offset
     * @return offset of first non-white character on the row specified by offset.
     * @throws javax.swing.text.BadLocationException
     */
    static int getOffsetFirstNonWhiteOnRow(StyledDocument doc, int offset) throws BadLocationException {
        Element lineElement = doc.getParagraphElement(offset);
        int start = lineElement.getStartOffset();
        while (start + 1 < lineElement.getEndOffset()) {
            try {
                if (doc.getText(start, 1).charAt(0) != ' ') {
                    break;
                }
            } catch (BadLocationException ex) {
                throw (BadLocationException) new BadLocationException(
                        "calling getText(" + start + ", " + (start + 1)
                        + ") on doc of length: " + doc.getLength(), start).initCause(ex);
            }
            start++;
        }
        return start;
    }

    static int getIndexOfLastWhite(char[] line) {
        int i = line.length;
        while (--i > -1) {
            final char c = line[i];
            if (Character.isWhitespace(c)) {
                return i;
            }
        }
        return -1;
    }

    /**
     *
     */
    public void addCompletionItems(final CompletionResultSet completionResultSet, ShellEditorDocument doc, String target, int startOffset, int caretOffset) {
        try {
            if (doc.getArgNum(caretOffset) == 0) {
                //
                // add history commands that match the filter
                //
                for (String historyCmd : doc.getCommandHistory().getUniqueHistory()) {

                    //We exclude history commands that are only a single word as it is most likely they will be
                    //be completed by another provider.
                    if (historyCmd.startsWith(target) && (historyCmd.contains(" ") || historyCmd.contains("("))) {
                        completionResultSet.addItem(new ShellHistoryCompletionItem(historyCmd, startOffset, caretOffset));
                    }
                }
            }

//
//        try {
//            if (doc.getArgNum(caretOffset) == 0) {
            //
            // add file names that match filter
            //
//                PathManager pathManager = doc.getPathManager();
//
//                File[] fileMatches = pathManager.getFileList(target);
//
//                for (File fileMatch : fileMatches) {
//                    completionResultSet.addItem(new ShellFileCompletionItem(fileMatch, startOffset, caretOffset));
//                }
//            }
//        } catch (BadLocationException ex) {
//            Exceptions.printStackTrace(ex);
//        }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }



    }
}
