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
 * Class to find files that start with an input string.
 *
 * @author 
 */
public class FileCompletionExtensionFilter implements FilenameFilter {

    private String filterName;
    private String[] validExts;

    public FileCompletionExtensionFilter(String filterName, String[] validExts) {
        this.filterName = filterName;
        this.validExts = validExts;
    }

    public boolean accept(File dir, String fileName) {


        // check to see if the file starts with the filter, if the filter is a directory then let them all through.
        if (!filterName.endsWith("/") && !fileName.startsWith(filterName)) {
            return false;
        }

        File file = new File(dir, fileName);

        //option for this?
        if (file.isDirectory()) {
            return true;
        }


        for (String filterExt : validExts) {
            if (fileName.endsWith("." + filterExt)) {
                return true;
            }
        }


        return false;
    }
}

