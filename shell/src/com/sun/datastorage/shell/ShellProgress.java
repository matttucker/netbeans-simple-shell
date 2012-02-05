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

import org.openide.util.Cancellable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 *
 */
public abstract class ShellProgress implements PropertyChangeListener {

    public static final String PROP_PROGRESS = "PROGRESS";
    public static final String PROP_SUBTASK_PROGRESS = "SUBTASK_PROGRESS";
    public static final String PROP_PROGRESS_ENABLE = "PROGRESS_ENABLE";
    public static final String PROP_PROGRESS_MARK = "PROGRESS_MARK";
    public static final String PROP_PROGRESS_NOTE = "PROGRESS_NOTE";
    public static final String PROP_PROGRESS_MAX = "PROGRESS_MAX";
    public static final String PROP_PROGRESS_SUBTASK_PERCENT_OF_MAX = "PROGRESS_SUBTASK_PERCENT_OF_MAX";

    public abstract void propertyChange(PropertyChangeEvent evt);

    public abstract void finish();

    public abstract void setNote(String string);

    public abstract void start(int workunits);

    public abstract ShellProgress create(String msg, Cancellable doc);

    public abstract void setEnable(boolean enable);

    public abstract void progressMark();

    public abstract void setSubtaskProgress(float subtaskProgress);

    public abstract void setSubtaskPercentOfMax(float subtaskPercentOfMax);
    public abstract void setMax(int maxWorkUnits);
    public abstract void setProgress(int workUnits);
}
