/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import clientHandler.ClientHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
public class Server {

    private static final int portNumber = 8112;//server's port number
    private static final String localhost = "localhost";
    private static HashMap<String, ClientHandler> userList = new HashMap();//save the clients

    public static final String USER = "USER#";
    public static final String MSG = "MSG#";
    public static final String STOP = "STOP#";
    public static final String USERLIST = "USERLIST#";

    public void connect() {

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

    public static void main(String[] args) {

    }

    public void sendUserList(Socket clientSocket) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String tmp = "";
            if (userList.isEmpty()) {
                tmp = "";
            } else {
                for (String value : userList.keySet()) {
                    tmp += value + ",";
                }
            }
            String outputLine = USERLIST + tmp;
            out.println(outputLine);

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean getUserNameAndUpdateUserList(String inputLine, ClientHandler clientHandler) {
        boolean status = false;
        if (inputLine.startsWith(USER)) {

            String userName = inputLine.substring(5);
            userList.put(userName, clientHandler);

            status = true;
        }
        return status;
    }

    public void sendUpdatedUserList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void removeClientFromUserList(ClientHandler aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void processMessage(Socket clientSocket, String inputLine) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
