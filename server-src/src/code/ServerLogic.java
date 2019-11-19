package code;

import javafx.application.Platform;
import javafx.util.Pair;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class ServerLogic implements Runnable{

    private ServerSocket serverSocket;

    // Threads for each client
    static ArrayList<ClientThread> clientThreads = new ArrayList<>();

    //Array to hold players currently in game
    static ConcurrentHashMap<Player, Player> inGame = new ConcurrentHashMap<>();

    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<String> playerNameList = new ArrayList<>();

    Integer numClients = 0;

    Integer numPlayed = 0;

    Integer portNumber;

    public ServerLogic(Integer portNumber) throws IOException {
        this.portNumber = portNumber;
        serverSocket = new ServerSocket(portNumber);
    }

    @Override
    public void run() {
        while (true){
            try {
                final Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted a connection " + numClients);

                Player player = new Player(numClients);
                ClientThread newClientThread = new ClientThread(clientSocket, this, player);
                numClients++;

                // Update Number of Clients GUI in ServerGUI
                Platform.runLater(() -> ServerGUI.numClientsLabel.setText("Num Clients: " + numClients));

                Thread clientThread = new Thread(newClientThread);
                clientThreads.add(newClientThread);
                clientThread.start();
                ServerGUI.threads.add(clientThread);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeToAll(Message msg){
        ArrayList<ClientThread> clientThreadsWrite = new ArrayList<>(clientThreads);
        for (ClientThread clientThread :
                clientThreadsWrite) {
            clientThread.write(msg);
        }
    }

    public void closeConnection() {
        System.exit(1);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<String> getPlayerNameList() {
        return playerNameList;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    // Used to send message to the targeted player
    void write(Player player, Message msg) {
        // counter used to find location of player in clientThreads
        int counter = 0;

        //writing to targeted player
        for( Player playerr :
                players) {
            if(playerr.getName() != null && player != null && player.getName().equals(playerr.getName())) {
                clientThreads.get(counter).write(msg);
            }
            counter++;
        }
    }
}
