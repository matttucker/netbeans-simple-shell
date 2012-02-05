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

import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;

import javax.swing.text.Document;

/**
 *
 */
public class ShellHighlightsLayerFactory implements HighlightsLayerFactory {

    public static ShellErrorHighlighter getShellErrorHighlighter(Document doc) {
        ShellErrorHighlighter highlighter = (ShellErrorHighlighter) doc.getProperty(ShellErrorHighlighter.class);
        if (highlighter == null) {
            doc.putProperty(ShellErrorHighlighter.class, highlighter = new ShellErrorHighlighter(doc));
        }
        return highlighter;
    }

    public static ShellOutHighlighter getShellOutHighlighter(Document doc) {
        ShellOutHighlighter highlighter = (ShellOutHighlighter) doc.getProperty(ShellOutHighlighter.class);
        if (highlighter == null) {
            doc.putProperty(ShellOutHighlighter.class, highlighter = new ShellOutHighlighter(doc));
        }
        return highlighter;
    }

    @Override
    public HighlightsLayer[] createLayers(Context context) {
        return new HighlightsLayer[]{
                    HighlightsLayer.create(
                    ShellErrorHighlighter.class.getName(),
                    ZOrder.TOP_RACK,
                    true,
                    getShellErrorHighlighter(context.getDocument()).getHighlightsBag()),
                    HighlightsLayer.create(
                    ShellOutHighlighter.class.getName(),
                    ZOrder.TOP_RACK,
                    true,
                    getShellOutHighlighter(context.getDocument()).getHighlightsBag())
                };
    }
}



