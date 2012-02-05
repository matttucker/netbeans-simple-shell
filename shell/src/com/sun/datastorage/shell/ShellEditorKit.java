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

import javax.swing.text.Document;
import javax.swing.Action;
import javax.swing.text.TextAction;
import org.netbeans.modules.editor.NbEditorKit;
import java.awt.event.ActionEvent;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseAction;
import javax.swing.text.BadLocationException;

public class ShellEditorKit extends NbEditorKit {

    public static final String MIME_TYPE = "text/x-shell"; // NOI18N

    /**
     * Creates a new instance of ShellEditorKit
     */
    public ShellEditorKit() {
    }

    /**
     * Retrieves the content type for this editor kit
     */
    @Override
    public String getContentType() {
        return MIME_TYPE;
    }

    //    @Override
    //    @SuppressWarnings(value = "deprecation")
    //    public Syntax createSyntax(Document document) {
    //        return new PlainSyntax();
    //    }
    @Override
    public Document createDefaultDocument() {
        Document doc = new ShellEditorDocument();
        Object mimeType = doc.getProperty("mimeType"); //NOI18N
        if (mimeType == null) {
            doc.putProperty("mimeType", getContentType()); //NOI18N
        }
        return doc;
    }

    @Override
    protected Action[] createActions() {
        Action[] superActions = super.createActions();
        Action[] shellActions = new Action[]{new LastCommandAction("shell-last-command"),
            new HomeAction("shell-begin-line"),
            new HistoryBackAction("shell-history-backward"),
            new HistoryForwardAction("shell-history-forward"),
            new DeleteForwardAction("shell-delete-forward"),
            new CopyAction("shell-copy"),
            new CancelAction("shell-cancel"),
            new KillAction("shell-kill")
        };
        return TextAction.augmentList(superActions, shellActions);
        // return shellActions;
    }

    /**
     * Go to home prompt location
     */
    public static class HomeAction extends BaseAction {

        public HomeAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent arg0, JTextComponent comp) {
            ShellEditorDocument doc = (ShellEditorDocument) comp.getDocument();
            comp.setCaretPosition(doc.promptEndIdx + 1);
        }
    }

    /**
     * Execute last insertCommand in history.
     */
    public static class LastCommandAction extends BaseAction {

        public LastCommandAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent arg0, JTextComponent comp) {

            ShellEditorDocument doc = (ShellEditorDocument) comp.getDocument();

            if (doc.getCommandHistory().hasPrevious()) {
                String cmd = doc.getCommandHistory().previous();
                doc.command(cmd, false, false);
            }
        }
    }

    /**
     * Display one commande back in the insertCommand history.
     */
    public static class HistoryBackAction extends BaseAction {

        public HistoryBackAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent arg0, JTextComponent comp) {
            String cmd = null;
            if (comp instanceof ShellEditorPane) {
                ShellEditorDocument doc = (ShellEditorDocument) comp.getDocument();
                comp.setCaretPosition(doc.promptEndIdx + 1);

                try {
                    doc.remove(doc.promptEndIdx + 1, (doc.getLength() - 1) - doc.promptEndIdx);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }

                if (doc.getCommandHistory().hasPrevious()) {
                    cmd = doc.getCommandHistory().previous();
                    try {
                        doc.insertString(doc.promptEndIdx + 1, cmd, doc.shellAttribute);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Display one insertCommand forward in the insertCommand history.
     */
    public static class HistoryForwardAction extends BaseAction {

        public HistoryForwardAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent arg0, JTextComponent comp) {
            String cmd;
            ShellEditorDocument doc = (ShellEditorDocument) comp.getDocument();
            comp.setCaretPosition(doc.promptEndIdx + 1);

            try {
                doc.remove(doc.promptEndIdx + 1, (doc.getLength() - 1) - doc.promptEndIdx);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }

            if (doc.getCommandHistory().hasNext()) {
                cmd = doc.getCommandHistory().next();
                try {
                    doc.insertString(doc.promptEndIdx + 1, cmd, doc.shellAttribute);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Delete from current caret position to end of line.
     */
    public static class DeleteForwardAction extends BaseAction {

        public DeleteForwardAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent arg0, JTextComponent comp) {
            ShellEditorDocument doc = (ShellEditorDocument) comp.getDocument();
            int offs = comp.getCaretPosition();

            if (offs > doc.promptEndIdx) {
                try {
                    doc.remove(offs, doc.getLength() - offs);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Cancel current shell process.
     */
    public static class CopyAction extends BaseAction {

        public CopyAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent arg0, JTextComponent comp) {
            ShellEditorDocument doc = (ShellEditorDocument) comp.getDocument();

            if (comp != null) {
                comp.copy();
            }
        }
    }

    /**
     * Cancel current shell process.
     */
    public static class CancelAction extends BaseAction {

        public CancelAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent arg0, JTextComponent comp) {
            ShellEditorDocument doc = (ShellEditorDocument) comp.getDocument();
            doc.cancel();


        }
    }

    /**
     * Kill shell  process.
     */
    public static class KillAction extends BaseAction {

        public KillAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent arg0, JTextComponent comp) {
            ShellEditorDocument doc = (ShellEditorDocument) comp.getDocument();
            doc.kill();
        }
    }
}
