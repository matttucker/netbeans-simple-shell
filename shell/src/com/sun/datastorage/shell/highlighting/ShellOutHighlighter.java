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
package com.sun.datastorage.shell.highlighting;

import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;


public class ShellOutHighlighter implements ShellHighlighter {

    Document doc;
    private final OffsetsBag bag;

    public ShellOutHighlighter(Document doc) {
        bag = new OffsetsBag(doc);
        this.doc = doc;

    }

    @Override
    public OffsetsBag getHighlightsBag() {
        return bag;
    }

    @Override
    public void addHighlight(int startOffset, int endOffset) {
        int length = doc.getLength();
//        if (endOffset >= length) {
//            System.out.println("Warning: error highlighter end >= length, endOffset = " + endOffset +", length = " + doc.getLength());
//            endOffset = doc.getLength() - 1;
//        }
        FontColorSettings fcs = MimeLookup.getLookup("text/x-java").lookup(FontColorSettings.class);
        AttributeSet fontColors = fcs.getTokenFontColors("default");

    }
}
