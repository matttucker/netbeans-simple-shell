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

import java.util.List;
import java.util.ListIterator;
import java.util.Vector;


public class CommandHistory {
    
    List<String> history;
    List<String> uniqueHistory;
    ListIterator<String> iterator;
    
    private String lastCmd;
    
    
    
    
    enum Direction { BACKWARD, FORWARD };
    Direction dir = Direction.BACKWARD;
    Direction prevDir = dir;
    
    /** Creates a new instance of CommandHistory */
    public CommandHistory() {
        history = new Vector<String>(100);
        uniqueHistory = new Vector<String>(100);
        lastCmd = "";
        iterator = history.listIterator(history.size());
    }
    
    public void add(String cmd) {
        
        //only add a command to the history if it is different from the last
        //command entered.
        if (lastCmd.equals(cmd) == false) {
            history.add(cmd);
            
            if (uniqueHistory.contains(cmd) == false) {
                uniqueHistory.add(cmd);
            }
            
            lastCmd = cmd;
            
        }
        
        iterator = history.listIterator(history.size());
        dir = Direction.BACKWARD;
    }
    
    
    public String next() {
        if (dir == Direction.BACKWARD) {
            dir = Direction.FORWARD;
            
            //read in an extra command when changing direction, so
            // that the next iteration gives a new command.
            if (iterator.hasNext()) {
                String junk = iterator.next();
            }
        }
        
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
        
    }
    
    public boolean hasNext() {
        return iterator.hasNext();
    }
    
    public String previous() {
        if (dir == Direction.FORWARD) {
            dir = Direction.BACKWARD;
            
            //read in an extra command when changing direction, so
            // that the next iteration gives a new command.
            if (iterator.hasPrevious()) {
                String junk = iterator.previous();
            }
        }
      
        if (iterator.hasPrevious()) {
            return iterator.previous();
        } else {
            return null;
        }
    }
    
    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }
    
    public List<String> getUniqueHistory() {
        return uniqueHistory;
    }
}
