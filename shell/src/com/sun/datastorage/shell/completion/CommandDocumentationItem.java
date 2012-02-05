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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.Action;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.openide.util.Exceptions;

/**
 *
 * @author
 */
public class CommandDocumentationItem implements CompletionDocumentation {

    private CommandCompletionItem item;

    public CommandDocumentationItem(CommandCompletionItem item) {
        this.item = item;
    }

    @Override
    public String getText() {
        URL url = getURL();

        if (url == null) {
            return "";
        }
        try {
            return urlToString(url);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }

    @Override
    public URL getURL() {
        URL url = item.cmd.getHelpUrl();
        if (url == null) {
            return null;
        }
        try {
            url.openStream();
            return url;
        } catch (IOException ex) {
            return null;
        }



    }

    public CompletionDocumentation resolveLink(String arg0) {
        return null;
    }

    public Action getGotoSourceAction() {
        return null;
    }

    static String urlToString(URL url) throws FileNotFoundException, IOException {
        String data = "";

        if (url != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                if (inputLine.contains("<?xml") == false) {
                    data = data + inputLine;
                }
            }

            reader.close();
        }

        return data;
    }
}
