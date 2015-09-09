/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import clientHandler.ClientHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksandar, Lukasz, Viktor
 */
public class Server
{

    private static final int portNumber = 8112;//server's port number
    private static final String localhost = "localhost";
    private static HashMap<String, ClientHandler> userList = new HashMap();//save the clients

    public static final String USER = "USER#";
    public static final String MSG = "MSG#";
    public static final String STOP = "STOP#";
    public static final String USERLIST = "USERLIST#";

    public void connect()
    {

        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(localhost, portNumber));

            while (true) {
                new Thread(new ClientHandler(serverSocket.accept(), this)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public static void main(String[] args)
    {

    }

    public void sendUserList(Socket clientSocket)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean getUserNameAndUpdateUserList(String inputLine, ClientHandler aThis)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void sendUpdatedUserList()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
