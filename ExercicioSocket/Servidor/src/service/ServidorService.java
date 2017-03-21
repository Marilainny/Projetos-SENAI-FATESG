/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.ChatMessage;
import bean.ChatMessage.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marilainny Martins da Silva class ServidorService has atributes
 * (serverSocket, socket, maponlines); has ServidorService method inicialize
 * connection port 9000, while connection is true,accept new connection,and
 * makes initialization new Thread socket; has implementation private class
 * ListenerSocket where it has the atributes output and input. ListenerSocket
 * class has the method inicialization object output and input,urser online
 * connected. has run method get the action urser and it makes validation if
 * action is (CONNECT, DISCONNECT, SEND_ONE, SEND_ALL, USERS_ONLINE).
 *
 */
public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    private HashMap<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();// It's a list where is add all users online on chat (connected)to send message everybody online (connected). 

    public ServidorService() {// method constructor of class ServidorService.
        try {// block try and catch exception.
            serverSocket = new ServerSocket(9000); // inicialization a serverSocket intance It's using port connection 9000;
            System.out.println("Servidor on!");

            while (true) {//keep the connection always waiting a new connection. 
                socket = serverSocket.accept(); //inicialization socket from the object serverSocket and method accept;
                new Thread(new ListenerSocket(socket)).start();// Inicialization to start the Theard;
            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class ListenerSocket implements Runnable {//Create class ListenerScoket with implemention Runnablet for threads 

        private ObjectOutputStream output;//variable execute send message servidor; 
        private ObjectInputStream input;//variable execute receipt message servidor;

        public ListenerSocket(Socket socket) {//Listener contructed method
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());//inicialization 
                this.input = new ObjectInputStream(socket.getInputStream());//inicialization 
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void run() {
            ChatMessage message = null; //create variable ChatMessage
            try {

                while ((message = (ChatMessage) input.readObject()) != null) {//Receve message sent for urser
                    Action action = message.getAction();

                    if (action.equals(Action.CONNECT)) { //validacion type message sent for urser (connection request);
                        boolean isConnect = connect(message, output);  //user connecting
                        if (isConnect) {
                            mapOnlines.put(message.getName(), output);//When users is connected He is include in the list users online
                            sendOnlines();//call name list when each user makes the connection.
                        }
                    } else if (action.equals(Action.DISCONNECT)) {//validacion type message sent for urser (disconnection request leave the char);
                        disconnect(message, output);// command disconnect service.
                        sendOnlines();//update name list when each user makes the connection.
                        return;//Forces the connection output

                    } else if (action.equals(Action.SEND_ALL)) {//validacion type message sent for urser (request to send mensagem everybody on-line);
                        sendAll(message);//command call 

                    } else if (action.equals(Action.SEND_ONE)) {//validacion type message sent for urser (request to send mensagem only one person on-line);
                        sendOne(message);

                    }
                }
            } catch (IOException ex) {
                disconnect(message, output);//case user use the close windowns connection closed.
                sendOnlines();//update name list when each user makes the connection.
                System.out.println(message.getName() + " deixou o chat!\n");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean connect(ChatMessage message, ObjectOutputStream output) {//command to connect;
        if (mapOnlines.size() == 0) {//this time no one user is connected on service.
            message.setText("YES");// Waring urser he is connect;
            send(message, output); //Send message
            return true;
        }

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {//Scroll throught the list
            if (kv.getKey().equals(message.getName())) {//validation name equal text typed
                message.setText("NO"); //if equal show message "NO"
                send(message, output); // send message urser no possible connection because other urser name equal.
                return false;
            } else {
                message.setText("YES");// if name list different name typed, send message "YES"
                send(message, output);//send message
                return true;
            }
        }
        return false;//excption

    }

    private void disconnect(ChatMessage message, ObjectOutputStream output) {
        mapOnlines.remove(message.getName());//remove onlines list name user close connection;
        message.setText("Até Logo!\n");//alert saying user disconnected chat
        message.setAction(Action.SEND_ONE);//send a mensagem everybody online go out user disconnected.
        sendAll(message);//command send message.
        System.out.println("User " + message.getName() + " deixou o chat.\n");

    }

    private void send(ChatMessage message, ObjectOutputStream output) {
        try {
            output.writeObject(message);//command to send message sendOne;
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendOne(ChatMessage message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (kv.getKey().equals(message.getNameReserved())) {
                try {
                    kv.getValue().writeObject(message);//command to send message sendOne;
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void sendAll(ChatMessage message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (!kv.getKey().equals(message.getName())) {
                message.setAction(Action.SEND_ONE);
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void sendOnlines() {//It's used to update list user online
        Set<String> setNames = new HashSet<String>();
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            setNames.add(kv.getKey());
        }
        ChatMessage message = new ChatMessage();//intancia of class ChatMessage;
        message.setAction(Action.USERS_ONLINE);//action user online
        message.setSetOnlines(setNames);//list type set send list name for users.

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            message.setName(kv.getKey());
            try {
                kv.getValue().writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
