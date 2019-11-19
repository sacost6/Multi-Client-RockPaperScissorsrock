package code;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;

/*
    Message that contains a command type and player information
 */
public class Message implements Serializable {

    // This is to make sure it is the same across the two projects
    private static final long serialVersionUID = 321;

    public CommandType commandType;

    Player player;

    public ArrayList<Player> players;
    public ArrayList<String> playerNameList;

    // Create a message that contains player info
    public Message(Player player){
        commandType = CommandType.PLAYERNAME;
        this.player = player;
    }

    // Create a message to send a challenge
    public Message(Player player, CommandType commandtype) {
        this.commandType = commandtype;
        this.player = player;
    }

    // Create a message that contains all the clients in the form of an array list
    public Message(ArrayList<Player> players, ArrayList<String> playerNameList){
        commandType = CommandType.CLIENT;
        this.players = new ArrayList<>(players);
        this.playerNameList = new ArrayList<>(playerNameList);
    }


    // Creates a message containing only the command type
    // For instance when starting the server once 2 players are connected
    // Or for when removing a client from the server
    public Message(CommandType commandType){
        this.commandType = commandType;
    }

}
