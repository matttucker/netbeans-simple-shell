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
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;

public class ShellEditorProgress extends ShellProgress {

    ProgressHandle handle;
    boolean started = false;
    boolean enable = false;
    int progress = 0;    // use the progressMark to handle subprogress in independent jobs.
    int progressMark = 0;
    float subtaskProgress;
    float subtaskPercentOfMax;
    int max;

    /**
     */
    public ShellEditorProgress(String displayMessage, Cancellable cancel) {
        handle = ProgressHandleFactory.createHandle(displayMessage, cancel);
    }

    public ShellEditorProgress() {
    }

    public void setSubtaskProgress(float subtaskProgress) {
        if (enable) {

            //subtaskProgress varies between 0.0-1.0
            //subtaskPercentOfMax varies between 0.0-1.0
            progress = (int) (subtaskProgress * subtaskPercentOfMax * max + progressMark);

            handle.progress(progress);
        }
    }

    public void setSubtaskPercentOfMax(float subtaskPercentOfMax) {
        this.subtaskPercentOfMax = subtaskPercentOfMax;
        progressMark();
    }

    public void progressMark() {
        progressMark = progress;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void setNote(String note) {
        handle.progress(note);
    }

    public void finish() {
        if (started) {
            handle.finish();
        }
    }

    public void start(int workunits) {
        if (!started) {
            handle.start((Integer) workunits);
        }
        max = workunits;
        started = true;
        progress = 0;
        enable = true;
    }
    // Factory class

    public static final ShellEditorProgress createGUI(String msg, Cancellable doc) {
        return new ShellEditorProgress(msg, doc);
    }

    public ShellEditorProgress create(String msg, Cancellable doc) {
        return createGUI(msg, doc);
    }

    @Override
    public void setMax(int maxWorkUnits) {
        if (!started) {
            handle.start(max);
        }
        max = maxWorkUnits;
        started = true;
    }

    @Override
    public void setProgress(int workUnits) {
        if (enable) {
            progress = workUnits + progressMark;

            handle.progress(progress);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (PROP_PROGRESS_MARK.equals(propertyName)) {
            progressMark();

        } else if (PROP_PROGRESS_ENABLE.equals(propertyName)) {

            setEnable((Boolean) evt.getNewValue());

        } else if (PROP_SUBTASK_PROGRESS.equals(propertyName)) {
            setSubtaskProgress((Float) evt.getNewValue());

        } else if (PROP_PROGRESS.equals(propertyName)) {

            setProgress((Integer) evt.getNewValue());
        } else if (PROP_PROGRESS_NOTE.equals(propertyName)) {
            setNote((String) evt.getNewValue());

        } else if (PROP_PROGRESS_MAX.equals(propertyName)) {

            setMax((Integer) evt.getNewValue());

        } else if (PROP_PROGRESS_SUBTASK_PERCENT_OF_MAX.equals(propertyName)) {
            setSubtaskPercentOfMax((Float) evt.getNewValue());

        }
    }
}
