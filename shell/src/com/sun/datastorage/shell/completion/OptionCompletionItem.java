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

import com.sun.datastorage.shell.Command;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;


public class OptionCompletionItem extends CommandCompletionItem {

    public OptionCompletionItem(String text, Command cmd, int dotOffset, int carretOffset) {
        super(text, cmd, dotOffset, carretOffset);

        icon = new ImageIcon(ImageUtilities.loadImage("com/sun/datastorage/shell/completion/resources/method_16.png"));
    }
}
