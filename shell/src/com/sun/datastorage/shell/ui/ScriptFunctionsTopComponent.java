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
package com.sun.datastorage.shell.ui;

import com.sun.datastorage.shell.ShellDocument;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.ObjectStreamException;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
final class ScriptFunctionsTopComponent extends ShellAttachTopComponent {

    private static ScriptFunctionsTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "com/sun/datastorage/shell/resources/shell_16x16.png";
    private static final String PREFERRED_ID = "ScriptFunctionsTopComponent";
    static final String PROP_FUNCTION_LIST = "PROP_CONSOLE_VELOCITY";
    static final String DEFAULT_FUNCTION_LIST = "load():unload():halt()";
    Vector<String> functionList;

    private ScriptFunctionsTopComponent() {
        super();
        initComponents();
        setName(NbBundle.getMessage(ScriptFunctionsTopComponent.class, "CTL_ScriptFunctionsTopComponent"));
        setToolTipText(NbBundle.getMessage(ScriptFunctionsTopComponent.class, "HINT_ScriptFunctionsTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
        FlowLayout layout = new FlowLayout();
        this.setLayout(layout);
        functionList = new Vector<String>(10);

    }

    private void setFunctionList(String functionListString) {
        String[] functions = functionListString.split(":");

        functionList.removeAllElements();
//        for (String aFunction : functions) {
//            functionList.add(aFunction);
//        }
        functionList.addAll(Arrays.asList(functions));

    }

    private String getFunctionListStr() {
        String functionListStr = new String();
        for (String aFunction : functionList) {
            functionListStr += aFunction + ":";
        }
        return functionListStr;
    }

    void createScriptFunctionComponents() {


//        if (shellDoc != null) {
//
//            SimpleBindings bindings = (SimpleBindings) shellDoc.getScriptEngine().getContext().getBindings(ScriptContext.ENGINE_SCOPE);
//
//            final Set<Map.Entry<String, Object>> items = bindings.entrySet();
//            
//            
//            
//            SwingUtilities.invokeLater(new Runnable() {
//
//                public void run() {
//                    Object obj;
//                    String name;
//                    String className;
//                    tc.removeAll();
//                    for (Map.Entry<String, Object> item : items) {
//                        name = item.getKey();
//                        obj = item.getValue();
//                        className = obj.getClass().toString();
//                        if (className.equals("class sun.org.mozilla.javascript.internal.InterpretedFunction")) {
//                            tc.add(new JButton(name));
//                        }
//                    }
//                    tc.repaint();
//                    tc.validate();
//                }
//            });
//        }

//        Object obj;
//        String name;
//        String className;
        this.removeAll();
        for (String function : functionList) {
            final String functionStr = function;
            if (!functionStr.isEmpty()) {
                JButton jButton = new JButton(functionStr);
                jButton.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        if (shellDoc != null) {
                            shellDoc.command(functionStr, false, true);
                        }
                    }
                });
                jButton.addMouseListener(new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseReleased(MouseEvent event) {
                        if (event.isPopupTrigger()) {
                            functionButtonContextMenu(event);

                        }
                    }
                });

                this.add(jButton);
            }
        }
        this.repaint();
        this.validate();

    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void componentOpened() {
        super.componentOpened();
        setFunctionList(NbPreferences.forModule(ScriptFunctionsTopComponent.class).get(PROP_FUNCTION_LIST, DEFAULT_FUNCTION_LIST));
        createScriptFunctionComponents();
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        NbPreferences.forModule(ScriptFunctionsTopComponent.class).put(PROP_FUNCTION_LIST, getFunctionListStr());

    }

    @Override
    public Object writeReplace() throws ObjectStreamException {
        NbPreferences.forModule(ScriptFunctionsTopComponent.class).put(PROP_FUNCTION_LIST, getFunctionListStr());
        return super.writeReplace();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked

    // if (evt.isPopupTrigger()) {
    if (evt.isPopupTrigger()) {
        functionPopupMenu(evt);
    }

}//GEN-LAST:event_formMouseClicked

