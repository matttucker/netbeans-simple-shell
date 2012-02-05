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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.InputMap;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.openide.util.WeakListeners;

public class ShellEditorPane extends JEditorPane {

    public static final String PROP_SHELL_TITLE = "title";
    private PropertyChangeSupport propertySupport;
    public String title = null;
    //Image background = Toolkit.getDefaultToolkit().createImage("c:/images/sunshineandredcloud.jpg");
    //Image background = Toolkit.getDefaultToolkit().createImage("sunlogo2.gif");
    Image background = null;
    public static final String SHELL_ERROR_LAYER = "com.sun.datastorage.shell.highlighting.BlockHighlighting/SHELL_ERROR_LAYER";

    public void init() {
        propertySupport = new PropertyChangeSupport(this);
        setCaretPosition(0);

        ShellDocument doc = (ShellDocument) getDocument();

        // Here we get a list of commanders registered. The Document gets a copy of the list so that each
        // editor pane instance can have its own commander states. TODO is there a better way of doing this.
        Lookup.Result<Commander> commanders = MimeLookup.getLookup(MimePath.get("text/x-shell")).lookupResult(Commander.class);

        for (Commander cmder : commanders.allInstances()) {
            try {
                doc.addCommander(cmder.copy());
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        //doc.setCommanderList(commanderList);
        doc.setEditorPane(this);



        doc.initCommanders();

        // should be after commanders get initialized, so they can mod the default
        // path.
        doc.setPathPreference();

        doc.insertPromptLater();

        putClientProperty("HighlightsLayerExcludes",
                "^org\\.netbeans\\.modules\\.editor\\.lib2\\.highlighting\\.CaretRowHighlighting$" + "|"
                + ".+brace.+");

        installKeyBindings();

        doc.runStartupCmd();
    }

    /**
     * Install key bindings to the shell.
     */
    void installKeyBindings() {
        KeyStroke key;
        InputMap inputMap = getInputMap();

        //the rest of the key maps are in the editor kit.
        //up arrow - insertCommand history
        key = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        // inputMap.put(key, historyBackAction);
        inputMap.put(key, "shell-history-backward");

        //dwn arrow - insertCommand history
        key = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        inputMap.put(key, "shell-history-forward");


        Object action = inputMap.get(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));

    }

    public void setTitle(String title) {
        propertySupport.firePropertyChange(PROP_SHELL_TITLE, this.title, title);
        this.title = title;
    }

    public PropertyChangeListener addWeakShellPropertyChangeListener(PropertyChangeListener listener) {
        PropertyChangeListener weakListener = WeakListeners.propertyChange(listener, this);
        propertySupport.addPropertyChangeListener(weakListener);
        return weakListener;
    }

    public void removeShellPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    @Override
    public final void paintComponent(Graphics g) {
        if (background != null) {
            g.drawImage(background, 0, 0, (int) getSize().getWidth(), (int) getSize().getHeight(), this);
        }
        //g.drawImage(background, 0,0, null);
        super.paintComponent(g);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            System.out.println("finalizing shell editor pane.");
        } finally {
            super.finalize();
        }

    }
}
