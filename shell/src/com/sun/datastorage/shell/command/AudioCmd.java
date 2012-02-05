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
package com.sun.datastorage.shell.command;

import com.sun.datastorage.shell.CmdLineParser;
import com.sun.datastorage.shell.Command;
import com.sun.datastorage.shell.Commander;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

class AudioCmd extends Command {

    public AudioCmd(Commander cmdr) {
        super(cmdr, "audio", "com.sun.datastorage.shell.command.audio", "nbdocs://com.sun.datastorage.shell/com/sun/datastorage/shell/docs/shell-command-audio.html");
    }

    @Override
    public void printUsage(String[] args) {
        cmdr.getDoc().getOutWriter().println("Usage: audio <soundfilename>");
    }

    @Override
    public void execute(String[] args) throws Exception {
        super.execute(args);
        if (otherArgs.length == 1) {
            String soundFileName = otherArgs[0];
            AudioInputStream stream = null;
            try {
                File soundFile = cmdr.getDoc().getPathManager().getFile(soundFileName);
                stream = AudioSystem.getAudioInputStream(soundFile);
                AudioFormat format = stream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(stream);
                clip.start();
            } catch (FileNotFoundException ex) {
                cmdr.getDoc().getErrorWriter().println(ex.getMessage());
            } catch (LineUnavailableException ex) {
                cmdr.getDoc().getErrorWriter().println(ex.getMessage());
            } catch (UnsupportedAudioFileException ex) {
                cmdr.getDoc().getErrorWriter().println(ex.getMessage());
            } catch (IOException ex) {
                cmdr.getDoc().getErrorWriter().println(ex.getMessage());
            } finally {
                try {
                    stream.close();
                } catch (IOException ex) {
                    cmdr.getDoc().getErrorWriter().println(ex.getMessage());
                }
            }
        }
    }
}
