
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

    public void sendUserList(Socket clientSocket)
    {
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

    public boolean getUserNameAndUpdateUserList(String inputLine, ClientHandler clientHandler)
    {
        boolean status = false;
        if (inputLine.startsWith(USER)) {

            String userName = inputLine.substring(5);
            userList.put(userName, clientHandler);

            status = true;
        }
        return status;
    }

    public void sendUpdatedUserList()
    {
        String tmp = "";
        if (userList.isEmpty()) {
            tmp = "";
        } else {
            for (String value : userList.keySet()) { //adds users to the userlist (STRING)
                tmp += value + ",";
            }
        }
        String userListStr = USERLIST + tmp;;

        for (ClientHandler col : userList.values()) { //for all of the clients prints userlist
            col.print(userListStr);
        }
    }

    public void removeClientFromUserList(ClientHandler client)
    {
        userList.values().remove(client);
    }

    public void processMessage(Socket clientSocket, String inputLine)
    {
        if (inputLine.startsWith(MSG)) {
            int secondHashIndex = inputLine.indexOf("#", 4);//get the index of the second hash
            // String toWhichUsers = inputLine.substring(4, secondHashIndex);

            if (secondHashIndex == 5) {
                for (ClientHandler col : userList.values()) {
                    col.print(inputLine);
                }
            } else {
                String[] receivers = inputLine.substring(4, secondHashIndex).split(",");
                for (int i = 0; i < receivers.length; i++) {
//                    System.out.println("Receivers " + receivers[i]);
                    for (String userName : userList.keySet()) {
                        if (userName.equals(receivers[i])) {
                            userList.get(userName).print(inputLine);
//                        } else {
//                            try {
//                                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//                                out.println(receivers[i] + " does not exists");
//                            } catch (Exception e) {
//                            }
                        }
                    }
                }
            }
        } else {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Unknown command");
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args)
    {
        Server server = new Server();
        server.connect();
    }

}
