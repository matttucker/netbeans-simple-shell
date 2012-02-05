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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;


public class ShellCompletionItem implements CompletionItem {

    static protected Color fieldColor = Color.decode("0x0000B2");
    static protected ImageIcon fieldIcon = null;
    protected ImageIcon icon;
    protected int type;
    protected int carretOffset;
    protected int dotOffset;
    protected String text;

    public ShellCompletionItem(String text, int dotOffset, int carretOffset) {

        this.text = text;
        this.dotOffset = dotOffset;
        this.carretOffset = carretOffset;
    }

    @Override
    public void processKeyEvent(KeyEvent keyEvent) {
    }

    @Override
    public int getPreferredWidth(Graphics graphics, Font font) {
        return CompletionUtilities.getPreferredWidth(text, null, graphics, font);
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor,
            Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(icon, text, null, g, defaultFont,
                (selected ? Color.white : fieldColor), width, height, selected);
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return null;
    }

    @Override
    public CompletionTask createToolTipTask() {
        return null;
    }

    @Override
    public boolean instantSubstitution(JTextComponent jTextComponent) {
        defaultAction(jTextComponent);
        return true;
    }

    @Override
    public int getSortPriority() {
        return 0;
    }

    @Override
    public CharSequence getSortText() {
        return text;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return text;
    }

    String getText() {
        return text;
    }

    void doSubstitute(final JTextComponent component, final String toAdd, final int backOffset) {

        final StyledDocument doc = (StyledDocument) component.getDocument();
        final AbstractDocument abDoc = (AbstractDocument) component.getDocument();

        class AtomicChange implements Runnable {

            public void run() {

                int caretOffset = component.getCaretPosition();
                String value = getText();

                if (toAdd != null) {
                    value += toAdd;
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

    public void defaultAction(JTextComponent jTextComponent) {
        Completion.get().hideAll();
        doSubstitute(jTextComponent, null, 0);
    }
}
