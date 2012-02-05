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
package com.sun.datastorage.shell.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import javax.swing.JComponent;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public class ShellOptionsPanelController extends OptionsPanelController {

    private ShellPanelsGroup panelsGroup;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanelsGroup().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        this.
        getPanelsGroup().store();
        changed = false;
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanelsGroup().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanelsGroup().getComponent();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private ShellPanelsGroup getPanelsGroup() {
        if (panelsGroup == null) {
            panelsGroup = new ShellPanelsGroup(this);
        }
        return panelsGroup;
    }

    public void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    private class ShellPanelsGroup {

        private javax.swing.JTabbedPane jTabbedPane;
        private Vector<ShellOptionsPanel> shellPanels;

        ShellPanelsGroup(ShellOptionsPanelController controller) {
            jTabbedPane = new javax.swing.JTabbedPane();
            shellPanels = new Vector<ShellOptionsPanel>();

            // Here we get a list of  registered shell option panels.
            Lookup.Result<ShellOptionsPanel> panels = MimeLookup.getLookup(MimePath.get("text/x-shell")).lookupResult(ShellOptionsPanel.class);


            for (ShellOptionsPanel panel : panels.allInstances()) {
                panel.setController(controller);

                if (panel instanceof ShellGeneralOptionsPanel) {
                    shellPanels.insertElementAt(panel, 0);
                    jTabbedPane.insertTab(panel.getName(), null, panel, "", 0);
                } else {
                    shellPanels.add(panel);
                    jTabbedPane.add(panel.getName(), panel);
                }
            }

            jTabbedPane.setSelectedIndex(0);

        }

        void load() {
            if (shellPanels != null) {
                for (ShellOptionsPanel panel : shellPanels) {
                    panel.load();
                }
            }
        }

        void store() {
            if (shellPanels != null) {
                for (ShellOptionsPanel panel : shellPanels) {
                    if (panel.valid()) {
                        panel.store();
                    }
                }
            }
        }

        boolean valid() {
            // TODO check whether form is consistent and complete
            return true;
        }

        JComponent getComponent() {
            return jTabbedPane;
        }
    }
}
