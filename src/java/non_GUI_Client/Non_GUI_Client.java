/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package non_GUI_Client;

import clientHandler.ClientHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Utils;

/**
 *
 * @author sasho
 */
public class Non_GUI_Client extends Observable {

    private Socket socket;
    private InetAddress serverAddress;
    private ServerMessageReader reader;
    private String serverMessage = "";
    private String IP;
    private int PORT;

    private void getIPAndPort() {

        try {
            BufferedReader stdIn
                    = new BufferedReader(new InputStreamReader(System.in));
 
            while (true) {
               System.out.println("Enter IP: ");
               String input = stdIn.readLine();
               if(input != null || input.trim().length() != 0){
                   this.IP = input;
                   break;
               }
            }
            while (true) {
               System.out.println("Enter PORT: ");
               String input = stdIn.readLine();
               if(input != null || input.trim().length() != 0){
                   this.PORT = Integer.parseInt(input);
                   break;
               }
            }

        } catch (IOException ex) {
            Logger.getLogger(Non_GUI_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setIPAndPort(String IP, int PORT){
        
        this.IP = IP;
        this.PORT = PORT;
    }

    public void connect() {

        try {
            serverAddress = InetAddress.getByName(IP);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Non_GUI_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket = new Socket(IP, PORT);
        } catch (IOException ex) {
            Logger.getLogger(Non_GUI_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(String message) {

        try {

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //send message to the server
            out.println(message);

        } catch (IOException ex) {
            Logger.getLogger(Non_GUI_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void readFromServer() {

        reader = new ServerMessageReader(socket);
        reader.start();

    }

    public String getServerMessage() {

        return serverMessage;
    }

    public void run() {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader stdIn
                    = new BufferedReader(new InputStreamReader(System.in));

            String fromUser;

            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(new ServerMessageReader((socket)));

            while ((fromUser = stdIn.readLine()) != null) {//read from the Server, until you reach the end of the stream

                out.println(fromUser);//send the message to the server
                if (fromUser.equals("STOP#")) {
                    executor.shutdown();
                    break;
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Non_GUI_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        Non_GUI_Client client = new Non_GUI_Client();
        client.getIPAndPort();
        client.connect();
        client.run();
    }

    //this class reads the input from the Server
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
                    
                    serverMessage = fromServer;//save the message
                    System.out.println(fromServer);//print the message from the server
                    setChanged();
                    notifyObservers(fromServer);
                }

            } catch (IOException ex) {
                Logger.getLogger(Non_GUI_Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