private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
    if (evt.isPopupTrigger()) {
        functionPopupMenu(evt);
    }
}//GEN-LAST:event_formMousePressed

private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
// TODO add your handling code here: 
    if (evt.isPopupTrigger()) {
        functionPopupMenu(evt);
    }
}//GEN-LAST:event_formMouseReleased

    void functionPopupMenu(final MouseEvent evt) {
        JMenuItem menuItem;

        final JPopupMenu pmenu = new JPopupMenu();

        menuItem = new JMenuItem("New...");
        AbstractAction newFunctionAction = new AbstractAction("New...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Component invoker = pmenu.getInvoker();
                JComponent invokerAsJComponent = (JComponent) invoker;
                Container topLevel = invokerAsJComponent.getTopLevelAncestor();
                //TODO: this dialog could be way better. Poll the shell for current 
                // functions and give the user the option to add them or subtract
                // them from the function list.
                NewFunctionDialog dialog = new NewFunctionDialog((Frame) topLevel, true);

                dialog.setVisible(true);
                dialog.dispose();

                if (dialog.getReturnStatus() == NewFunctionDialog.RET_OK) {
                    String functionName = dialog.getFunctionName();
                    functionList.add(functionName);
                }
                createScriptFunctionComponents();
            }
        };
        menuItem = new JMenuItem(newFunctionAction);
        pmenu.add(menuItem);


        AbstractAction removeAllAction = new AbstractAction("Delete All") {

            @Override
            public void actionPerformed(ActionEvent e) {
                functionList.removeAllElements();
                createScriptFunctionComponents();
            }
        };
        menuItem = new JMenuItem(removeAllAction);
        pmenu.add(menuItem);

        pmenu.show(evt.getComponent(), evt.getX(), evt.getY());

    }

    void functionButtonContextMenu(final MouseEvent evt) {
        JMenuItem menuItem;

        final JPopupMenu pmenu = new JPopupMenu();
        AbstractAction editFunctionAction = new AbstractAction("Edit...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Component invoker = pmenu.getInvoker();
                JButton button = (JButton) invoker;
                Container topLevel = button.getTopLevelAncestor();

                EditFunctionDialog dialog = new EditFunctionDialog((Frame) topLevel, true);
                String oldName = button.getText();
                dialog.setFunctionName(oldName);
                dialog.setVisible(true);
                dialog.dispose();

                if (dialog.getReturnStatus() == EditFunctionDialog.RET_OK) {
                    String functionName = dialog.getFunctionName();
                    button.setText(functionName);

                    functionList.remove(oldName);
                    functionList.add(functionName);
                }
                createScriptFunctionComponents();
            }
        };
        menuItem = new JMenuItem(editFunctionAction);
        pmenu.add(menuItem);


        menuItem = new JMenuItem("Delete");
        AbstractAction deleteFunctionAction = new AbstractAction("Delete") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Component invoker = pmenu.getInvoker();
                JButton button = (JButton) invoker;
                int idx = functionList.indexOf(button.getText());
                functionList.remove(idx);
                createScriptFunctionComponents();
            }
        };
        menuItem = new JMenuItem(deleteFunctionAction);
        pmenu.add(menuItem);


        pmenu.show(evt.getComponent(), evt.getX(), evt.getY());

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized ScriptFunctionsTopComponent getDefault() {
        if (instance == null) {
            instance = new ScriptFunctionsTopComponent();
        }

        return instance;
    }

    /**
     * Obtain the ScriptFunctionsTopComponentTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ScriptFunctionsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(ScriptFunctionsTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");





            return getDefault();
        }
        if (win instanceof ScriptFunctionsTopComponent) {
            return (ScriptFunctionsTopComponent) win;
        }



        Logger.getLogger(ScriptFunctionsTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");





        return getDefault();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public void attachToShell(ShellDocument newDoc) {
        super.attachToShell(newDoc);
        createScriptFunctionComponents();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (shellDoc == null) {
                    setDisplayName("Shell Buttons:(no connection)");
                } else {
                    setDisplayName("Shell Buttons" + ":" + shellDoc.getShellId());
                }

            }
        });
    }
}
