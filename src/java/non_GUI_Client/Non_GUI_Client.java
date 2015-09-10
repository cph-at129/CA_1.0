/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package non_GUI_Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import server.Server;

/**
 *
 * @author sasho
 */
public class Non_GUI_Client {

    private static String IP = "unknown";

    private String getServerAddress() {

        String ip = JOptionPane.showInputDialog("Enter IP Address of the Server:", "Welcome to the Chatter");
        return ip;
    }

    private void run() {

        Socket socket;

        try {
            socket = new Socket(IP, 8112);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader stdIn
                    = new BufferedReader(new InputStreamReader(System.in));

            String fromUser;
            ServerMessageReader reader = new ServerMessageReader(socket);

            Thread t = new Thread(reader);

            t.start();
            
            while ((fromUser = stdIn.readLine()) != null) {//read from the Server, until you reach the end of the stream

                out.println(fromUser);//send the message to the server
                
                if(!t.isAlive()){
                    break;
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Non_GUI_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        Non_GUI_Client client = new Non_GUI_Client();
        IP = client.getServerAddress();
        System.out.println("IP: " + IP);
        client.run();
    }

    private class ServerMessageReader extends Thread {

        private Socket clientSocket;

        public ServerMessageReader(Socket clientSocket) {

            this.clientSocket = clientSocket;
        }

        @Override
        public synchronized void run() {

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                String fromServer;

                while ((fromServer = in.readLine()) != null) {//read from the Server, until you reach the end of the stream

                    System.out.println(fromServer);//print the message from the server

                }
                

            } catch (IOException ex) {
                Logger.getLogger(Non_GUI_Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
