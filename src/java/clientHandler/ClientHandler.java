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
public class ClientHandler implements Runnable {

    public static final String USER = "USER#";
    public static final String MSG = "MSG#";
    public static final String STOP = "STOP#";
    public static final String USERLIST = "USERLIST#";

    private final Server server;
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Socket getClientSocket() {

        return clientSocket;
    }

    public ClientHandler(Socket clientSocket, Server server) {

        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try {
            server.sendUserList(clientSocket);

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            String outputLine;
            while ((inputLine = in.readLine()) != null) { //loop to get the user name and send it to everyone
                
                boolean status = server.getUserNameAndUpdateUserList(inputLine, this);
                if (status) {
                    server.sendUpdatedUserList();
                    break;
                }
            }
            //  out.println("You are online");

            while ((inputLine = in.readLine()) != null) {

                Logger.getLogger(ClientHandler.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", inputLine));
                if (inputLine != null) {
                    if (inputLine.equals(STOP)) {
                        server.removeClientFromUserList(this);
                        server.sendUpdatedUserList();
                        break;
                    } else if (inputLine.startsWith(MSG) && inputLine.substring(4).contains("#") && inputLine.length() > 4) {
                        server.proccessMessage(inputLine, clientSocket, this);
                    }
                }
            }
            clientSocket.close();
            Logger.getLogger(ClientHandler.class.getName()).log(Level.INFO, "Closed a Connection");

        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void print(String output) {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println(output);

        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
