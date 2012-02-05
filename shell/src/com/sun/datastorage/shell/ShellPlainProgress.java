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

import java.beans.PropertyChangeEvent;
import org.openide.util.Cancellable;

public class ShellPlainProgress extends ShellProgress {

    /**
     */
    public ShellPlainProgress(String displayMessage, Cancellable cancel) {
    }

    public ShellPlainProgress() {
    }

    public void propertyChange(PropertyChangeEvent evt) {
    }

    public void finish() {
    }

    public void start(int workunits) {
    }

    // Factory class
    public static final ShellPlainProgress createCmdLine(String msg, Cancellable doc) {
        return new ShellPlainProgress(msg, doc);
    }

    public ShellPlainProgress create(String msg, Cancellable doc) {
        return createCmdLine(msg, doc);
    }

    @Override
    public void setEnable(boolean enable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void progressMark() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSubtaskProgress(float subtaskProgress) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSubtaskPercentOfMax(float subtaskPercentOfMax) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setNote(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMax(int maxWorkUnits) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setProgress(int workUnits) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
