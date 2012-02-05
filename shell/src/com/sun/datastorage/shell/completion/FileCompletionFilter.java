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

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author 
 */
public class FileCompletionFilter implements FilenameFilter {

    private String filterName;
    public static final int DIRS_AND_FILES = 0;
    public static final int FILES_ONLY = 1;
    public static final int DIRS_ONLY = 2;
    int fileType;

    public FileCompletionFilter(String filterName, int fileType) {
        this.filterName = filterName;
        this.fileType = fileType;
    }

    @Override
    public boolean accept(File dir, String fileName) {


        // check to see if the file starts with the filter, if the filter is null then let them all through.
        if (!filterName.endsWith("/") && !fileName.startsWith(filterName)) {
            return false;
        }



        File file = new File(fileName);
        switch (fileType) {
            case FILES_ONLY:
                if (file.isFile()) {
                    return true;
                }
                break;
            case DIRS_ONLY:
                if (file.isDirectory()) {
                    return true;
                }
                break;
            default:
                return true;
        }

        //return false;
        //TODO: fix this, isDirectory does not work. Use FileFilter?
        return true;
    }
}
