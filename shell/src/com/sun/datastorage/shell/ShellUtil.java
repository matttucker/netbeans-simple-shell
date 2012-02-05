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

import java.io.PrintWriter;


public class ShellUtil {

    public static void printHexData(PrintWriter out, int address, byte[] data) {

        int i0 = 0;
        byte[] rowdata = new byte[16];
        for (int i = 0; i < data.length; i++) {

            if (i % 16 == 0) {
                if (i != 0) {
                    

                    out.print(": ");

                    // print ASCII representation.
                    System.arraycopy(data, i0, rowdata, 0, 16);
                    String str = new String(rowdata);
                    str = str.replaceAll("[^\\w\\.@-\\\\()=+\\[\\]]", ".");
                    out.println(str);
                    out.flush();
                    i0 +=16;
                }
                out.printf("%08xh: ", address + i);
            }

            out.printf("%02x ", data[i]);
        }

        // blanks on last row
        int nBlanks = 16 - (data.length-i0);
        for(int i = 0; i < nBlanks; i++) {
            out.printf("-- ");
        }
        out.print(": ");

        // print ASCII representation.
        rowdata = new byte[data.length - i0];
        System.arraycopy(data, i0, rowdata, 0, data.length - i0);
        String str = new String(rowdata);
        str = str.replaceAll("[^\\w\\.@-\\\\()=+\\[\\]]", ".");
        out.println(str);
        out.flush();

    }
}
