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

import com.sun.datastorage.shell.FileMatch;
import javax.swing.ImageIcon;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;
import org.openide.util.ImageUtilities;


public class ShellFileCompletionItem extends ShellCompletionItem {

    FileMatch fileMatch;
    String target;

    ;

    public ShellFileCompletionItem(String target, FileMatch fileMatch, int dotOffset, int carretOffset) {
        super(fileMatch.match, dotOffset, carretOffset);
        this.fileMatch = fileMatch;
        this.target = target;
        this.target = this.target.replace("\\", "/");
        icon = new ImageIcon(ImageUtilities.loadImage("com/sun/datastorage/shell/completion/resources/folder_16.gif"));
    }

    @Override
    public CompletionTask createDocumentationTask() {


        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            protected void query(CompletionResultSet completionResultSet, Document document, int i) {
                completionResultSet.setDocumentation(new ShellFileDocumentationItem(fileMatch.file));
                completionResultSet.finish();
            }
        });
    }

    @Override
    public int getSortPriority() {
        return 5;
    }

    @Override
    void doSubstitute(final JTextComponent component, final String toAdd, final int backOffset) {

        final StyledDocument doc = (StyledDocument) component.getDocument();
        final AbstractDocument abDoc = (AbstractDocument) component.getDocument();

        class AtomicChange implements Runnable {

            public void run() {

                int caretOffset = component.getCaretPosition();
                String value = fileMatch.match;
                value = value.replace("\\", "/");

                if (toAdd != null) {
                    value += toAdd;
                }
                // adjust dotOffset
                if (target.contains("/")) {
                    dotOffset = dotOffset + target.lastIndexOf("/") + 1;
                }
                try {
                    doc.remove(dotOffset, carretOffset - dotOffset);
                    abDoc.insertString(dotOffset, value, null);
                    component.setCaretPosition(component.getCaretPosition() - backOffset);
                } catch (BadLocationException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }

            }
        }
        AtomicChange ac = new AtomicChange();
        try {
            NbDocument.runAtomicAsUser(doc, ac);
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
}
