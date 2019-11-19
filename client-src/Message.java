package code;

import java.io.Serializable;

/*
    Message that contains a commandtype and player information
 */
public class Message implements Serializable {

    // This is to make sure it is the same across the two projects
    private static final long serialVersionUID = 321;

    public Integer playerNumber;
    public Integer numPlayerPoints;
    public Sign playerSign;
    public CommandType commandType;
    public Boolean isWinner;

    // Create a message that contains player info and whether they won the ENTIRE game or not
    public Message(Player player, Boolean isWinner){
        commandType = CommandType.PLAYER;
        playerNumber = player.getNumber();
        numPlayerPoints = player.getNumberOfPoints();
        playerSign = player.getSign();
        this.isWinner = isWinner;
    }

    // Creates a message containing only the command type
    // For instance when starting the server once 2 players are connected
    // Or for when removing a client from the server
    public Message(CommandType commandType){
        this.commandType = commandType;
    }

}
