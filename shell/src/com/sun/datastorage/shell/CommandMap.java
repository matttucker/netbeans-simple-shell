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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;


public class CommandMap extends HashMap<String, Command> {

    public void put(Command cmd) {
        this.put(cmd.name, cmd);
    }
    
   public  Vector<Command> getStartsWith(String target) {
        Vector<Command> cmdsFound = new Vector<Command>();
        Iterator<Entry<String, Command>> iterator = this.entrySet().iterator();
        Command cmd;
        Entry<String, Command> e;
        String key;
        while (iterator.hasNext()) {
            e = iterator.next();
            cmd = e.getValue();
            //key = e.getKey();
            if (cmd.name.startsWith(target))
                cmdsFound.add(cmd);
        }
       
        return cmdsFound;
    }
   /**
    *  Find a single command that starts with the key. If more than one command starts with the key, then return null.
    * 
    * @param key
    * @return
    */
  public Command getSingleMatch(String key) {
      Vector<Command> cmds = getStartsWith(key);
      if (cmds == null || cmds.size() > 1) {
          return null;
      } else {
          return cmds.get(0);
      }
  }
}
