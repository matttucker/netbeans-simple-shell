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

import com.sun.datastorage.shell.highlighting.ShellHighlighter;
import java.io.IOException;
import java.io.Writer;


public class ShellWriter extends Writer {

    private ShellDocument doc;
    private ShellHighlighter layer;

    /**
     * Creates a new instance of ShellWriter
     */
    public ShellWriter(ShellDocument doc) {
        this.doc = doc;
        this.layer = null;
    }

    public ShellWriter(ShellDocument doc, ShellHighlighter layer) {
        this.doc = doc;
        this.layer = layer;
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {

        if (layer != null) {
            doc.insertTextLater(new String(cbuf, off, len), layer);
        } else {
            doc.insertTextLater(new String(cbuf, off, len));
        }

    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}
