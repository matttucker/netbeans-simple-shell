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
package com.sun.datastorage.shell.rmi;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Matt Tucker
 */
public class ShellBridge implements Serializable {

    RemoteShell shell;
    String clientId;

    /**
     * Makes a connection to the shell server.
     *
     * @param host  The host name of the shell server. For example, "127.0.0.1" for the local host.
     * @param port  The port number of the shell server. For example, "1099" which is the default RMI registry port.
     *
     * @throws java.rmi.RemoteException
     * @throws java.rmi.NotBoundException
     * @throws java.net.UnknownHostException
     */
    public void connect(String host, int port) throws RemoteException, NotBoundException, UnknownHostException {
        connect(host, port, 0);
    }

    /**
     * Makes a connection to the shell server.
     *
     * @param host  The host name of the shell server. For example, "127.0.0.1" for the local host.
     * @param port  The port number of the shell server. For example, "1099" which is the default RMI registry port.
     * @param shellId Use this to specify the shell you want to attach to.
     *
     * @throws java.rmi.RemoteException
     * @throws java.rmi.NotBoundException
     * @throws java.net.UnknownHostException
     */
    public void connect(String host, int port, int shellId) throws RemoteException, NotBoundException, UnknownHostException {

        String name = "ShellServer";  // has to match server name!
        Registry registry = LocateRegistry.getRegistry(host, port);
        shell = (RemoteShell) registry.lookup(name);

        clientId = InetAddress.getLocalHost().getHostName() + ":" + shellId;
        clientId = shell.connect(clientId);


        System.out.println(clientId + " connected to " + name + " " + host + ", port=" + port);
    }

    /**
     * Disconnects from the shell server.
     *
     * @throws java.rmi.RemoteException
     */
    public void disconnect() throws RemoteException {
        if (clientId != null) {
            shell.disconnect(clientId);
        } else {
            throw new RemoteException("shellBridge: Client ID is undefined.");
        }
    }

    /**
     * Send a command to the attached shell of the shell Server.  Command will be processed in the
     * shell background, and this method will return once the command has been completed.
     *
     * @param cmd  command string.
     * @return PID of command.
     * @throws java.rmi.RemoteException
     */
    public int command(String cmd) throws RemoteException {
        return command(cmd, true, true);
    }

    /**
     * Send a command to the attached shell of the shell Server.
     *
     * @param cmd Command string.
     * @param background    process command in the shell background.
     * @param waitTillDone  wait till the command has been completed before returning.
     * @return PID of command.
     * @throws java.rmi.RemoteException
     */
    public int command(String cmd, boolean background, boolean waitTillDone) throws RemoteException {
        if (clientId != null) {
            return shell.command(clientId, cmd, background, waitTillDone);
        } else {
            throw new RemoteException("shellBridge: Client ID is undefined.");
        }
    }

    /**
     * Query the shell server to see if a command process has been completed.
     *
     * @param pid process id of a command.
     * @return If the command has been completed return true, otherwise false.
     * @throws java.rmi.RemoteException
     */
    public boolean commandDone(int pid) throws RemoteException {
        if (clientId != null) {
            return shell.commandDone(clientId, pid);
        } else {
            throw new RemoteException("shellBridge: Client ID is undefined.");
        }
    }


    /**
     * Get the answer generated from the scriptengine.
     *
     * @return answer from the scriptengine
     * @throws java.rmi.RemoteException
     */
    public Object getAnswer() throws RemoteException {
        if (clientId != null) {
            return shell.getAnswer(clientId);
        } else {
            throw new RemoteException("shellBridge: Client ID is undefined.");
        }
    }

    /**
     * Get the version of the shell bridge interface.
     * @return version string
     * @throws java.rmi.RemoteException
     */
    public String getVersion() throws RemoteException {
        return shell.getVersion();
    }

}
