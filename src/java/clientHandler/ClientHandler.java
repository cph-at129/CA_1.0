/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;

/**
 *
 * @author Aleksandar, Lukasz, Viktor
 */
public class ClientHandler implements Runnable
{

    public static final String USER = "USER#";
    public static final String MSG = "MSG#";
    public static final String STOP = "STOP#";
    public static final String USERLIST = "USERLIST#";

    private final Server server;
    private final Socket clientSocket;
    
    public ClientHandler(Socket clientSocket, Server server)
    {

        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run()
    {
        try {
            server.sendUserList(clientSocket);

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            String outputLine;
            while ((inputLine = in.readLine()) != null) { //loop to get the user name and send it to everyone
                boolean status = server.getUserNameAndUpdateUserList(inputLine, this);
                if (status) {
                    server.sendUpdatedUserList();
                    break;
                }
            }
            out.println("You are online");

            clientSocket.close();

        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
