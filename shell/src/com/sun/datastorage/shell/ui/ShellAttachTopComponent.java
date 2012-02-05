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
import com.sun.datastorage.shell.ShellEditorDocument;
import com.sun.datastorage.shell.ShellProvider;
import java.util.Collection;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;


public abstract class ShellAttachTopComponent extends TopComponent implements
        ShellProvider, LookupListener {

    ShellDocument shellDoc;
    Lookup.Result result = null;

    public ShellAttachTopComponent() {
        associateLookup(Lookups.singleton(this));
    }

    @Override
    public ShellDocument getShellDocument() {
        return shellDoc;
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        Lookup.Result r = (Lookup.Result) lookupEvent.getSource();
        Collection c = r.allInstances();

        if (!c.isEmpty()) {
            ShellProvider shellProvider = (ShellProvider) c.iterator().next();
            attachToShell(shellProvider.getShellDocument());
        } else {

            if (shellDoc != null && shellDoc.getCommanderList() == null) {
                try {
                    // no shells exist anymore, so cleanup
                    shellDoc = null;

                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }

            }
        }
    }

   

    @Override
    @SuppressWarnings(value = "unchecked")
    public void componentOpened() {

        Lookup.Template tpl = new Lookup.Template(ShellEditorDocument.class);
        result = Utilities.actionsGlobalContext().lookup(tpl);
        result.addLookupListener(this);

        //ShellTopComponent comp = ShellTopComponent.findInstance(-1);
        ShellDocument doc = Utilities.actionsGlobalContext().lookup(ShellDocument.class);
        attachToShell(doc);
    }

    @Override
    public void componentClosed() {
        attachToShell(null);

        //remove lookup listener
        result.removeLookupListener(this);
        result = null;

    }

    public void attachToShell(ShellDocument newDoc) {
        shellDoc = newDoc;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

 
}
