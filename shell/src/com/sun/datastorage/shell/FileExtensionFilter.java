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

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Class to find files that start with an input string.
 *
 */
public class FileExtensionFilter extends FileFilter {

    private String[] validExts;
    String description;

    public FileExtensionFilter(String descrip, String[] exts) {
        super();
        this.validExts = exts;
        description = descrip;
    }

    public boolean accept(File file) {

        if (file.isDirectory()) {
            return true;
        }


        for (String filterExt : validExts) {
            if (file.toString().endsWith("." + filterExt)) {
                return true;
            }
        }


        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
