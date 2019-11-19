package code;

/*
    Commands the the server sends to the client
 */
public enum CommandType {
    PLAYER, START, NEXT, END, CLIENT, PLAYERNAME,
    CHALLENGE, CHALLENGED, ACCEPT, DECLINE, INGAME, PLAYING
}
