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
package com.sun.datastorage.shell.server;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * A table model for modeling the server connections.
 * 
 * @author Matt Tucker
 */
public class ConnectionModel extends AbstractTableModel {

    public Vector<Connection> connections;
  
    static final String[] columnNames = {"Client Connection"};

    public ConnectionModel() {
        super();
        
        connections = new Vector<Connection>(2); 

    }
    
    

    public int getRowCount() {
        return connections.size();
    }

    public int getColumnCount() {
        return 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return connections.get(rowIndex).clientId;
        }  else {
            return null;
        }
    }

    public void setConnections(Vector<Connection> connections) {
        this.connections = connections;
        fireTableChanged(null);
    }
    
    public void update() {
        fireTableChanged(null);
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column] == null ? "No Name" : columnNames[column];
    }
    

    
    
}
