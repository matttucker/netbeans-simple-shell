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

import java.util.Vector;

public class JobManager {

    Vector<ShellProcess> jobList = new Vector<ShellProcess>(5);
    int lastJob = 0;

    public JobManager() {
    }

    public boolean add(ShellProcess proc) {
        lastJob++;
        proc.job = lastJob;
        return jobList.add(proc);
    }

    public boolean remove(ShellProcess proc) {

        if (proc.job == lastJob) {
            lastJob--;
        }
        return jobList.remove(proc);
    }

    public ShellProcess get(int i) {
        return jobList.get(i);
    }

    public int size() {
        return jobList.size();
    }
}
