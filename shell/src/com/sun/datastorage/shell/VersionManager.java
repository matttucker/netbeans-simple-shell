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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.openide.modules.InstalledFileLocator;

public class VersionManager {

    public static final String VERSION_FILE = "version.properties";

    public static String getVersionProperty(String key) throws FileNotFoundException, IOException {
        InstalledFileLocator fileLocator = InstalledFileLocator.getDefault();
        File versionFile = fileLocator.locate(VERSION_FILE, null, false);
        if (versionFile != null) {
            FileInputStream prpFis = new FileInputStream(versionFile);
            Properties prop = new Properties();
            prop.load(prpFis);
            return prop.getProperty(key);
        } else {
            return null;
        }
    }
}
