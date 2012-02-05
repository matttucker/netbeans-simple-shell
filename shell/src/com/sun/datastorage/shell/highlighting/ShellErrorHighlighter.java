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
import org.netbeans.spi.editor.highlighting.HighlightsSequence;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;


public class ShellErrorHighlighter implements ShellHighlighter, LookupListener {

//    private static final AttributeSet ERROR_ATTRIBUTE_SET = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.RED,
//            StyleConstants.Italic, true, StyleConstants.Bold, false);
    Document doc;

    public ShellErrorHighlighter(final Document doc) {
        bag = new OffsetsBag(doc, false);
        this.doc = doc;
        Result fontsColors = MimeLookup.getLookup("text/x-shell").lookup(new Lookup.Template(FontColorSettings.class));
        if (fontsColors != null) {
            fontsColors.addLookupListener(this);
        }
        System.out.println("new error highlighter.");
    }
    private final OffsetsBag bag;

    @Override
    public OffsetsBag getHighlightsBag() {
        return bag;
    }

    @Override
    public void addHighlight(int startOffset, int endOffset) {
        int length = doc.getLength();

        //TODO: these seems like a bug when adding a highlight to the bag, the outside mark is not 
        // set to the length but to a really large number.
        if (endOffset == length) {
            endOffset = length - 1;
        }

        if (endOffset > length) {
            System.out.println("Warning: error highlighter end >= length, endOffset = " + endOffset + ", length = " + doc.getLength());
            endOffset = doc.getLength() - 1;
        }
        FontColorSettings fcs = MimeLookup.getLookup("text/x-shell").lookup(FontColorSettings.class);
        AttributeSet fontColors = fcs.getTokenFontColors("shellerror");

        bag.addHighlight(startOffset, endOffset, fontColors);

    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Lookup.Result result = ((Lookup.Result) ev.getSource());

        System.out.println("change is a happening.");
        HighlightsSequence hq = bag.getHighlights(0, doc.getLength());

        FontColorSettings fcs = MimeLookup.getLookup("text/x-shell").lookup(FontColorSettings.class);
        AttributeSet fontColors = fcs.getTokenFontColors("shellerror");
        OffsetsBag newbag = new OffsetsBag(doc, false);

        while (hq.moveNext()) {
            int start = hq.getStartOffset();
            int end = hq.getEndOffset();
            newbag.addHighlight(start, end, fontColors);
        }

        bag.setHighlights(newbag);
    }
}
