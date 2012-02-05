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

import com.sun.datastorage.shell.ShellProcess;
import com.sun.datastorage.shell.rmi.RemoteShell;
import com.sun.datastorage.shell.ui.ShellTopComponent;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author Matt Tucker
 */
public class Server implements RemoteShell {

    static final String name = "ShellServer";
    ConnectionModel connectionModel;
    private Preferences pref;
    public static final String RMI_SERVER_PORT_PROP = "RMI_SERVER_PORT_PROP";
    public static final int RMI_SERVER_PORT_DEFAULT = 1099;
    public static final String RMI_SERVER_CODEBASE_PROP = "RMI_SERVER_CODEBASE_PROP";
    public static final String RMI_SERVER_CODEBASE_DEFAULT = "file:///C:/Program%20Files/Java/jre6/axbridge/lib/com-sun-datastorage-shell-rmi.jar";
    public String codeBase;
    private boolean isEnabled = false;

    Server(ConnectionModel connectionModel) {
        this.connectionModel = connectionModel;


        pref = Preferences.userNodeForPackage(Server.class);

        setupServer();

    }

    public boolean isEnabled() {
        return isEnabled;
    }

    private void setupServer() {    
        codeBase = pref.get(Server.RMI_SERVER_CODEBASE_PROP, RMI_SERVER_CODEBASE_DEFAULT);
        System.setProperty("java.rmi.server.codebase", codeBase);
        System.out.println("java.rmi.server.codebase =" + System.getProperty("java.rmi.server.codebase"));

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        SecurityManager securityManager = System.getSecurityManager();
        try {
            RemoteShell remoteShell = (RemoteShell) this;
            RemoteShell stub = (RemoteShell) UnicastRemoteObject.exportObject(remoteShell, 0);
            int port = pref.getInt(RMI_SERVER_PORT_PROP, RMI_SERVER_PORT_DEFAULT);
            Registry registry = LocateRegistry.getRegistry(port);

            registry.bind(name, stub);
            System.out.println("Shell Server bound to RMI registry.");
            isEnabled = true;
        } catch (Exception ex) {
            isEnabled = false;
            String errorMsg = "Shell server could not be bound to RMI registry";
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, errorMsg, ex);
        }
    }

    @Override
    public String connect(final String client) throws RemoteException {

        final Connection c = new Connection(client);

        if (connectionModel.connections.contains(c) == false) {

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ShellTopComponent shell = ShellTopComponent.findInstance(c.shellId);

                    if (shell == null) {
                        shell = new ShellTopComponent(c.shellId);
                        shell.open();
                        shell.requestActive();
                    }
                    c.setDoc(shell.getShellDocument());
                    connectionModel.connections.add(c);
                    connectionModel.update();
                }
            });

            // wait for EDT to register the connection

            while (!connectionModel.connections.contains(c)) {
                try {

                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }

            }

        }


        return client;
    }

    @Override
    public void disconnect(String client) throws RemoteException {

        Connection cc = new Connection(client);
        if (connectionModel.connections.contains(cc)) {
            connectionModel.connections.remove(cc);
            connectionModel.update();

        }
    }

    @Override
    public int command(String client, String cmd) throws RemoteException {
        return command(client, cmd, false, true);
    }

    @Override
    public int command(String client, String cmd, boolean background, boolean waitTillDone) throws RemoteException {
        Connection cc = new Connection(client);

        if (connectionModel.connections.contains(cc)) {
            Connection c = connectionModel.connections.get(connectionModel.connections.indexOf(cc));
            return c.doc.command(cmd, background, waitTillDone);
        } else {
            throw new RemoteException("Client '" + client + "' not registered on server. Connect first.");
        }
    }

    @Override
    public boolean commandDone(String client, int pid) throws RemoteException {
        Connection cc = new Connection(client);
        if (connectionModel.connections.contains(cc)) {
            //Connection c = connectionModel.connections.get( connectionModel.connections.indexOf(client));
            return ShellProcess.isDone(pid);
        } else {
            throw new RemoteException("Client '" + client + "' not registered on server. Connect first.");
        }

    }

    @Override
    public Object getAnswer(String client) throws RemoteException {
        Connection cc = new Connection(client);
        if (connectionModel.connections.contains(cc)) {
            Connection c = connectionModel.connections.get(connectionModel.connections.indexOf(cc));
            try {
                Object answer = c.doc.getAnswer();
                String cls = answer.getClass().getName();
                if (answer.getClass().getName().equals("sun.org.mozilla.javascript.internal.NativeJavaObject")) {
                    answer = answer.toString();
                }
                return answer;
            } catch (Exception ex) {
                throw new RemoteException(ex.getMessage());
            }
        } else {
            throw new RemoteException("Client '" + client + "' not registered on server. Connect first.");
        }
    }

    @Override
    public String getVersion() throws RemoteException {
        //TODO: get this from the module spec version.
        return "1.0.1";
    }
}
