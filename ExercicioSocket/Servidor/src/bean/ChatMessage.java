/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Marilainny Martins da Silva;
 * Class package Servidor;
 * has atributes (name,texte,nameReserved);
 * has atribute setOnlines (List name users online);
 * has atribute action of type Action; 
 * methods get and set of atributes (name,texte,nameReserved, setOnlines, action);
 * method enum Action (CONNECT, DISCONNECT, SEND_ONE, SEND_ALL, USERS_ONLINE);
 * 
 */

//Working with object of class ChatMessage contains (name, text, nameReserved, setOnlines) and class Implements serializable (salve last status of object).
public class ChatMessage implements Serializable {
    
//Class atributes
    
    private String name; //this is where it contains the client's name.
    private String text; //this is where it contains the message's text.
    private String nameReserved; //this will the name of a client than will receive a reserved type message.
    private Set<String> setOnlines = new HashSet<String>(); //this is a constructor with a list of the names everybody is online (connected online service);
    private Action action; //this is one action ENUM includes actions (CONNECT, DISCONNECT, SEND_ONE, SEND_ALL, USERS_ONLINE);

// Get and Set methodes.
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameReserved() {
        return nameReserved;
    }

    public void setNameReserved(String nameReserved) {
        this.nameReserved = nameReserved;
    }

    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
    
    
    public enum Action {
        
        CONNECT, DISCONNECT, SEND_ONE, SEND_ALL, USERS_ONLINE
        
    } // CONNECT: action connection;
      // DISCONNECT: action to finesh a connection (leave the chat);
      // SEND_ONE: action to send a reserved message;
      // SEND_ALL: action to send a message everybody on chat;
      // USERS_ONLINE: action to update users list;
}
